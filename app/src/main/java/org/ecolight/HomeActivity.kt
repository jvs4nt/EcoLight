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
import java.io.Console

class HomeActivity : AppCompatActivity() {
    private lateinit var registrarConsumoButton: ImageButton
    private lateinit var titleMetaTextView: TextView
    private lateinit var profileButton: ImageButton
    private lateinit var meuConsumoButton: ImageButton
    private lateinit var meuPerfilButton: ImageButton
    private lateinit var listaDispositivosButton: ImageButton
    private lateinit var editGoalImageButton: ImageButton

    private lateinit var auth: FirebaseAuth

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

        registrarConsumoButton.setOnClickListener {
            val intent = Intent(this, ConsumptionRegisterActivity::class.java)
            startActivity(intent)
        }

        titleMetaTextView.setOnClickListener {
            val intent = Intent(this, GoalRegisterActivity::class.java)
            startActivity(intent)
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
            return
        }

        RetrofitClient.apiService.getMetaByEmail(userEmail).enqueue(object : Callback<Meta> {
            override fun onResponse(call: Call<Meta>, response: Response<Meta>) {
                if (response.isSuccessful) {
                    val meta = response.body()
                    titleMetaTextView.text = "R$${meta?.valorMeta ?: "Meta"}"
                    Log.d("HomeActivity", "Meta carregada: ${meta?.valorMeta}")
                } else {
                    titleMetaTextView.text = "..."
                    Log.e("HomeActivity", "Erro: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Meta>, t: Throwable) {
                Toast.makeText(this@HomeActivity, "Erro de conexão: ${t.message}", Toast.LENGTH_SHORT).show()
                titleMetaTextView.text = "..."
                Log.e("HomeActivity", "Erro ao carregar meta", t)
            }
        })
    }

}
