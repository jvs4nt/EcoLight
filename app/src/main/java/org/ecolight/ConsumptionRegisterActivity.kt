package org.ecolight

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.*

class ConsumptionRegisterActivity : AppCompatActivity() {
    private lateinit var goBackMenuButton: ImageButton
    private lateinit var profileButton: ImageButton
    private lateinit var homeButton: ImageButton
    private lateinit var menuButton: ImageButton
    private lateinit var saveButton: Button
    private lateinit var nameEditText: EditText
    private lateinit var timeUsedEditText: EditText
    private lateinit var categorySpinner: Spinner

    private lateinit var database: DatabaseReference
    private val deviceNames = mutableListOf<String>() // Lista para armazenar os nomes dos dispositivos
    private val deviceIds = mutableListOf<String>() // Lista para armazenar os IDs dos dispositivos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_consumption_register)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializar componentes
        goBackMenuButton = findViewById(R.id.goBackMenuButton)
        profileButton = findViewById(R.id.profileButton)
        homeButton = findViewById(R.id.homeButton)
        menuButton = findViewById(R.id.menuButton)
        saveButton = findViewById(R.id.logoutButton)
        nameEditText = findViewById(R.id.nameEditText)
        timeUsedEditText = findViewById(R.id.tempoUtilizadoEditText)
        categorySpinner = findViewById(R.id.categorySpinner)

        // Inicializar Firebase Database
        database = FirebaseDatabase.getInstance("https://ecolight-b74d6-default-rtdb.firebaseio.com/").getReference("devices")

        // Configurar botões de navegação
        goBackMenuButton.setOnClickListener { startActivity(Intent(this, HomeActivity::class.java)) }
        profileButton.setOnClickListener { startActivity(Intent(this, ProfileActivity::class.java)) }
        homeButton.setOnClickListener { startActivity(Intent(this, HomeActivity::class.java)) }
        menuButton.setOnClickListener { startActivity(Intent(this, HomeActivity::class.java)) }

        // Configurar botão de salvar
        saveButton.setOnClickListener { saveConsumptionData() }

        // Carregar dispositivos no Spinner
        loadDevicesToSpinner()
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
                // Configurar o Spinner com os nomes dos dispositivos
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
        val name = nameEditText.text.toString().trim()
        val timeUsed = timeUsedEditText.text.toString().trim()
        val selectedDevicePosition = categorySpinner.selectedItemPosition

        if (name.isEmpty() || timeUsed.isEmpty() || selectedDevicePosition == -1) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedDeviceId = deviceIds[selectedDevicePosition]
        val selectedDeviceName = deviceNames[selectedDevicePosition]

        // Dados de consumo podem ser salvos no Firebase
        val consumptionData = mapOf(
            "name" to name,
            "timeUsed" to timeUsed,
            "deviceId" to selectedDeviceId,
            "deviceName" to selectedDeviceName
        )

        // Salvar os dados em um novo nó no banco de dados
        val consumptionDatabase = FirebaseDatabase.getInstance().getReference("consumption")
        consumptionDatabase.push().setValue(consumptionData)
            .addOnSuccessListener {
                Toast.makeText(this, "Consumo salvo com sucesso", Toast.LENGTH_SHORT).show()
                // Limpar campos
                nameEditText.text.clear()
                timeUsedEditText.text.clear()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao salvar consumo: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
