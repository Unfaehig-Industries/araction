package tech.tooz.bto.toozifier.examples.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Bundle
import android.view.*
import tech.tooz.bto.toozifier.examples.BaseToozifierFragment
import tech.tooz.bto.toozifier.examples.R
import tech.tooz.bto.toozifier.examples.databinding.FragmentAudioBinding
import tooz.bto.common.Constants
import tooz.bto.toozifier.audio.*
import tooz.bto.toozifier.error.ErrorCause
import tooz.bto.toozifier.registration.RegistrationListener

class AudioFragment : BaseToozifierFragment() {

    companion object {
        // Max recording length in seconds, can be individually defined
        private const val MAX_RECORDING_LENGTH = 60
    }

    private var isRegistered = false
    private var binding: FragmentAudioBinding? = null
    private var lastRecording: ArrayList<Byte> = arrayListOf()

    // The default sampling rate provided by this API is 16000 Hz
    private var samplingRate: Int? = null

    // The default encoding  provided by this API is PCM_SIGNED
    private var encoding: AudioEncoding? = null

    // The default channel configuration for this API is MONO
    private var channel: AudioChannel? = null

    private var audioTrack: AudioTrack? = null

    private val registrationListener = object : RegistrationListener {

        override fun onRegisterSuccess() {
            isRegistered = true
            sendViewToGlasses()
            toozifier.removeListener(audioListener)
            toozifier.addListener(audioListener)
        }

        override fun onDeregisterFailure(errorCause: ErrorCause) {}

        override fun onDeregisterSuccess() {
            isRegistered = false
        }

        override fun onRegisterFailure(errorCause: ErrorCause) {
            isRegistered = false
        }
    }

    private val audioListener = object : AudioListener {
        override fun onAudioChunkReceived(audioChunk: AudioChunk) {
            audioChunk.apply {
                when (position) {
                    ChunkPosition.START -> {
                        lastRecording.clear()
                        data.forEach {
                            lastRecording.add(it)
                        }
                        samplingRate = audioFormat?.samplingRate
                        encoding = audioFormat?.encoding
                        channel = audioFormat?.channel
                    }
                    ChunkPosition.MIDDLE, ChunkPosition.END -> {
                        data.forEach {
                            lastRecording.add(it)
                        }
                    }
                }
            }
        }

        override fun onAudioError(errorCause: ErrorCause) {
            setUiState(currentlyRecording = false, currentlyPlaying = false)
        }

        override fun onAudioRecordingStarted() {
            setUiState(currentlyRecording = true, currentlyPlaying = false)
        }

        override fun onAudioRecordingStopped() {
            setUiState(currentlyRecording = false, currentlyPlaying = false, recordingFinished = true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAudioBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setClickListener()
    }

    override fun onResume() {
        super.onResume()
        registerToozer()
    }

    override fun onPause() {
        super.onPause()
        toozifier.stopAudio()
        toozifier.deregister()
        audioTrack?.stop()
        setUiState(currentlyRecording = false, currentlyPlaying = false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.options_refresh, menu);
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.refresh) {
            registerToozer()
            true
        } else super.onOptionsItemSelected(item)
    }

    private fun setClickListener() {
        binding?.apply {
            btnRecordAudio.let { button ->
                button.setOnClickListener {
                    if (isRegistered) {
                        // maxDuration is measured in seconds
                        toozifier.startAudio(maxDuration = MAX_RECORDING_LENGTH)
                    }
                }
            }
            btnPlayLastRecording.setOnClickListener {
                if (lastRecording.isNotEmpty() && samplingRate != null && channel != null && encoding != null) {
                    playRecordedAudioTrack()
                    setUiState(currentlyRecording = false, currentlyPlaying = true)
                }
            }
            tvStop.setOnClickListener {
                toozifier.stopAudio()
                audioTrack?.stop()
                setUiState(currentlyPlaying = false, currentlyRecording = false, recordingFinished = true)
            }
        }
    }

    private fun sendViewToGlasses() {
        // This is just a static view that is shown in the glasses
        (LayoutInflater.from(requireContext()).inflate(R.layout.audio_tooz_view, null))?.apply {
            toozifier.updateCard(this, this, Constants.FRAME_TIME_TO_LIVE_FOREVER)
        }
    }

    private fun playRecordedAudioTrack() {
        // default is mono
        val channel = if (channel == AudioChannel.MONO) AudioFormat.CHANNEL_OUT_MONO else AudioFormat.CHANNEL_OUT_STEREO
        // default is PCM_SIGNED
        val encoding = when (encoding) {
            AudioEncoding.PCM_SIGNED -> AudioFormat.ENCODING_PCM_16BIT
            else -> throw Exception("Only PCM_SIGNED allowed in this example!")
        }

        val audioAttributesBuilder = AudioAttributes.Builder()
        audioAttributesBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC)
        val audioFormatBuilder = AudioFormat.Builder()
        audioFormatBuilder.setEncoding(encoding)
        audioFormatBuilder.setSampleRate(samplingRate!!)
        audioFormatBuilder.setChannelMask(channel)

        audioTrack = AudioTrack(
            audioAttributesBuilder.build(),
            audioFormatBuilder.build(),
            lastRecording.size,
            AudioTrack.MODE_STATIC,
            AudioManager.AUDIO_SESSION_ID_GENERATE
        )
        audioTrack!!.write(lastRecording.toByteArray(), 0, lastRecording.size)
        audioTrack!!.playbackRate = samplingRate!!
        audioTrack!!.play()
    }

    private fun registerToozer() {
        toozifier.register(requireContext(), "Audio Example", registrationListener)
    }

    private fun setUiState(currentlyRecording: Boolean, currentlyPlaying: Boolean, recordingFinished: Boolean = false) {
        binding?.apply {
            btnRecordAudio.isEnabled = !currentlyRecording && !currentlyPlaying
            btnPlayLastRecording.isEnabled = recordingFinished && !currentlyRecording && !currentlyPlaying
            if (currentlyRecording || currentlyPlaying) {
                tvStop.visibility = View.VISIBLE
                if (currentlyRecording && !pulsatorRed.isStarted) {
                    pulsatorRed.start()
                } else if (currentlyPlaying && !pulsatorGreen.isStarted) {
                    pulsatorGreen.start()
                }
            } else {
                tvStop.visibility = View.INVISIBLE
                pulsatorRed.stop()
                pulsatorGreen.stop()
            }
        }
    }
}