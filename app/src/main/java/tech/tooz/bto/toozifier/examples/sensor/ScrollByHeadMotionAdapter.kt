package tech.tooz.bto.toozifier.examples.sensor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import tech.tooz.bto.toozifier.examples.R
import timber.log.Timber

class ScrollByHeadMotionAdapter : RecyclerView.Adapter<ScrollByHeadMotionAdapter.ScrollByHeadMotionViewHOlder>() {

    private var items: MutableList<String>

    init {
        items = mutableListOf()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScrollByHeadMotionViewHOlder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_scroll_by_head_motion, parent, false) as AppCompatTextView
        return ScrollByHeadMotionViewHOlder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ScrollByHeadMotionViewHOlder, position: Int) {
        holder.view.text = items[position]
    }

    class ScrollByHeadMotionViewHOlder(val view: AppCompatTextView) : RecyclerView.ViewHolder(view)

    fun createItem(name: String): List<String> {
        Timber.d("adding item: $name")
        items.add(name)
        notifyItemInserted(items.size - 1)
        return items
    }
}