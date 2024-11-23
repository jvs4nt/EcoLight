package org.ecolight

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
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

class EditGoalActivity : AppCompatActivity() {
    private lateinit var valorMetaEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var goBackMenuButton: ImageButton

    private lateinit var auth: FirebaseAuth

    private var metaId: String? = null
    private var usuarioEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_goal)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        usuarioEmail = auth.currentUser?.email

        valorMetaEditText = findViewById(R.id.valorMetaEditText)
        saveButton = findViewById(R.id.saveButton)
        goBackMenuButton = findViewById(R.id.goBackMenuButton)

        metaId = intent.getStringExtra("META_ID")
        val valorMeta = intent.getDoubleExtra("VALOR_META", 0.0)
        valorMetaEditText.setText(valorMeta.toString())

        saveButton.setOnClickListener {
            saveGoal()
        }

        goBackMenuButton.setOnClickListener {
            finish()
        }
    }

    private fun saveGoal() {
        val newValorMeta = valorMetaEditText.text.toString().toDoubleOrNull()

        Log.d("EditGoalActivity", "metaId: $metaId")
        Log.d("EditGoalActivity", "newValorMeta: $newValorMeta")
        Log.d("EditGoalActivity", "usuarioEmail: $usuarioEmail")

        if (metaId.isNullOrEmpty()) {
            Toast.makeText(this, "Meta ID está vazio", Toast.LENGTH_SHORT).show()
            return
        }

        if (newValorMeta == null) {
            Toast.makeText(this, "Valor da meta é inválido", Toast.LENGTH_SHORT).show()
            return
        }

        if (usuarioEmail.isNullOrEmpty()) {
            Toast.makeText(this, "Email do usuário não encontrado", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedMeta = Meta(
            id = metaId!!.toInt(),
            valorMeta = newValorMeta,
            usuarioEmail = usuarioEmail!!
        )

        RetrofitClient.apiService.updateMeta(metaId!!, updatedMeta).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@EditGoalActivity, "Meta atualizada com sucesso!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(
                        this@EditGoalActivity,
                        "Erro ao atualizar meta: ${response.code()} - ${errorBody ?: "Sem detalhes"}",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.e("EditGoalActivity", "Erro ao atualizar meta: ${response.code()} - $errorBody")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@EditGoalActivity, "Erro de conexão: ${t.message}", Toast.LENGTH_LONG).show()
                Log.e("EditGoalActivity", "Erro de conexão ao atualizar meta", t)
            }
        })
    }
}
