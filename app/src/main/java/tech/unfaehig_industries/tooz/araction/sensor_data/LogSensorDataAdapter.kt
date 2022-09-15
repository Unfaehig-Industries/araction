package tech.unfaehig_industries.tooz.araction.sensor_data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import tech.unfaehig_industries.tooz.araction.databinding.SensorDataItemBinding

class LogSensorDataAdapter : RecyclerView.Adapter<LogSensorDataAdapter.SensorDataViewHolder>() {

    private var items: MutableList<String> = mutableListOf()

    class SensorDataViewHolder(val view: AppCompatTextView) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SensorDataViewHolder {
        val view: AppCompatTextView = SensorDataItemBinding.inflate(LayoutInflater.from(parent.context)).root
        return SensorDataViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: SensorDataViewHolder, position: Int) {
        holder.view.text = items[position]
    }

    fun createItem(name: String): List<String> {
        Timber.d("adding item: $name")
        items.add(name)
        notifyItemInserted(items.size - 1)
        return items
    }
}