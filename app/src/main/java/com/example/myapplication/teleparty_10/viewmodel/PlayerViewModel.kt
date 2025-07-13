package com.example.myapplication.teleparty_10.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.media3.common.C
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.Tracks

class PlayerViewModel : ViewModel() {

    private val _resolutions = MutableLiveData<List<String>>()
    val resolutions: LiveData<List<String>> = _resolutions

    private val _selectedTrack = MutableLiveData<TrackSelectionOverride?>()
    val selectedTrack: LiveData<TrackSelectionOverride?> = _selectedTrack

    private val trackOverrides = mutableListOf<Pair<String, TrackSelectionOverride>>()

    fun updateTracks(tracks: Tracks) {
        val labels = mutableListOf<String>()
        trackOverrides.clear()

        for (group in tracks.groups) {
            if (group.type == C.TRACK_TYPE_VIDEO) {
                for (i in 0 until group.mediaTrackGroup.length) {
                    val format = group.mediaTrackGroup.getFormat(i)
                    val label = "${format.height}p (${format.bitrate / 1000} kbps)"
                    val override = TrackSelectionOverride(group.mediaTrackGroup, listOf(i))
                    labels.add(label)
                    trackOverrides.add(label to override)
                }
            }
        }

        _resolutions.postValue(labels)
    }

    fun selectTrackAt(position: Int) {
        _selectedTrack.postValue(trackOverrides.getOrNull(position)?.second)
    }
}
