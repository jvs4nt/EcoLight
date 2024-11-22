package org.ecolight.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import org.ecolight.EditConsumptionActivity
import org.ecolight.R
import org.ecolight.models.Consumption

class ConsumptionAdapter(
    private val consumptions: MutableList<Consumption>
) : RecyclerView.Adapter<ConsumptionAdapter.ConsumptionViewHolder>() {

    class ConsumptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val consumptionName: TextView = itemView.findViewById(R.id.consumptionName)
        val powerTextView: TextView = itemView.findViewById(R.id.powerTextView)
        val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
        val priceTextView: TextView = itemView.findViewById(R.id.priceTextView)
        val editButton: Button = itemView.findViewById(R.id.editItemButton)
        val deleteButton: Button = itemView.findViewById(R.id.deleteItemButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsumptionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_consumption_card, parent, false)
        return ConsumptionViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConsumptionViewHolder, position: Int) {
        val consumption = consumptions[position]

        holder.consumptionName.text = consumption.name
        holder.timeTextView.text = "Tempo: ${consumption.timeUsed} min"

        val deviceId = consumption.deviceId
        if (deviceId != null) {
            val database = FirebaseDatabase.getInstance().getReference("devices")
            database.child(deviceId).get().addOnSuccessListener { snapshot ->
                val power = snapshot.child("power").getValue(String::class.java)
                holder.powerTextView.text = "Potência: ${power ?: "Não disponível"}"

                val powerInWatts = power?.replace("W", "")?.toDoubleOrNull() ?: 0.0
                val powerInKw = powerInWatts / 1000
                val timeInHours = (consumption.timeUsed?.toDoubleOrNull() ?: 0.0) / 60
                val price = timeInHours * powerInKw * 0.50 // Tarifa fixa de 0.50

                holder.priceTextView.text = String.format("Preço: R$ %.2f", price)
            }.addOnFailureListener {
                holder.powerTextView.text = "Potência: Não disponível"
                holder.priceTextView.text = "Preço: Não disponível"
            }
        } else {
            holder.powerTextView.text = "Potência: Não disponível"
            holder.priceTextView.text = "Preço: Não disponível"
        }

        holder.editButton.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, EditConsumptionActivity::class.java)
            intent.putExtra("CONSUMPTION_ID", consumption.id)
            intent.putExtra("NAME", consumption.name)
            intent.putExtra("TIME_USED", consumption.timeUsed)
            intent.putExtra("DEVICE_ID", consumption.deviceId)
            intent.putExtra("DEVICE_NAME", consumption.deviceName)
            context.startActivity(intent)
        }

        holder.deleteButton.setOnClickListener {
            val currentUser = FirebaseAuth.getInstance().currentUser
            val email = currentUser?.email

            if (email == null) {
                Toast.makeText(holder.itemView.context, "Usuário não autenticado!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sanitizedEmail = email.replace(".", ",")
            val database = FirebaseDatabase.getInstance().getReference("consumption")

            consumption.id?.let { id ->
                database.child(sanitizedEmail).child(id).removeValue()
                    .addOnSuccessListener {
                        consumptions.removeAt(position)
                        notifyItemRemoved(position)
                        Toast.makeText(holder.itemView.context, "Consumo apagado com sucesso!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(holder.itemView.context, "Erro ao apagar consumo: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }

    }

    override fun getItemCount(): Int = consumptions.size
}
