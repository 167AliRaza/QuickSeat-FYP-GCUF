package com.example.quickseat

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Adapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.w3c.dom.Text

class seatDetails : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var seatAdapter: SeatAdapter
    private lateinit var busId: String
    private lateinit var busTime: String
    private lateinit var busDate: String

    private lateinit var db: FirebaseFirestore
    private lateinit var tv:TextView
    private lateinit var tvBack:TextView
    private lateinit var book:Button
    private lateinit var auth:FirebaseAuth
    private var selectedSeat: String?=null
    private var seatList: MutableList<SeatModel> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seat_details)
        busId = intent.getStringExtra("busID")?: ""
        busTime = intent.getStringExtra("busTime")?: ""
        busDate = intent.getStringExtra("busDate")?: ""

        tv=findViewById(R.id.textView1)
        tv.text=busId
        tvBack=findViewById(R.id.textViewBack)
        book=findViewById(R.id.seatbook_button)
        auth= FirebaseAuth.getInstance()
        val userEmail = auth.currentUser?.email ?: ""


        tvBack.setOnClickListener {
            finish()
        }
        recyclerView = findViewById(R.id.recyclerViewSeats)
        recyclerView.layoutManager = GridLayoutManager(this, 6)
        lifecycleScope.launch {
             seatList = getSeatsForBus(busId)

            seatAdapter = SeatAdapter(seatList){ seat ->
                selectedSeat = seat.seatNumber // Store selected seat
            }
            recyclerView.adapter = seatAdapter

        }
        book.setOnClickListener {

//            if (selectedSeat == null) {
//                Toast.makeText(this, "Please select a seat first!", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//            lifecycleScope.launch {
//                if (checkUserBooking(userEmail, busId, busDate, busTime)) {
//                    Toast.makeText(this@seatDetails, "You have already booked a seat for this bus.", Toast.LENGTH_SHORT).show()
//                } else {
//                    bookSeatAndUpdateAdapter(busId, selectedSeat!!, userEmail)
//                }
//            }
            selectedSeat?.let { seat ->
                lifecycleScope.launch {
                    if (!checkUserBooking(userEmail, busId, busDate, busTime)) {
                        bookSeatAndUpdateAdapter(busId, seat, userEmail)
                    } else {
                        Toast.makeText(this@seatDetails, "You have already booked a seat for this bus.", Toast.LENGTH_SHORT).show()
                    }
                }
            } ?: Toast.makeText(this, "Please select a seat first!", Toast.LENGTH_SHORT).show()





        }






    }
    private fun bookSeatAndUpdateAdapter(busId: String, seatNumber: String,userEmail:String) {
        bookSeat(this, busId, seatNumber,userEmail){ success ->
            if (success) {

                lifecycleScope.launch {

                    val bookingInfo = BookingInfo(busId, seatNumber, busDate, busTime,userEmail)
                    storeUserBooking(bookingInfo)
                }
                // Find the seat in your seatList and update its isBooked property
                val updatedSeatIndex = seatList.indexOfFirst { it.seatNumber == seatNumber }
                if (updatedSeatIndex != -1) {
                    seatList[updatedSeatIndex].isBooked = true // Update the local data
                    seatAdapter.notifyItemChanged(updatedSeatIndex) // Notify adapter
                }
            } else {
                // Handle booking failure
                Toast.makeText(this, "Error booking", Toast.LENGTH_SHORT).show()
            }
        }

    }



    suspend fun getSeatsForBus(busId: String): MutableList<SeatModel> {
    val db = FirebaseFirestore.getInstance()
    val seatList = mutableListOf<SeatModel>()

    return try {
        val result = db.collection("BUSES")
            .document(busId)
            .collection("SEATS")
            .get()
            .await()  // Wait for Firestore response

        for (document in result) {
            val seatNumber = document.id
            val isBooked = document.getBoolean("isBooked") ?: false
            val bookedBy = document.getString("bookedBy") ?:""

            seatList.add(SeatModel(seatNumber, isBooked,bookedBy))
            Log.d("SeatFetch", "Seat: $seatNumber, Booked: $isBooked book by: $bookedBy")


//            val seat = document.toObject(SeatModel::class.java)
//            seatList.add(seat)
        }
        seatList.sortBy { it.seatNumber.toIntOrNull() ?: Int.MAX_VALUE }
        seatList
    } catch (e: Exception) {
        Log.e("Firestore", "Error fetching seats", e)
        seatList// Return empty list on error
    }
}


    fun bookSeat(context: Context, busId: String, seatNumber: String,userEmail: String, callback: (Boolean) -> Unit)   {
        val db = FirebaseFirestore.getInstance()
        val seatRef = db.collection("BUSES")
            .document(busId)
            .collection("SEATS")
            .document(seatNumber)

        seatRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val isBooked = document.getBoolean("isBooked") ?: false
                if (!isBooked) {
                    seatRef.update(
                        "isBooked", true,
                        "bookedBy", userEmail
                    ).addOnSuccessListener {
                        Toast.makeText(context, "Seat Booked Successfuly ", Toast.LENGTH_SHORT).show()
                        callback(true)
                    }.addOnFailureListener { e ->
                        Toast.makeText(context, "Error booking seat $seatNumber ${e}", Toast.LENGTH_SHORT).show()
                        callback(false)
                    }
                } else {
                    Toast.makeText(context, "Seat $seatNumber is already booked", Toast.LENGTH_SHORT).show()
                    callback(false)

                }
            } else {
                Toast.makeText(context, "Seat $seatNumber does not exist", Toast.LENGTH_SHORT).show()
                callback(false)

            }
        }.addOnFailureListener{e->
            Toast.makeText(context, "Error getting seat information${e}", Toast.LENGTH_SHORT).show()
            callback(false)

        }
    }

