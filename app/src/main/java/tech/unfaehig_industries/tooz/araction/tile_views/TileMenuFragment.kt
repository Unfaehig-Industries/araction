package tech.unfaehig_industries.tooz.araction.tile_views

import tech.unfaehig_industries.tooz.phone_tracking.TrackingEventManager
import tech.unfaehig_industries.tooz.phone_tracking.SensorDataCallback
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import tech.unfaehig_industries.tooz.tooz_base_views.BaseToozifierFragment
import tech.unfaehig_industries.tooz.araction.databinding.TileMenuFragmentBinding
import tech.unfaehig_industries.tooz.araction.tile_menu.TileActionButtonData
import tech.unfaehig_industries.tooz.araction.tile_menu.TileButtonData
import tech.unfaehig_industries.tooz.araction.tile_menu.TileSubmenuButtonData
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
        val subsubmenuTiles: Array<TileButtonData> = arrayOf(
            TileActionButtonData("Sys", Color.parseColor("#592E83"), {updateActionText("120")}),
            TileActionButtonData("Dia", Color.parseColor("#5C374C"), {updateActionText("60")})
        )
        val submenuTiles: Array<TileButtonData> = arrayOf(
            TileSubmenuButtonData("Blutdruck", Color.parseColor("#F39237"), subsubmenuTiles),
            TileActionButtonData("Puls", Color.parseColor("#DC758F"), {updateActionText("50")})
        )
        val actionTiles: Array<TileButtonData> = arrayOf(
            TileSubmenuButtonData("Vitalwerte", Color.parseColor("#00CCA3"), submenuTiles),
            TileActionButtonData("Medikation", Color.parseColor("#F39237"), {updateActionText("Medikation")}),
            TileActionButtonData("Anamnese", Color.parseColor("#DC758F"), {updateActionText("Anamnese")}),
            TileActionButtonData("Aufenthalt", Color.parseColor("#008DD5"), {updateActionText("Aufenthalt")})
        )

        val tiles: Array<TileButtonData> = arrayOf(
            TileSubmenuButtonData("Karin Jager", Color.parseColor("#592E83"), actionTiles),
            TileSubmenuButtonData("Philipp Wexler", Color.parseColor("#CCC900"), actionTiles),
            TileSubmenuButtonData("Marcel Gärtner", Color.parseColor("#5C374C"), actionTiles),
            TileSubmenuButtonData("Christin Pabst", Color.parseColor("#29339B"), actionTiles)
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
        requireContext().mainExecutor?.execute {
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