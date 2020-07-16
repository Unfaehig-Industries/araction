package tech.tooz.bto.toozifier.examples.sensor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import tech.tooz.bto.toozifier.examples.R

class ScrollByHeadMotionAdapter : RecyclerView.Adapter<ScrollByHeadMotionAdapter.ScrollByHeadMotionViewHOlder>() {

    private var items: List<String>

    init {
        items = createItems()
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

    private fun createItems(): List<String> {
        val items = arrayListOf<String>()
        for (i in 0..1000) {
            items.add("Item $i")
        }
        return items
    }
}