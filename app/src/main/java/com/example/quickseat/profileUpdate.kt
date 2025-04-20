package com.example.quickseat
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso


class profileUpdate : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var uploadButton: Button
    private lateinit var chooseImageButton: Button

    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null

    private lateinit var storageReference: StorageReference
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_update)

        imageView = findViewById(R.id.imageView)
        uploadButton = findViewById(R.id.uploadButton)
        chooseImageButton = findViewById(R.id.chooseImageButton)

        storageReference = FirebaseStorage.getInstance().reference.child("images")
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        loadExistingImage()
        chooseImageButton.setOnClickListener {
            openFileChooser()
        }

        uploadButton.setOnClickListener {
            uploadImage()
        }


    }
    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            Picasso.get().load(imageUri).into(imageView)
        }
    }

    private fun uploadImage() {
        if (imageUri != null) {
            val fileReference = storageReference.child(
                System.currentTimeMillis().toString() + "." + getFileExtension(imageUri!!)
            )

            fileReference.putFile(imageUri!!)
                .addOnSuccessListener {
//                        taskSnapshot ->
                    fileReference.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        saveImageUrlToFirestore(imageUrl)
                    }
                    Toast.makeText(this, "Upload successful", Toast.LENGTH_SHORT).show()

                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show()
        }
    }



    private fun getFileExtension(uri: Uri): String? {
        val contentResolver = contentResolver
        val mimeTypeMap = android.webkit.MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    private fun saveImageUrlToFirestore(imageUrl: String) {
        val email = auth.currentUser?.email
        if (email != null) {
            val userDocument = firestore.collection("USERS").document(email)
            userDocument.update("profileImageUrl", imageUrl)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile image URL saved", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to save profile image URL: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
        else
        {
            Toast.makeText(this, "No uid found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadExistingImage() {
        val email = auth.currentUser?.email
        if (email != null) {
            val userDocument = firestore.collection("users").document(email)
            userDocument.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val imageUrl = documentSnapshot.getString("profileImageUrl")
                        if (imageUrl != null && imageUrl.isNotEmpty()) {
                            Picasso.get().load(imageUrl).into(imageView)
                        }
                    }
                    else{                    Toast.makeText(this, "Failed to load existing image", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to load existing image: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

}