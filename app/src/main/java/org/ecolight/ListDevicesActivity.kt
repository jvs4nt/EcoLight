package org.ecolight

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import org.ecolight.adapters.DevicesAdapter
import org.ecolight.models.Device

class ListDevicesActivity : AppCompatActivity() {
    private lateinit var homeButton: ImageButton
    private lateinit var menuButton: ImageButton
    private lateinit var profileButton: ImageButton
    private lateinit var goBackMenuButton: ImageButton
    private lateinit var devicesRecyclerView: RecyclerView
    private lateinit var addButton: Button

    private lateinit var database: DatabaseReference // Referência ao Firebase
    private val devicesList = mutableListOf<Device>() // Lista para armazenar os dispositivos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_list_devices)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        homeButton = findViewById(R.id.homeButton)
        menuButton = findViewById(R.id.menuButton)
        profileButton = findViewById(R.id.profileButton)
        goBackMenuButton = findViewById(R.id.goBackMenuButton)
        addButton = findViewById(R.id.addButton)

        homeButton.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        menuButton.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        profileButton.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        goBackMenuButton.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        addButton.setOnClickListener {
            startActivity(Intent(this, CreateDeviceActivity::class.java))
        }

        devicesRecyclerView = findViewById(R.id.devicesRecyclerView)
        devicesRecyclerView.layoutManager = LinearLayoutManager(this)

        database = FirebaseDatabase.getInstance().getReference("devices")

        loadDevicesFromFirebase()
    }

    private fun loadDevicesFromFirebase() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                devicesList.clear()
                for (deviceSnapshot in snapshot.children) {
                    val device = deviceSnapshot.getValue(Device::class.java)
                    device?.let {
                        devicesList.add(it)
                    }
                }
                devicesRecyclerView.adapter = DevicesAdapter(devicesList)
            }

            override fun onCancelled(error: DatabaseError) {
                error.toException().printStackTrace()
            }
        })
    }
}
