package org.ecolight

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.ecolight.models.Device

class EditDeviceActivity : AppCompatActivity() {
    private lateinit var nameEditText: EditText
    private lateinit var powerEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var goBackMenuButton: ImageButton
    private lateinit var homeButton: ImageButton
    private lateinit var menuButton: ImageButton
    private lateinit var profileButton: ImageButton

    private lateinit var database: DatabaseReference
    private var deviceId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_device)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        nameEditText = findViewById(R.id.nameEditText)
        powerEditText = findViewById(R.id.powerEditText)
        saveButton = findViewById(R.id.saveButton)
        goBackMenuButton = findViewById(R.id.goBackMenuButton)
        homeButton = findViewById(R.id.homeButton)
        menuButton = findViewById(R.id.menuButton)
        profileButton = findViewById(R.id.profileButton)

        database = FirebaseDatabase.getInstance("https://ecolight-b74d6-default-rtdb.firebaseio.com/").getReference("devices")

        deviceId = intent.getStringExtra("DEVICE_ID")
        val deviceName = intent.getStringExtra("DEVICE_NAME")
        val devicePower = intent.getStringExtra("DEVICE_POWER")

        nameEditText.setText(deviceName)
        powerEditText.setText(devicePower)

        saveButton.setOnClickListener {
            saveEditedDevice()
        }

        goBackMenuButton.setOnClickListener { finish() }
        homeButton.setOnClickListener { startActivity(Intent(this, HomeActivity::class.java)) }
        menuButton.setOnClickListener { startActivity(Intent(this, HomeActivity::class.java)) }
        profileButton.setOnClickListener { startActivity(Intent(this, ProfileActivity::class.java)) }
    }

    private fun saveEditedDevice() {
        val name = nameEditText.text.toString().trim()
        val power = powerEditText.text.toString().trim()

        if (name.isEmpty() || power.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        deviceId?.let { id ->
            val updatedDevice = Device(id = id, name = name, power = power)
            database.child(id).setValue(updatedDevice)
                .addOnSuccessListener {
                    Toast.makeText(this, "Dispositivo atualizado com sucesso", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao atualizar: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
