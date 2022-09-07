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
import tech.tooz.bto.toozifier.examples.databinding.ExampleListFragmentBinding

class ExampleListFragment : Fragment() {

    private var _binding: ExampleListFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ExampleListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpExamplesList()
        (activity as? MainActivity)?.showProgress(false)
    }

    private fun setUpExamplesList() {
        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerviewExample.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(
            binding.recyclerviewExample.context,
            layoutManager.orientation
        )
        binding.recyclerviewExample.addItemDecoration(dividerItemDecoration)
        binding.recyclerviewExample.adapter = ExamplesAdapter(
            items = arrayListOf(
                Example.SENSOR
            ),
            parentFragment = this
        )
    }

    class ExamplesAdapter(private val items: List<Example>, private val parentFragment: Fragment) :
        RecyclerView.Adapter<ExamplesAdapter.ExampleViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExampleViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.example_list_item, parent, false)
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
                Example.SENSOR.ordinal -> {
                    bindItem(
                        holder = holder,
                        text = parentFragment.requireContext().getString(R.string.sensor_data_example_name),
                        clickListener = {
                            parentFragment.findNavController().navigate(R.id.sensor_data_action)
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
        SENSOR
    }

}