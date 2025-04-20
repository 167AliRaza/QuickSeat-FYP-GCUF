package com.example.quickseat

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings.System.putString
import android.util.Log
import android.view.View
import android.widget.Adapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.GravityCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.squareup.picasso.Picasso

open class homepage : AppCompatActivity() {

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var auth: FirebaseAuth
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var db:FirebaseFirestore
    protected lateinit var recyclerView: RecyclerView
    private lateinit var busAdapter: BusAdapter
    private lateinit var progressbar: ProgressBar
    private lateinit var loadingText: TextView
    private lateinit var imageView: ImageView
    private lateinit var noBusesTextView: TextView
    private val busList = mutableListOf<DataModel>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)
        auth = FirebaseAuth.getInstance()
        loadingText = findViewById(R.id.loadingTextView)
        progressbar = findViewById(R.id.progressbar)
        noBusesTextView = findViewById(R.id.noBusesTextView)
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        toolbar = findViewById(R.id.toolbar)


        setSupportActionBar(toolbar)
        check_user_staus()
        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        setupSwipeRefresh()
        fetchBuses()
        val headerView = navigationView.getHeaderView(0)
        imageView = headerView.findViewById(R.id.nav_header_image)
        imageView.setOnClickListener {
            val intent = Intent(this, profileUpdate::class.java)
            startActivity(intent)
        }


    }

    protected fun setupSwipeRefresh() {
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            fetchBuses()
        }
    }

    private fun fetchBuses() {


        progressbar.visibility = View.VISIBLE
        loadingText.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        noBusesTextView.visibility = View.GONE

        busList.clear()

        db= FirebaseFirestore.getInstance()

        db.collection("BUSES").get()
            .addOnSuccessListener { result ->
                swipeRefreshLayout.isRefreshing = false
                for (document in result) {
                    val bus = document.toObject(DataModel::class.java)
                    busList.add(bus)
                }

                // Setup Adapter with Click Listener
                busAdapter = BusAdapter(busList, object : BusAdapter.OnItemClickListener {
                     override fun onItemClick(bus: DataModel) {
                         val intent = Intent(this@homepage, seatDetails::class.java).apply {
                            putExtra("busID", bus.busNo)
                            putExtra("busTime", bus.busTime)
                             putExtra("busDate", bus.busDate)
//                            putExtra("route", bus.busRoute)
//                            putExtra("seats_available", bus.busSeats)
                        }
                        startActivity(intent)
                    }
                })
                recyclerView.adapter = busAdapter

                if (busList.isEmpty()) {
                    noBusesTextView.visibility = View.VISIBLE
                }

                // Hide progress bar and show RecyclerView
                progressbar.visibility = View.GONE
                loadingText.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
            .addOnFailureListener {
                swipeRefreshLayout.isRefreshing = false
                progressbar.visibility = View.GONE
                loadingText.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE

                Toast.makeText(this, "Error fetching buses", Toast.LENGTH_SHORT).show()
            }





    }


    private fun check_user_staus( ) {
        val sharedPref = getSharedPreferences(packageName, Context.MODE_PRIVATE) ?: return
        val isLogin =sharedPref.getString("Email","1")
        navigationView.setNavigationItemSelectedListener { menuItem ->
            // Handle menu item clicks here
            when (menuItem.itemId) {
                R.id.nav_home ->
                {
                    val intent = Intent(this, homepage::class.java)
                    startActivity(intent)                }
                R.id.nav_settings ->
                {
                    val intent = Intent(this, admin_view::class.java)
                    startActivity(intent)

                                     }
                R.id.nav_logout ->
                {
                    sharedPref.edit().remove("Email").apply()
                    val intent = Intent(this, login::class.java)
                    startActivity(intent)
                    finish()

                }

            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true // Return true to indicate that the item click was handled
        }

        if (isLogin == "1") {
            val email = intent.getStringExtra("email")

            if (email != null) {
                setText(email)
                with(sharedPref.edit()) {
                    putString("Email", email)
                    apply()
                }
            } else {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        } else {
            setText(isLogin)
        }


    }

    private fun setText(email: String?) {

        val headerView = navigationView.getHeaderView(0)
        val userName = headerView.findViewById<TextView>(R.id.nav_header_name)
        val userEmail = headerView.findViewById<TextView>(R.id.nav_header_email)
        val userRollno = headerView.findViewById<TextView>(R.id.nav_header_rollno)

        val profileImage = headerView.findViewById<ImageView>(R.id.nav_header_image)
        db= FirebaseFirestore.getInstance()
        if (email!=null)
        {
            db.collection("USERS").document(email).get()
                .addOnSuccessListener { task->
                    userName.text=task.get("name").toString()
                   userEmail.text=task.get("email").toString()
                    userRollno.text=task.get("rollNo").toString()


                }
        }




    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
    private fun loadExistingImage() {
        db= FirebaseFirestore.getInstance()

        val email = auth.currentUser?.email
        if (email != null) {
            val userDocument = db.collection("users").document(email)
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