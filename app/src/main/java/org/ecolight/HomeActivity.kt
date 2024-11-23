package org.ecolight

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
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

class HomeActivity : AppCompatActivity() {
    private lateinit var registrarConsumoButton: ImageButton
    private lateinit var titleMetaTextView: TextView
    private lateinit var profileButton: ImageButton
    private lateinit var meuConsumoButton: ImageButton
    private lateinit var meuPerfilButton: ImageButton
    private lateinit var listaDispositivosButton: ImageButton
    private lateinit var editGoalImageButton: ImageButton
    private lateinit var refreshImageButton: ImageButton

    private lateinit var auth: FirebaseAuth

    private var userMeta: Meta? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        auth = FirebaseAuth.getInstance()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        registrarConsumoButton = findViewById(R.id.registrarConsumoButton)
        titleMetaTextView = findViewById(R.id.titleMetaTextView)
        profileButton = findViewById(R.id.profileButton)
        meuConsumoButton = findViewById(R.id.meuConsumoButton)
        meuPerfilButton = findViewById(R.id.meuPerfilButton)
        listaDispositivosButton = findViewById(R.id.listaDispositivosButton)
        editGoalImageButton = findViewById(R.id.editGoalImageButton)
        refreshImageButton = findViewById(R.id.refreshImageButton)

        registrarConsumoButton.setOnClickListener {
            val intent = Intent(this, ConsumptionRegisterActivity::class.java)
            startActivity(intent)
        }

        titleMetaTextView.setOnClickListener {
            if (userMeta == null) {
                val intent = Intent(this, GoalRegisterActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Você já registrou uma meta!", Toast.LENGTH_SHORT).show()
            }
        }

        profileButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        meuConsumoButton.setOnClickListener {
            val intent = Intent(this, ConsumptionListActivity::class.java)
            startActivity(intent)
        }

        meuPerfilButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        listaDispositivosButton.setOnClickListener {
            val intent = Intent(this, ListDevicesActivity::class.java)
            startActivity(intent)
        }

        refreshImageButton.setOnClickListener {
            fetchUserGoal()
        }

        editGoalImageButton.setOnClickListener {
            val userEmail = auth.currentUser?.email
            if (!userEmail.isNullOrEmpty()) {
                RetrofitClient.apiService.getMetaByEmail(userEmail).enqueue(object : Callback<Meta> {
                    override fun onResponse(call: Call<Meta>, response: Response<Meta>) {
                        if (response.isSuccessful) {
                            val meta = response.body()
                            if (meta != null) {
                                val intent = Intent(this@HomeActivity, EditGoalActivity::class.java)
                                intent.putExtra("META_ID", meta.id.toString()) // Passar como String
                                intent.putExtra("VALOR_META", meta.valorMeta)
                                startActivity(intent)
                            } else {
                                Toast.makeText(this@HomeActivity, "Meta não encontrada", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this@HomeActivity, "Erro ao carregar meta", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Meta>, t: Throwable) {
                        Toast.makeText(this@HomeActivity, "Erro de conexão: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
            }
        }

        fetchUserGoal()
    }

    private fun fetchUserGoal() {
        val userEmail = auth.currentUser?.email

        if (userEmail.isNullOrEmpty()) {
            titleMetaTextView.text = "Meta não definida"
            userMeta = null // Resetar a meta
            return
        }

        RetrofitClient.apiService.getMetaByEmail(userEmail).enqueue(object : Callback<Meta> {
            override fun onResponse(call: Call<Meta>, response: Response<Meta>) {
                if (response.isSuccessful) {
                    userMeta = response.body()
                    titleMetaTextView.text = "R$${userMeta?.valorMeta ?: "Meta"}"
                    Log.d("HomeActivity", "Meta carregada: ${userMeta?.valorMeta}")
                } else {
                    titleMetaTextView.text = "..."
                    userMeta = null // Resetar a meta
                    Log.e("HomeActivity", "Erro: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Meta>, t: Throwable) {
                Toast.makeText(this@HomeActivity, "Erro de conexão: ${t.message}", Toast.LENGTH_SHORT).show()
                titleMetaTextView.text = "..."
                userMeta = null // Resetar a meta
                Log.e("HomeActivity", "Erro ao carregar meta", t)
            }
        })
    }
}

