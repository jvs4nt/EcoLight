package org.ecolight

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class EditConsumptionActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var timeUsedEditText: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var saveButton: Button
    private lateinit var goBackMenuButton: ImageButton
    private lateinit var homeButton: ImageButton
    private lateinit var menuButton: ImageButton
    private lateinit var profileButton: ImageButton

    private lateinit var database: DatabaseReference
    private val deviceNames = mutableListOf<String>()
    private val deviceIds = mutableListOf<String>()

    private var consumptionId: String? = null
    private var selectedDeviceId: String? = null
    private var selectedDeviceName: String? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_consumption)
        auth = FirebaseAuth.getInstance()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        nameEditText = findViewById(R.id.nameEditText)
        timeUsedEditText = findViewById(R.id.tempoUtilizadoEditText)
        categorySpinner = findViewById(R.id.categorySpinner)
        saveButton = findViewById(R.id.logoutButton)
        goBackMenuButton = findViewById(R.id.goBackMenuButton)
        homeButton = findViewById(R.id.homeButton)
        menuButton = findViewById(R.id.menuButton)
        profileButton = findViewById(R.id.profileButton)

        database = FirebaseDatabase.getInstance("https://ecolight-b74d6-default-rtdb.firebaseio.com/").getReference("devices")

        consumptionId = intent.getStringExtra("CONSUMPTION_ID")
        nameEditText.setText(intent.getStringExtra("NAME"))
        timeUsedEditText.setText(intent.getStringExtra("TIME_USED"))
        selectedDeviceId = intent.getStringExtra("DEVICE_ID")
        selectedDeviceName = intent.getStringExtra("DEVICE_NAME")

        loadDevicesToSpinner()

        saveButton.setOnClickListener {
            saveEditedConsumption()
        }

        goBackMenuButton.setOnClickListener { finish() }
        homeButton.setOnClickListener { startActivity(Intent(this, HomeActivity::class.java)) }
        menuButton.setOnClickListener { startActivity(Intent(this, HomeActivity::class.java)) }
        profileButton.setOnClickListener { startActivity(Intent(this, ProfileActivity::class.java)) }
    }

    private fun loadDevicesToSpinner() {
        database.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                deviceNames.clear()
                deviceIds.clear()
                var selectedIndex = 0
                var index = 0

                for (deviceSnapshot in snapshot.children) {
                    val deviceName = deviceSnapshot.child("name").getValue(String::class.java)
                    val deviceId = deviceSnapshot.key
                    if (deviceName != null && deviceId != null) {
                        deviceNames.add(deviceName)
                        deviceIds.add(deviceId)

                        if (deviceId == selectedDeviceId) {
                            selectedIndex = index
                        }
                        index++
                    }
                }

                val adapter = ArrayAdapter(
                    this@EditConsumptionActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    deviceNames
                )
                categorySpinner.adapter = adapter
                categorySpinner.setSelection(selectedIndex)
            }

            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                Toast.makeText(
                    this@EditConsumptionActivity,
                    "Erro ao carregar dispositivos: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun saveEditedConsumption() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val email = currentUser?.email

        if (email == null) {
            Toast.makeText(this, "Usuário não autenticado!", Toast.LENGTH_SHORT).show()
            return
        }

        val sanitizedEmail = email.replace(".", ",")

        val name = nameEditText.text.toString().trim()
        val timeUsed = timeUsedEditText.text.toString().trim()
        val selectedDevicePosition = categorySpinner.selectedItemPosition

        if (name.isEmpty() || timeUsed.isEmpty() || selectedDevicePosition == -1) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedDeviceId = deviceIds[selectedDevicePosition]
        val updatedDeviceName = deviceNames[selectedDevicePosition]

        val updatedConsumption = mapOf(
            "name" to name,
            "timeUsed" to timeUsed,
            "deviceId" to updatedDeviceId,
            "deviceName" to updatedDeviceName
        )

        consumptionId?.let { id ->
            val consumptionDatabase = FirebaseDatabase.getInstance().getReference("consumption")
            consumptionDatabase.child(sanitizedEmail).child(id).updateChildren(updatedConsumption)
                .addOnSuccessListener {
                    Toast.makeText(this, "Consumo atualizado com sucesso", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, ConsumptionListActivity::class.java))
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao atualizar consumo: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

}
