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

class CreateDeviceActivity : AppCompatActivity() {

    private lateinit var homeButton: ImageButton
    private lateinit var menuButton: ImageButton
    private lateinit var profileButton: ImageButton
    private lateinit var goBackMenuButton: ImageButton
    private lateinit var nameEditText: EditText
    private lateinit var powerEditText: EditText
    private lateinit var saveButton: Button

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_device)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        homeButton = findViewById(R.id.homeButton)
        menuButton = findViewById(R.id.menuButton)
        profileButton = findViewById(R.id.profileButton)
        goBackMenuButton = findViewById(R.id.goBackMenuButton)

        nameEditText = findViewById(R.id.nameEditText)
        powerEditText = findViewById(R.id.powerEditText)
        saveButton = findViewById(R.id.saveButton)

        database = FirebaseDatabase.getInstance("https://ecolight-b74d6-default-rtdb.firebaseio.com/").getReference("devices")

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
            finish()
        }

        saveButton.setOnClickListener {
            saveDeviceToFirebase()
        }
    }

    private fun saveDeviceToFirebase() {
        val name = nameEditText.text.toString().trim()
        val power = powerEditText.text.toString().trim()

        if (name.isEmpty() || power.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        val newDevice = Device(
            id = database.push().key,
            name = name,
            power = power
        )

        newDevice.id?.let {
            database.child(it).setValue(newDevice)
                .addOnSuccessListener {
                    Toast.makeText(this, "Dispositivo salvo com sucesso", Toast.LENGTH_SHORT).show()
                    nameEditText.text.clear()
                    powerEditText.text.clear()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao salvar: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
