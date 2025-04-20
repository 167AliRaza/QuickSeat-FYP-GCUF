package com.example.quickseat

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
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
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var email_et :EditText
    private lateinit var password_et :EditText
    private lateinit var c_password_et :EditText
    private lateinit var roll_no_et :EditText
    private lateinit var sign_up_btn :Button
    private lateinit var terms_cb :CheckBox
    private lateinit var auth: FirebaseAuth
    private lateinit var db:FirebaseFirestore
    private lateinit var progressBar:ProgressBar




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        email_et=findViewById (R.id.email_edit_text)
        password_et =findViewById (R.id.password_edit_text)
        c_password_et =findViewById(R.id.confirm_password_edit_text)
        roll_no_et =findViewById(R.id.rollNo_edit_text)
        sign_up_btn= findViewById(R.id.sign_up_button)
        terms_cb=findViewById(R.id.terms_checkbox)
        auth = FirebaseAuth.getInstance()
        db= FirebaseFirestore.getInstance()
        progressBar = findViewById(R.id.progressBar) // Add progressbar to your layout.
        progressBar.visibility = ProgressBar.INVISIBLE
        sign_up_btn.setOnClickListener {

            hideKeyboard(it)
            form_validation()

        }

        val signin: TextView = findViewById(R.id.sign_in_btn)
        signin.setOnClickListener {
            val intent = Intent(this, login::class.java)
            startActivity(intent)
            finish()
        }







    }

    private fun signUp(email: String, password: String,name:String) {
        progressBar.visibility = ProgressBar.VISIBLE // Show progress bar
        sign_up_btn.isEnabled = false //disable button
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                progressBar.visibility = ProgressBar.INVISIBLE // Hide progress bar
                sign_up_btn.isEnabled = true
                if (task.isSuccessful) {
                    val Users =db.collection("USERS")

                    val user = hashMapOf(
                        "name" to name,
                        "email" to email,
                        "rollNo" to roll_no_et.text.toString()
                    )
                    Users.document(email).set(user)
                    val intent = Intent(this, homepage::class.java)
                    intent.putExtra("email",email)
                    startActivity(intent)
                    finish()

                }
                else {

                        if (task.exception is FirebaseAuthException) {
                            val errorCode = (task.exception as FirebaseAuthException).errorCode
                            when (errorCode) {
                                "ERROR_INVALID_EMAIL" -> {
                                    Toast.makeText(
                                        this,
                                        "Invalid email format.",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }

                                "ERROR_WEAK_PASSWORD" -> {
                                    Toast.makeText(
                                        this,
                                        "Password is too weak. Please choose a stronger one.",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }

                                "ERROR_EMAIL_ALREADY_IN_USE" -> {
                                    Toast.makeText(
                                        this,
                                        "User already exist. Please Try To Sign in.",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }

                                else -> {
                                    Toast.makeText(
                                        this,
                                        "Authentication failed. Please try again later.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }

                }
            }

        }
        private fun form_validation(): Boolean {
            val email = email_et.text.toString().trim()
            val password = password_et.text.toString().trim()
            val confirmPassword = c_password_et.text.toString().trim()
            val rollNo = roll_no_et.text.toString().trim()

            if (TextUtils.isEmpty(email)) {
                email_et.error = "Email is required"
                email_et.requestFocus()
                return false

            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                email_et.error = "Invalid email format"
                email_et.requestFocus()
                return false
            }
            if (TextUtils.isEmpty(password)) {
                password_et.error = "Password is required"
                password_et.requestFocus()
                return false

            } else if (password.length < 8) {
                password_et.error = "Password must be at least 8 characters"
                password_et.requestFocus()
                return false

            }

            if (TextUtils.isEmpty(confirmPassword)) {
                c_password_et.error = "Confirm password is required"
                c_password_et.requestFocus()
                return false

            } else if (password != confirmPassword) {
                c_password_et.error = "Passwords do not match"
                c_password_et.requestFocus()
                return false

            }

            if (TextUtils.isEmpty(rollNo)) {
                roll_no_et.error = "Roll No is required"
                roll_no_et.requestFocus()
                return false
            }

            if (!terms_cb.isChecked) {

                Toast.makeText(this, "Please accept the terms and conditions", Toast.LENGTH_SHORT)
                    .show()
                return false
            }


            verify_user(rollNo.toInt())
            return true

        }


        private fun verify_user(userId: Int) {
            progressBar.visibility = ProgressBar.VISIBLE // Show progress bar
            sign_up_btn.isEnabled = false //disable button


            // Get the Retrofit instance
            val apiService = api_interface.RetrofitClient.instance

            // Create the call object for the API request
            val call: Call<ApiResponse> = apiService.authUser(userId)

            // Execute the call asynchronously
            call.enqueue(object : Callback<ApiResponse> {
                override fun onResponse(
                    call: Call<ApiResponse>,
                    response: Response<ApiResponse>
                ) {
                    progressBar.visibility = ProgressBar.INVISIBLE // Hide progress bar
                    sign_up_btn.isEnabled = true //enable button
                    if (response.isSuccessful) {
                        // Handle successful response
                        val apiResponse = response.body()
                        if (apiResponse?.status == "success") {
                            // Access the user data from the response
                            val userData = apiResponse.data
                            val email_text = email_et.text.toString().trim().lowercase()
                            if (userData.email == email_text) {

                                signUp(email_text, password_et.text.toString().trim(),userData.name)

                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "Email didn't match with your Roll No ",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }

                        } else {
                            roll_no_et.error = "Roll No is Invalid"
                            roll_no_et.requestFocus()

                            // Handle case where status is not success

                        }
                    } else {
                        // Handle failure response
                        roll_no_et.error = "Roll No is Invalid"
                        roll_no_et.requestFocus()
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    // Handle failure
                    progressBar.visibility = ProgressBar.INVISIBLE // Hide progress bar
                    sign_up_btn.isEnabled = true //enable button
                    Log.e("API Error", "Failure: ${t.message}")
                    Toast.makeText(
                        applicationContext,
                        "Error: Please Try Again Later",
                        Toast.LENGTH_LONG
                    ).show()

                }
            })
        }

      private fun hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
     }

}


