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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileActivity : AppCompatActivity() {
    private lateinit var goBackMenuButton: ImageButton
    private lateinit var homeButton: ImageButton
    private lateinit var profileButton: ImageButton
    private lateinit var editarPerfilButton: Button
    private lateinit var logoutButton: Button
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("users")

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        goBackMenuButton = findViewById(R.id.goBackMenuButton)
        homeButton = findViewById(R.id.homeButton)
        profileButton = findViewById(R.id.profileButton)
        editarPerfilButton = findViewById(R.id.editarPerfilButton)
        logoutButton = findViewById(R.id.logoutButton)
        nameEditText = findViewById(R.id.nameEditText)
        emailEditText = findViewById(R.id.emailEditText)

        goBackMenuButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        homeButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        profileButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        editarPerfilButton.setOnClickListener {
            saveProfileData()
        }

        logoutButton.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        loadProfileData()
    }

    private fun loadProfileData() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val email = currentUser.email
            emailEditText.setText(email)
            database.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (userSnapshot in snapshot.children) {
                            val name = userSnapshot.child("name").getValue(String::class.java)
                            nameEditText.setText(name)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@ProfileActivity,
                        "Erro ao carregar dados: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }

    private fun saveProfileData() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val email = currentUser.email
            val name = nameEditText.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(this, "Digite um nome", Toast.LENGTH_SHORT).show()
                return
            }

            val userData = mapOf(
                "name" to name,
                "email" to email
            )

            database.child(currentUser.uid).setValue(userData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Dados salvos com sucesso", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao salvar dados: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }
}
