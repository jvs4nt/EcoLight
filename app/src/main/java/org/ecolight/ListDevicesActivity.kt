package org.ecolight

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import org.ecolight.adapters.DevicesAdapter
import org.ecolight.api.RetrofitClient
import org.ecolight.models.Device

class ListDevicesActivity : AppCompatActivity() {
    private lateinit var homeButton: ImageButton
    private lateinit var menuButton: ImageButton
    private lateinit var profileButton: ImageButton
    private lateinit var goBackMenuButton: ImageButton
    private lateinit var devicesRecyclerView: RecyclerView

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

        devicesRecyclerView = findViewById(R.id.devicesRecyclerView)
        devicesRecyclerView.layoutManager = LinearLayoutManager(this)

        loadDevicesFromApi()
    }

    private fun loadDevicesFromApi() {
        lifecycleScope.launch {
            try {
                val devices: List<Device> = RetrofitClient.apiService.getDevices()

                devicesRecyclerView.adapter = DevicesAdapter(devices)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
