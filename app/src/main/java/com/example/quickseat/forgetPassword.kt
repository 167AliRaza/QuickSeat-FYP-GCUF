package com.example.quickseat

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class forgetPassword : AppCompatActivity() {
    private lateinit var email_et : EditText
    private lateinit var reset_btn : Button
    private lateinit var signin_btn : TextView
    private lateinit var contact_btn : TextView
    private lateinit var auth: FirebaseAuth





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)

        email_et=findViewById(R.id.email_edit_text)
        reset_btn=findViewById(R.id.reset_button)
        signin_btn=findViewById(R.id.sign_in_btn)
        reset_btn=findViewById(R.id.reset_button)
        contact_btn=findViewById(R.id.contact_btn)
        auth= FirebaseAuth.getInstance()
        contact_btn.setOnClickListener {
            val phoneNo="+923199252730"
            val uri = Uri.parse("https://api.whatsapp.com/send?phone=$phoneNo")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
        signin_btn.setOnClickListener {
            val intent=Intent(this,login::class.java)
            startActivity(intent)
            finish()
        }

        reset_btn.setOnClickListener {
            val email=email_et.text.toString().trim()
            if (TextUtils.isEmpty(email)) {
                email_et.error = "Email is required"
                email_et.requestFocus()

            }
            else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                email_et.error = "Invalid email"
                email_et.requestFocus()
            }
            else {
                auth.sendPasswordResetEmail(email)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Please Check Your Email", Toast.LENGTH_SHORT).show()
                        reset_btn.isEnabled=false

                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Email didn't exist", Toast.LENGTH_SHORT).show()
                        reset_btn.isEnabled=false

                    }


            }

        }

    }
}