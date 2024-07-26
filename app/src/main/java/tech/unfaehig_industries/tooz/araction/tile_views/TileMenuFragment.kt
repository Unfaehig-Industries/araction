package tech.unfaehig_industries.tooz.araction.tile_views

import tech.unfaehig_industries.tooz.phone_tracking.TrackingEventManager
import tech.unfaehig_industries.tooz.phone_tracking.SensorDataCallback
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import tech.unfaehig_industries.tooz.araction.BaseToozifierFragment
import tech.unfaehig_industries.tooz.araction.databinding.TileMenuFragmentBinding
import tech.unfaehig_industries.tooz.araction.tile_menu.TileButtonData
import tooz.bto.toozifier.sensors.Sensor

class TileMenuFragment : BaseToozifierFragment() {

    // The binding contains the views that are part of this fragment
    private var _binding: TileMenuFragmentBinding? = null
    private val binding get() = _binding!!

    override val layout: TileMenuLayout = TileMenuLayout(toozifier)
    override val dataSensors: Array<Sensor> = arrayOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TileMenuFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the view which is supposed to be shown on the glasses
        layout.inflateView(requireContext())

        //Initialize TileMenu
        val submenuTiles = arrayOf(
            TileButtonData("Puls", Color.parseColor("#00CCA3"), {updateActionText("Puls")}, arrayOf()),
            TileButtonData("Blutdruck", Color.parseColor("#00CCA3"), {updateActionText("Blutdruck")}, arrayOf())
        )
        val actionTiles = arrayOf(
            TileButtonData("Vitalwerte", Color.parseColor("#00CCA3"), {}, submenuTiles),
            TileButtonData("Medikation", Color.parseColor("#F39237"), {updateActionText("Medikation")}, arrayOf()),
            TileButtonData("Anamnese", Color.parseColor("#DC758F"), {updateActionText("Anamnese")}, arrayOf()),
            TileButtonData("Aufenthalt", Color.parseColor("#008DD5"), {updateActionText("Aufenthalt")}, arrayOf())
        )

        val tiles = arrayOf(
            TileButtonData("Karin Jager", Color.parseColor("#592E83"), {}, actionTiles),
            TileButtonData("Philipp Wexler", Color.parseColor("#CCC900"), {}, actionTiles),
            TileButtonData("Marcel Gärtner", Color.parseColor("#5C374C"), {}, actionTiles),
            TileButtonData("Christin Pabst", Color.parseColor("#29339B"), {}, actionTiles)
        )

        binding.tileMenu.populate(tiles)
        layout.tileMenu.populate(tiles)

        // Initialize phone positional tracking
        trackingEventManager =
            TrackingEventManager( object : SensorDataCallback {
                override fun onCursorUpdate(angle: Double, dist: Double) {
                    binding.tileMenu.moveView(angle, dist)
                    layout.tileMenu.moveView(angle, dist)
                }

                override fun onAccuracyChanged(accuracy: Int) {
                    // Handle accuracy change
                }
            }, activity)

        trackingEventManager.start()
        trackingEventManager.resetZeroPosition()
    }

    private fun updateActionText(text: String) {
        context?.mainExecutor?.execute {
            run {
                binding.actionText.text = text
                layout.actionText.text = text
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding.tileMenu.stopHoverJob()
        layout.tileMenu.stopHoverJob()
    }
}