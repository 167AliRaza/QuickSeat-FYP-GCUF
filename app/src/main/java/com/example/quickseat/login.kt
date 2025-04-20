package com.example.quickseat

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException

class login : AppCompatActivity() {
    private lateinit var email_et : EditText
    private lateinit var password_et :EditText
    private lateinit var terms_cb :CheckBox
    private lateinit var signin_btn :Button
    private lateinit var auth: FirebaseAuth
    private lateinit var progressBar2: ProgressBar





    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        email_et=findViewById(R.id.email_edit_text)
        password_et=findViewById(R.id.password_edit_text)
        terms_cb=findViewById(R.id.terms_checkbox)
        signin_btn=findViewById(R.id.sign_in_button)
        auth=FirebaseAuth.getInstance()
        progressBar2=findViewById(R.id.progressBar2)
        val signup: TextView = findViewById(R.id.sign_up_btn)
        val forget: TextView = findViewById(R.id.forget_btn)
        forget.setOnClickListener {
            val intent =Intent(this ,forgetPassword::class.java)
            startActivity(intent)
            finish()

        }


        signup.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        signin_btn.setOnClickListener {
            hideKeyboard(it)
            form_validation()

        }


    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun form_validation(): Boolean {
        val email = email_et.text.toString().trim()
        val password = password_et.text.toString().trim()

            if (TextUtils.isEmpty(email)) {
                email_et.error = "Email is required"
                email_et.requestFocus()
                return false

        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            email_et.error = "Invalid email"
            email_et.requestFocus()
            return false
        }
        if (TextUtils.isEmpty(password)) {
            password_et.error = "Password is required"
            password_et.requestFocus()
            return false

        } else if (password.length < 8) {
            password_et.error = "Invalid Password."
            password_et.requestFocus()
            return false

        }
        if (!terms_cb.isChecked) {

            Toast.makeText(this, "Please accept the terms and conditions", Toast.LENGTH_SHORT)
                .show()
            return false
        }

        signIn(email_et.text.toString().trim(),password_et.text.toString())
        return true

    }

    private fun signIn(email: String, password: String) {
        progressBar2.visibility = ProgressBar.VISIBLE
        signin_btn.isEnabled = false
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                progressBar2.visibility = ProgressBar.INVISIBLE
                signin_btn.isEnabled = true
                if (task.isSuccessful) {
//                     val user = auth.currentUser
                    Toast.makeText(this, "Sign in successful!", Toast.LENGTH_SHORT).show()
                    val intent =Intent(this,homepage::class.java)
                    intent.putExtra("email",email)
                    startActivity(intent)
                    finish()

                }
                else {
                    Toast.makeText(this, "Invalid email or Password.", Toast.LENGTH_SHORT).show()

                }
            }
    }
}