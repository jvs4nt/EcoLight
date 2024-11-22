package org.ecolight

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.ecolight.adapters.ConsumptionAdapter
import org.ecolight.models.Consumption

class ConsumptionListActivity : AppCompatActivity() {
    private lateinit var homeButton: ImageButton
    private lateinit var menuButton: ImageButton
    private lateinit var profileButton: ImageButton
    private lateinit var goBackMenuButton: ImageButton
    private lateinit var consumptionRecyclerView: RecyclerView
    private lateinit var database: DatabaseReference
    private val consumptionList = mutableListOf<Consumption>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_consumption_list)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        homeButton = findViewById(R.id.homeButton)
        menuButton = findViewById(R.id.menuButton)
        profileButton = findViewById(R.id.profileButton)
        goBackMenuButton = findViewById(R.id.goBackMenuButton)

        homeButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        menuButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        profileButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        goBackMenuButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        consumptionRecyclerView = findViewById(R.id.consumptionRecyclerView)
        consumptionRecyclerView.layoutManager = LinearLayoutManager(this)

        database = FirebaseDatabase.getInstance("https://ecolight-b74d6-default-rtdb.firebaseio.com/").getReference("consumption")

        loadConsumptions()
    }

    private fun loadConsumptions() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                consumptionList.clear() // Limpa a lista antes de adicionar os novos dados
                for (consumptionSnapshot in snapshot.children) {
                    val consumption = consumptionSnapshot.getValue(Consumption::class.java)
                    consumption?.id = consumptionSnapshot.key // Adiciona o ID do nó
                    if (consumption != null) {
                        consumptionList.add(consumption)
                    }
                }
                consumptionRecyclerView.adapter = ConsumptionAdapter(consumptionList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@ConsumptionListActivity,
                    "Erro ao carregar consumos: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}
