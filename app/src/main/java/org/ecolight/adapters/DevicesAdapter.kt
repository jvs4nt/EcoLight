package org.ecolight.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import org.ecolight.EditDeviceActivity
import org.ecolight.R
import org.ecolight.models.Device

class DevicesAdapter(
    private val devices: MutableList<Device>
) : RecyclerView.Adapter<DevicesAdapter.DeviceViewHolder>() {

    class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val deviceName: TextView = itemView.findViewById(R.id.deviceName)
        val devicePower: TextView = itemView.findViewById(R.id.devicePower)
        val editButton: Button = itemView.findViewById(R.id.editItemButton)
        val deleteButton: Button = itemView.findViewById(R.id.deleteItemButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_device_card, parent, false)
        return DeviceViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val device = devices[position]
        holder.deviceName.text = device.name
        holder.devicePower.text = "Power: ${device.power}"

        holder.deleteButton.setOnClickListener {
            deleteDeviceFromFirebase(device, position)
        }

        holder.editButton.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, EditDeviceActivity::class.java)
            intent.putExtra("DEVICE_ID", device.id)
            intent.putExtra("DEVICE_NAME", device.name)
            intent.putExtra("DEVICE_POWER", device.power)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int = devices.size

    private fun deleteDeviceFromFirebase(device: Device, position: Int) {
        val database = FirebaseDatabase.getInstance().getReference("devices")

        device.id?.let { id ->
            database.child(id).removeValue()
                .addOnSuccessListener {
                    devices.removeAt(position)
                    notifyItemRemoved(position)
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                }
        }
    }
}
