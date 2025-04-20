package com.example.quickseat

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.firebase.firestore.FirebaseFirestore

class admin_view : homepage() {

    private lateinit var add_btn:Button
    private lateinit var delete_btn:ImageButton
    private lateinit var cardView: CardView
    private lateinit var busno_et:EditText
    private lateinit var del_btn:Button
    private lateinit var cross_btn:Button

    private lateinit var db:FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentFrameLayout = findViewById<FrameLayout>(R.id.content_frame)
        layoutInflater.inflate(R.layout.activity_admin_view, contentFrameLayout)
        add_btn=findViewById(R.id.add_button)
        delete_btn=findViewById(R.id.delete_button)
        busno_et=findViewById(R.id.busNoeditText)
        del_btn=findViewById(R.id.delbutton)
        cardView=findViewById(R.id.cardView)
        db= FirebaseFirestore.getInstance()
        cross_btn=findViewById(R.id.crossbtn)
        delete_btn.setOnClickListener {

      cardView.visibility=View.VISIBLE

            cross_btn.setOnClickListener {
                cardView.visibility=View.GONE
            }
            del_btn.setOnClickListener{
                val busNo=busno_et.text
                if (busNo.isEmpty()) {
                    busno_et.error = "Bus No required"
                    busno_et.requestFocus()
                }
                else{
                    deleteBus(busNo.toString())
                    cardView.visibility=View.GONE
                    busno_et.text.clear()


                }

            }




        }

        add_btn.setOnClickListener {

            val intent = Intent(this, add_data::class.java)
            startActivity(intent)

        }
    }

    private fun deleteBus(documentId: String) {
        val documentRef = db.collection("BUSES").document(documentId)
        val seatsRef = documentRef.collection("SEATS")
        documentRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                seatsRef.get()
                    .addOnSuccessListener { seatDocuments ->
                        val batch = db.batch()
                        // Step 2: Delete each seat document
                        for (seat in seatDocuments) {
                            batch.delete(seat.reference)
                        }
                        // Step 3: Commit batch deletion of seats
                        batch.commit()
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Error retrieving seats for bus ", e)
                    }


                documentRef.delete()
                    .addOnSuccessListener {
                        setupSwipeRefresh()
                        Toast.makeText(this, "Deleted Successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to delete bus${e}", Toast.LENGTH_SHORT).show()
                    }
            }
        else {
                Toast.makeText(this, "Bus doesn't exist", Toast.LENGTH_SHORT).show()
            }


       }

    }
}