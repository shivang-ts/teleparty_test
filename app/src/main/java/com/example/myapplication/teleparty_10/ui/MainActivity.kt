package com.example.myapplication.teleparty_10.ui

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.example.myapplication.teleparty_10.R
import com.example.myapplication.teleparty_10.databinding.LayoutActivityMainBinding
import com.example.myapplication.teleparty_10.viewmodel.PlayerViewModel

@UnstableApi
class MainActivity : AppCompatActivity() {

    private lateinit var binding: LayoutActivityMainBinding
    private lateinit var player: ExoPlayer
    private lateinit var trackSelector: DefaultTrackSelector
    private val viewModel: PlayerViewModel by viewModels()

    companion object {
        private const val DRM_URL = "https://bitmovin-a.akamaihd.net/content/art-of-motion_drm/mpds/11331.mpd"
        private const val DRM_LICENSE_URL = "https://cwip-shaka-proxy.appspot.com/no_auth"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.layout_activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        trackSelector = DefaultTrackSelector(this).apply {
            setParameters(buildUponParameters().setForceHighestSupportedBitrate(false))
        }

        player = ExoPlayer.Builder(this)
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


        player.addListener(object : Player.Listener {
            override fun onTracksChanged(tracks: Tracks) {
                viewModel.updateTracks(tracks)
            }
        })

        viewModel.resolutions.observe(this, Observer { labels ->
            binding.resolutionSpinner.visibility = View.VISIBLE
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, labels)
            binding.resolutionSpinner.adapter = adapter
            binding.resolutionSpinner.setSelection(0, false)

            binding.resolutionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    viewModel.selectTrackAt(position)
                }
                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        })

        viewModel.selectedTrack.observe(this, Observer { override ->
            override?.let {

                val paramsBuilder = trackSelector.parameters.buildUpon()
                paramsBuilder.addOverride(it)
                trackSelector.setParameters(paramsBuilder.build())
            }
        })

        player.setMediaItem(mediaItem)
        player.prepare()
        player.playWhenReady = true
    }

    override fun onStop() {
        super.onStop()
        player.release()
    }
}