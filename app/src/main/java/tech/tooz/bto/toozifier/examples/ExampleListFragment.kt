package tech.tooz.bto.toozifier.examples

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_example_list.*

class ExampleListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_example_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpExamplesList()
        (activity as? MainActivity)?.showProgress(false)
    }

    private fun setUpExamplesList() {
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerview_example.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(
            recyclerview_example.context,
            layoutManager.orientation
        )
        recyclerview_example.addItemDecoration(dividerItemDecoration)
        recyclerview_example.adapter = ExamplesAdapter(
            items = arrayListOf(
                Example.PROMPT,
                Example.HEARTBEAT,
                Example.SENSOR,
                Example.AUDIO,
                Example.WEB_VIEW
            ),
            parentFragment = this
        )
    }

    class ExamplesAdapter(private val items: List<Example>, private val parentFragment: Fragment) :
        RecyclerView.Adapter<ExamplesAdapter.ExampleViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExampleViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_example, parent, false)
            return ExampleViewHolder(view)
        }

        override fun getItemCount(): Int {
            return items.size
        }

        override fun getItemViewType(position: Int): Int {
            return items[position].ordinal
        }

        override fun onBindViewHolder(holder: ExampleViewHolder, position: Int) {
            when (getItemViewType(position)) {
                Example.PROMPT.ordinal -> {
                    bindItem(
                        holder = holder,
                        text = parentFragment.requireContext().getString(R.string.prompt_example),
                        clickListener = {
                            parentFragment.findNavController().navigate(R.id.fragment_prompt_action)
                        }
                    )
                }
                Example.HEARTBEAT.ordinal -> {
                    bindItem(
                        holder = holder,
                        text = parentFragment.requireContext().getString(R.string.heartbeat_example),
                        clickListener = {
                            parentFragment.findNavController()
                                .navigate(R.id.fragment_heartbeat_action)
                        }
                    )
                }
                Example.SENSOR.ordinal -> {
                    bindItem(
                        holder = holder,
                        text = parentFragment.requireContext().getString(R.string.scrolling_by_head_motion),
                        clickListener = {
                            parentFragment.findNavController().navigate(R.id.fragment_sensor_action)
                        }
                    )
                }
                Example.WEB_VIEW.ordinal -> {
                    bindItem(
                        holder = holder,
                        text = parentFragment.requireContext().getString(R.string.webview_example),
                        clickListener = {
                            parentFragment.findNavController().navigate(R.id.fragment_webview_action)
                        }
                    )
                }
                Example.AUDIO.ordinal -> {
                    bindItem(
                        holder = holder,
                        text = parentFragment.requireContext().getString(R.string.audio),
                        clickListener = {
                            parentFragment.findNavController().navigate(R.id.fragment_audio)
                        }
                    )
                }
            }
        }

        private fun bindItem(holder: ExampleViewHolder, text: String, clickListener: () -> Unit) {
            holder.rootLayout.findViewById<TextView>(R.id.example_item_textview).text = text
            holder.itemView.setOnClickListener {
                clickListener.invoke()
            }
        }

        class ExampleViewHolder(val rootLayout: View) : RecyclerView.ViewHolder(rootLayout)
    }

    enum class Example {
        PROMPT, HEARTBEAT, SENSOR, AUDIO, WEB_VIEW
    }

}