//private fun checkUserBooking(context: Context, busId: String, busDate: String, busTime: String): Boolean {
//    val prefs: SharedPreferences = context.getSharedPreferences("user_bookings", Context.MODE_PRIVATE)
//    val key = "${busId}_${busDate}_${busTime}"
//    val bookingJson = prefs.getString(key, null)
//    if (bookingJson != null) {
//        val bookingInfo = Gson().fromJson(bookingJson, BookingInfo::class.java)
//        return bookingInfo.busDate == busDate && bookingInfo.busTime == busTime
//    }
//    return false
//}

//    private fun storeUserBooking(context: Context, bookingInfo: BookingInfo) {
//        val prefs: SharedPreferences = context.getSharedPreferences("user_bookings", Context.MODE_PRIVATE)
//        val key = "${bookingInfo.busId}_${bookingInfo.busDate}_${bookingInfo.busTime}"
//        val bookingJson = Gson().toJson(bookingInfo)
//        prefs.edit().putString(key, bookingJson).apply()
//    }
    private suspend fun checkUserBooking(userId: String, busId: String, busDate: String, busTime: String): Boolean {
        return try {
            val db = FirebaseFirestore.getInstance()
            val document = db.collection("USERS")
                .document(userId)
                .collection("BOOKINGS")
                .document("${busId}")
                .get()
                .await()

            if (document.exists()) {
                // Get the stored values from the document
                val storedDate = document.getString("busDate")
                val storedTime = document.getString("busTime")

                // Return true only if both date and time match
                storedDate == busDate && storedTime == busTime
            } else {
                false
            }


        } catch (e: Exception) {
            // Handle errors appropriately (e.g., log, show a message)
            e.printStackTrace()
            false
        }
    }


    private suspend fun storeUserBooking(bookingInfo: BookingInfo) {
        try {
            val db = FirebaseFirestore.getInstance()
            val bookingMap = hashMapOf(
                "busId" to bookingInfo.busId,
                "busDate" to bookingInfo.busDate,
                "busTime" to bookingInfo.busTime,
                "seatNumber" to bookingInfo.seatNumber,
                "userEmail" to bookingInfo.userEmail,
            )

            db.collection("USERS")
                .document(bookingInfo.userEmail)
                .collection("BOOKINGS")
                .document("${bookingInfo.busId}")
                .set(bookingMap)
                .await()

        } catch (e: Exception) {
            // Handle errors appropriately
            e.printStackTrace()
        }
    }

}