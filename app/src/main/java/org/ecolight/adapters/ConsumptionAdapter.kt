package org.ecolight.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
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
            }.addOnFailureListener {
                holder.powerTextView.text = "Potência: Não disponível"
            }
        } else {
            holder.powerTextView.text = "Potência: Não disponível"
        }

        val timeInMinutes = consumption.timeUsed?.toDoubleOrNull() ?: 0.0
        holder.priceTextView.text = "Preço: Calcular"

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
            val database = FirebaseDatabase.getInstance().getReference("consumption")
            consumption.id?.let { id ->
                database.child(id).removeValue()
                    .addOnSuccessListener {
                        consumptions.removeAt(position)
                        notifyItemRemoved(position)
                    }
                    .addOnFailureListener { e ->
                        e.printStackTrace()
                    }
            }
        }
    }

    override fun getItemCount(): Int = consumptions.size
}
