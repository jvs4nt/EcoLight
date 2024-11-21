package org.ecolight

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HomeActivity : AppCompatActivity() {
    private lateinit var goBackMenuButton: ImageButton
    private lateinit var registrarConsumoButton: ImageButton
    private lateinit var textView9: TextView
    private lateinit var profileButton: ImageButton
    private lateinit var meuConsumoButton: ImageButton
    private lateinit var meuPerfilButton: ImageButton
    private lateinit var listaDispositivosButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        goBackMenuButton = findViewById(R.id.goBackMenuButton)
        registrarConsumoButton = findViewById(R.id.registrarConsumoButton)
        textView9 = findViewById(R.id.textView9)
        profileButton = findViewById(R.id.profileButton)
        meuConsumoButton = findViewById(R.id.meuConsumoButton)
        meuPerfilButton = findViewById(R.id.meuPerfilButton)
        listaDispositivosButton = findViewById(R.id.listaDispositivosButton)

        goBackMenuButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        registrarConsumoButton.setOnClickListener {
            val intent = Intent(this, ConsumptionRegisterActivity::class.java)
            startActivity(intent)
        }

        textView9.setOnClickListener {
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
    }
}