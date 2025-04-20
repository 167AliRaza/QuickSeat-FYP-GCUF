package com.example.quickseat

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.log

class add_data : AppCompatActivity() {

    private lateinit var bus_no_et :EditText
    private lateinit var bus_route_et :EditText
    private lateinit var bus_time_et :EditText
    private lateinit var bus_date_et :EditText
    private lateinit var bus_seats_et :EditText
    private lateinit var bus_add_btn :Button
    private lateinit var db:FirebaseFirestore
    private lateinit var progress_bar:ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_data)
        bus_no_et=findViewById(R.id.bus_no_et)
        bus_route_et=findViewById(R.id.bus_route_et)
        bus_seats_et=findViewById(R.id.bus_seats_et)
        bus_time_et=findViewById(R.id.bus_time_et)
        bus_date_et=findViewById(R.id.bus_date_et)
        bus_add_btn=findViewById(R.id.data_insert_btn)
        db= FirebaseFirestore.getInstance()
        progress_bar=findViewById(R.id.progressbr)
        bus_date_et.setOnClickListener{
            showDatePickerDialog()

        }
        bus_time_et.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(
                this,
                { _, hourOfDay, minuteOfHour ->
                    bus_time_et.setText("$hourOfDay:$minuteOfHour")
                },
                hour,
                minute,
                false
            )
            timePickerDialog.show()
        }


        bus_add_btn.setOnClickListener {
            hideKeyboard(it)
            val bus_no =bus_no_et.text.toString().trim()
            val bus_route =bus_route_et.text.toString().trim()
            val bus_seats =bus_seats_et.text.toString().trim()
            val bus_time =bus_time_et.text.toString().trim()
            val bus_date =bus_date_et.text.toString().trim()


            if (TextUtils.isEmpty(bus_no)) {
                bus_no_et.error = "Bus No is required"
                bus_no_et.requestFocus()

            }
            else if (TextUtils.isEmpty(bus_route)) {
                bus_route_et.error = "Bus No is required"
                bus_route_et.requestFocus()

            }
            else if (TextUtils.isEmpty(bus_seats)) {
                bus_seats_et.error = "Bus No is required"
                bus_seats_et.requestFocus()

            }
            else if (TextUtils.isEmpty(bus_time)) {
                bus_time_et.error = "Bus No is required"
                bus_time_et.requestFocus()

            }
            else if (TextUtils.isEmpty(bus_date)) {
                bus_date_et.error = "Bus No is required"
                bus_date_et.requestFocus()

            }
            else{
                addBus(bus_no,bus_time,bus_route,bus_seats,bus_date)
            }



        }


    }

    private fun addBus(busNo: String, busTime: String, busRoute: String, busSeats: String,busDate:String) {
        progress_bar.visibility=ProgressBar.VISIBLE
        val Buses =db.collection("BUSES")

        val busData = hashMapOf(
            "busNo" to busNo,
            "busTime" to busTime,
            "busSeats" to busSeats,
            "busRoute" to busRoute,
            "busDate" to busDate
        )

        Buses.document(busNo).set(busData)
            .addOnSuccessListener {
                progress_bar.visibility=ProgressBar.GONE
                bus_add_btn.isEnabled=false
                Toast.makeText(this, "Data inserted ", Toast.LENGTH_SHORT).show()
                bus_no_et.text.clear()
                bus_time_et.text.clear()
                bus_seats_et.text.clear()
                bus_route_et.text.clear()
                bus_date_et.text.clear()
                bus_add_btn.isEnabled=true

                addSeats(busNo,busSeats.toInt())
                finish()


            }
            .addOnFailureListener {
                progress_bar.visibility=ProgressBar.GONE
                bus_add_btn.isEnabled=false

                Toast.makeText(this, "Error:try again ", Toast.LENGTH_SHORT).show()
                bus_no_et.text.clear()
                bus_time_et.text.clear()
                bus_seats_et.text.clear()
                bus_route_et.text.clear()
                bus_date_et.text.clear()
                bus_add_btn.isEnabled=true
                finish()
             }
    }

private fun addSeats(busNo: String, totalSeats: Int) {

    for (seatNumber in 1..totalSeats) {
            val seatData = hashMapOf(
                "seatNumber" to seatNumber.toString(),
                "isBooked" to false,
                "bookedBy" to ""
            )

            db.collection("BUSES").document(busNo)
                .collection("SEATS").document(seatNumber.toString())
                .set(seatData)
                .addOnSuccessListener {
                     Log.d("Firestore", "Seat $seatNumber added!")
                }
                .addOnFailureListener { e ->

                    Log.e("Firestore", "Error adding seat $seatNumber", e)
                }
        }
    }


    private fun hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = "$dayOfMonth/${month + 1}/$year"
                bus_date_et.setText(selectedDate)
            },
            year,
            month,
            dayOfMonth
        )

        // Set minimum date to today
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()

        // Set maximum date to 7 days from today
        val maxCalendar = Calendar.getInstance()
        maxCalendar.add(Calendar.DAY_OF_YEAR, 7)
        datePickerDialog.datePicker.maxDate = maxCalendar.timeInMillis

        datePickerDialog.show()
    }



    }
