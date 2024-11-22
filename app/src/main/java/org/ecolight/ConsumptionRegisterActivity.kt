package org.ecolight

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ConsumptionRegisterActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var timeUsedEditText: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var saveButton: Button
    private lateinit var goBackMenuButton: ImageButton
    private lateinit var profileButton: ImageButton
    private lateinit var homeButton: ImageButton
    private lateinit var menuButton: ImageButton

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private val deviceNames = mutableListOf<String>()
    private val deviceIds = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        auth = FirebaseAuth.getInstance()
        setContentView(R.layout.activity_consumption_register)

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
        profileButton = findViewById(R.id.profileButton)
        homeButton = findViewById(R.id.homeButton)
        menuButton = findViewById(R.id.menuButton)

        database = FirebaseDatabase.getInstance().getReference("devices")

        goBackMenuButton.setOnClickListener { startActivity(Intent(this, HomeActivity::class.java)) }
        profileButton.setOnClickListener { startActivity(Intent(this, ProfileActivity::class.java)) }
        homeButton.setOnClickListener { startActivity(Intent(this, HomeActivity::class.java)) }
        menuButton.setOnClickListener { startActivity(Intent(this, HomeActivity::class.java)) }

        loadDevicesToSpinner()

        saveButton.setOnClickListener { saveConsumptionData() }
    }

    private fun loadDevicesToSpinner() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                deviceNames.clear()
                deviceIds.clear()

                for (deviceSnapshot in snapshot.children) {
                    val deviceName = deviceSnapshot.child("name").getValue(String::class.java)
                    val deviceId = deviceSnapshot.key
                    if (deviceName != null && deviceId != null) {
                        deviceNames.add(deviceName)
                        deviceIds.add(deviceId)
                    }
                }

                val adapter = ArrayAdapter(
                    this@ConsumptionRegisterActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    deviceNames
                )
                categorySpinner.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@ConsumptionRegisterActivity,
                    "Erro ao carregar dispositivos: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun saveConsumptionData() {
        val currentUser = auth.currentUser
        val email = currentUser?.email

        if (email == null) {
            Toast.makeText(this, "Usuário não autenticado!", Toast.LENGTH_SHORT).show()
            return
        }

        val name = nameEditText.text.toString().trim()
        val timeUsed = timeUsedEditText.text.toString().trim()
        val selectedDevicePosition = categorySpinner.selectedItemPosition

        if (name.isEmpty() || timeUsed.isEmpty() || selectedDevicePosition == -1) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedDeviceId = deviceIds[selectedDevicePosition]
        val selectedDeviceName = deviceNames[selectedDevicePosition]

        val consumptionData = mapOf(
            "name" to name,
            "timeUsed" to timeUsed,
            "deviceId" to selectedDeviceId,
            "deviceName" to selectedDeviceName
        )

        val sanitizedEmail = email.replace(".", ",")
        val consumptionDatabase = FirebaseDatabase.getInstance().getReference("consumption")
        consumptionDatabase.child(sanitizedEmail).push().setValue(consumptionData)
            .addOnSuccessListener {
                Toast.makeText(this, "Consumo salvo com sucesso", Toast.LENGTH_SHORT).show()
                nameEditText.text.clear()
                timeUsedEditText.text.clear()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao salvar consumo: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

}
