package org.ecolight

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import org.ecolight.api.RetrofitClient
import org.ecolight.models.Meta
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class GoalRegisterActivity : AppCompatActivity() {
    private lateinit var goBackMenuButton: ImageButton
    private lateinit var profileButton: ImageButton
    private lateinit var saveButton: Button
    private lateinit var goalEditText: EditText
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_goal_register)
        auth = FirebaseAuth.getInstance()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        goBackMenuButton = findViewById(R.id.goBackMenuButton)
        profileButton = findViewById(R.id.profileButton)
        saveButton = findViewById(R.id.logoutButton)
        goalEditText = findViewById(R.id.nameEditText)

        goBackMenuButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        profileButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        saveButton.setOnClickListener {
            registerGoal()
        }
    }

    private fun registerGoal() {
        val valorMeta = goalEditText.text.toString().toDoubleOrNull()
        val usuarioEmail = auth.currentUser?.email

        if (valorMeta == null || usuarioEmail.isNullOrEmpty()) {
            Toast.makeText(this, "Preencha o valor da meta corretamente", Toast.LENGTH_SHORT).show()
            return
        }

        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val meta = Meta(
            valorMeta = valorMeta,
            dataCadastro = currentDate,
            usuarioEmail = usuarioEmail
        )

        RetrofitClient.apiService.registrarMeta(meta).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@GoalRegisterActivity, "Meta registrada com sucesso", Toast.LENGTH_SHORT).show()
                    goalEditText.text.clear()
                } else {
                    Toast.makeText(
                        this@GoalRegisterActivity,
                        "Erro ao registrar meta: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@GoalRegisterActivity, "Erro de conexão: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
