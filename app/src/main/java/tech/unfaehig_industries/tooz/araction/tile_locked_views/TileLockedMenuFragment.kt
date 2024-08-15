package tech.unfaehig_industries.tooz.araction.tile_locked_views

import tech.unfaehig_industries.tooz.phone_tracking.TrackingEventManager
import tech.unfaehig_industries.tooz.phone_tracking.SensorDataCallback
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tech.unfaehig_industries.tooz.tooz_base_application.BaseToozifierFragment
import tech.unfaehig_industries.tooz.araction.databinding.TileLockedMenuFragmentBinding
import tech.unfaehig_industries.tooz.tile_menu.tile_menu.TileActionButtonData
import tech.unfaehig_industries.tooz.tile_menu.tile_menu.TileButtonData
import tech.unfaehig_industries.tooz.tile_menu.tile_menu.TileSubmenuButtonData
import tooz.bto.toozifier.sensors.Sensor

class TileLockedMenuFragment : BaseToozifierFragment() {

    // The binding contains the views that are part of this fragment
    private var _binding: TileLockedMenuFragmentBinding? = null
    private val binding get() = _binding!!

    override val layout: TileLockedMenuLayout = TileLockedMenuLayout(toozifier)
    override val dataSensors: Array<Sensor> = arrayOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TileLockedMenuFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    @SuppressLint("InflateParams", "ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the view which is supposed to be shown on the glasses
        layout.inflateView(requireContext())

        //Initialize TileLockedMenu
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

        binding.tileLockedMenu.populate(tiles)
        layout.tileLockedMenu.populate(tiles)

        // Initialize phone positional tracking
        trackingEventManager =
            TrackingEventManager( object : SensorDataCallback {
                override fun onCursorUpdate(angle: Double, dist: Double) {
                    binding.tileLockedMenu.moveView(angle, dist)
                    layout.tileLockedMenu.moveView(angle, dist)
                }

                override fun onAccuracyChanged(accuracy: Int) {
                    // Handle accuracy change
                }
            }, activity)

        trackingEventManager.start()
        trackingEventManager.resetZeroPosition()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun updateActionText(text: String) {
        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                binding.actionText.text = text
                layout.actionText.text = text
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding.tileLockedMenu.stopHoverJob()
        layout.tileLockedMenu.stopHoverJob()
    }
}