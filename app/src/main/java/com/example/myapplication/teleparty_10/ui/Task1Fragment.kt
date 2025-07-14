package com.example.myapplication.teleparty_10.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.example.myapplication.teleparty_10.databinding.LayoutFragmentTask1Binding
import com.example.myapplication.teleparty_10.viewmodel.PlayerViewModel

class Task1Fragment : Fragment() {

    private var _binding: LayoutFragmentTask1Binding? = null
    private val binding get() = _binding!!
    private var player: ExoPlayer? = null
    private lateinit var trackSelector: DefaultTrackSelector
    private val viewModel: PlayerViewModel by viewModels()

    companion object {
        private const val DRM_URL = "https://bitmovin-a.akamaihd.net/content/art-of-motion_drm/mpds/11331.mpd"
        private const val DRM_LICENSE_URL = "https://cwip-shaka-proxy.appspot.com/no_auth"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LayoutFragmentTask1Binding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        trackSelector = DefaultTrackSelector(requireContext()).apply {
            setParameters(buildUponParameters().setForceHighestSupportedBitrate(false))
        }

        player = ExoPlayer.Builder(requireContext())
            .setTrackSelector(trackSelector)
            .build()
        binding.playerView.player = player

        val mediaItem = MediaItem.Builder()
            .setUri(DRM_URL.toUri())
            .setDrmConfiguration(
                MediaItem.DrmConfiguration.Builder(C.WIDEVINE_UUID)
                    .setLicenseUri(DRM_LICENSE_URL)
                    .build()
            )
            .build()

        player!!.addListener(object : Player.Listener {
            override fun onTracksChanged(tracks: Tracks) {
                viewModel.updateTracks(tracks)
            }
        })

        binding.resolutionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                viewModel.selectTrackAt(position)
                if (position != 0) binding.resolutionSpinner.setSelection(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        viewModel.resolutions.observe(viewLifecycleOwner) { labels ->
            binding.resolutionSpinner.visibility = View.VISIBLE
            if (binding.resolutionSpinner.adapter == null) {
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, labels)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.resolutionSpinner.adapter = adapter
            }
        }

        viewModel.selectedTrack.observe(viewLifecycleOwner) { override ->
            override?.let {
                val paramsBuilder = trackSelector.parameters.buildUpon()
                paramsBuilder.addOverride(it)
                trackSelector.setParameters(paramsBuilder.build())
            }
        }

        player!!.setMediaItem(mediaItem)
        player!!.prepare()
        player!!.playWhenReady = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.resolutionSpinner.onItemSelectedListener = null
        player?.release()
        player = null
        _binding = null
    }
}