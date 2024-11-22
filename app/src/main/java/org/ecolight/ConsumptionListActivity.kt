package org.ecolight

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import org.ecolight.adapters.ConsumptionAdapter
import org.ecolight.models.Consumption

class ConsumptionListActivity : AppCompatActivity() {

    private lateinit var goBackMenuButton: ImageButton
    private lateinit var homeButton: ImageButton
    private lateinit var profileButton: ImageButton
    private lateinit var menuButton: ImageButton
    private lateinit var consumptionRecyclerView: RecyclerView
    private lateinit var totalTextView: TextView
    private lateinit var valorImpostoTextView: TextView

    private val consumptions = mutableListOf<Consumption>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_consumption_list)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        goBackMenuButton = findViewById(R.id.goBackMenuButton)
        homeButton = findViewById(R.id.homeButton)
        profileButton = findViewById(R.id.profileButton)
        menuButton = findViewById(R.id.menuButton)
        consumptionRecyclerView = findViewById(R.id.consumptionRecyclerView)
        totalTextView = findViewById(R.id.totalTextView)
        valorImpostoTextView = findViewById(R.id.valorImpostoTextView)

        consumptionRecyclerView.layoutManager = LinearLayoutManager(this)

        goBackMenuButton.setOnClickListener { startActivity(Intent(this, HomeActivity::class.java)) }
        homeButton.setOnClickListener { startActivity(Intent(this, HomeActivity::class.java)) }
        profileButton.setOnClickListener { startActivity(Intent(this, ProfileActivity::class.java)) }
        menuButton.setOnClickListener { startActivity(Intent(this, HomeActivity::class.java)) }

        loadConsumptionsFromFirebase()
    }

    private fun loadConsumptionsFromFirebase() {
        val database = FirebaseDatabase.getInstance().getReference("consumption")
        database.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                consumptions.clear()

                for (consumptionSnapshot in snapshot.children) {
                    val consumption = consumptionSnapshot.getValue(Consumption::class.java)
                    consumption?.id = consumptionSnapshot.key
                    if (consumption != null) {
                        consumptions.add(consumption)
                    }
                }

                consumptionRecyclerView.adapter = ConsumptionAdapter(consumptions)

                calculateAndDisplayTotals()
            }

            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
            }
        })
    }

    private fun calculateAndDisplayTotals() {
        var total = 0.0
        val taxRate = 0.2

        for (consumption in consumptions) {
            val timeInHours = consumption.timeUsed?.toDoubleOrNull()?.div(60) ?: 0.0
            val deviceId = consumption.deviceId

            if (deviceId != null) {
                val database = FirebaseDatabase.getInstance().getReference("devices")
                database.child(deviceId).child("power").get().addOnSuccessListener { snapshot ->
                    val powerInWatts = snapshot.getValue(String::class.java)?.replace("W", "")
                        ?.toDoubleOrNull() ?: 0.0
                    val powerInKw = powerInWatts / 1000

                    val price = timeInHours * powerInKw * 0.50
                    total += price

                    totalTextView.text = String.format("R$ %.2f", total)
                    valorImpostoTextView.text = String.format("R$ %.2f", total * taxRate)
                }
            }
        }
    }
}
