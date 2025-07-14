package com.example.myapplication.teleparty_10.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.teleparty_10.R
import com.example.myapplication.teleparty_10.databinding.LayoutFragmentTask2Binding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class Task2Fragment : Fragment(R.layout.layout_fragment_task2) {

    private var _binding: LayoutFragmentTask2Binding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = LayoutFragmentTask2Binding.bind(view)
        val entityId = "amzn1.dv.gti.37a8bbd7-c9e0-49c9-87e9-216f5fb21f50"
        fetchPrimeVideoMetadata(entityId)
    }

    private fun fetchPrimeVideoMetadata(entityId: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    val url =
                        "https://atv-ps-eu.primevideo.com/cdp/lumina/playerChromeResources/v1?deviceID=22c625b20b67088005a96445b267caf2267334682f70faa14209d596&deviceTypeID=AOAGZA014O5RE&gascEnabled=true&marketplaceID=A2MFUE2XK8ZSSY&uxLocale=en_US&desiredResources=catalogMetadataV2%2CinPlaybackRatingV3&entityId=$entityId&firmware=1&widgetScheme=pvplayer-web-v1&nerid=ZgEOJ1%2BrLxv%2B5SUAdQREur00"
                    val client = OkHttpClient()
                    val request = Request.Builder()
                        .url(url)
                        .addHeader("User-Agent", "Mozilla/5.0")
                        .addHeader(
                            "Cookie",
                            "session-token=RgY0uOr3/zAr6Xy3eRq3Ufusq55WHL3/Q7mc41q85V3h5+a4UmeTkBoO7kpS4mZIcfTGMHIvYGucuKRa2uHoYoseHFr6Eb8qrTbZ7OMwG647dQWb1k8L5/+O+1bq807zqG/AvK0ry4UPBnG21XghoL0BhmCom4EonqcZKHo0hQHlvLvPnZnCZrvmfcyVaDOSagQrhBDrwJ4KMd1sn7NOkCwvgEkceQub8K0MlXpcKPIkAegyRHo9/PClEaXcZkm/CNzxaRMTecShnCunU1N4lVxI1s/x5wTdWD/y02/nevOiHUEGM8UCynqVdUlqi7M0jtp4NNyiJy72AbS9nylkB4La02fzCtd2BsxBdsWoPuN//XkzfKnWok7mGIPSS3D0E5GDB9eB750="
                        )
                        .build()
                    val response = client.newCall(request).execute()
                    if (!response.isSuccessful) {
                        return@withContext Result.failure(Exception("Server returned error: ${response.code}"))
                    }
                    val body = response.body.string()
                    Result.success(body)
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }

            result.fold(
                onSuccess = { body ->
                    try {
                        val json = JSONObject(body)
                        val resources = json.getJSONObject("resources")

                        val catalog =
                            resources.getJSONObject("catalogMetadataV2").getJSONObject("catalog")
                        val ratingInfo =
                            resources.getJSONObject("inPlaybackRatingV3").getJSONObject("ratingInfo")

                        val title = catalog.optString("title", "Unknown")
                        val series = catalog.optString("seriesTitle", "Unknown")
                        val season = catalog.optInt("seasonNumber", -1)
                        val episode = catalog.optInt("episodeNumber", -1)
                        val rating = ratingInfo.optString("ratingDisplayText", "N/A")

                        val entityType = catalog.optString("entityType", "N/A")
                        val type = catalog.optString("type", "N/A")
                        val languages = catalog.optJSONArray("originalLanguages")?.join(", ") ?: "Unknown"

                        Log.i("primeVideo", "Title: $title, Series: $series, Season $season, Episode $episode, Rating: $rating, Type: $type, Entity: $entityType, Languages: $languages")

                        binding.tvTitle.text = getString(R.string.string_title_log, title)
                        binding.tvSeries.text = getString(R.string.string_series, series)
                        binding.tvSeasonEpisode.text =
                            getString(R.string.string_season_episode, season, episode)
                        binding.tvRating.text = getString(R.string.string_rating, rating)
                        binding.tvType.text =
                            getString(R.string.string_type_entity, type, entityType)
                        binding.tvLanguages.text = getString(R.string.string_language, languages)
                    } catch (e: Exception) {
                        updateUiWithError("Invalid data format")
                    }
                },
                onFailure = { e ->
                    updateUiWithError(e.message ?: "Unknown error")
                }
            )
        }
    }

    private fun updateUiWithError(message: String) {
        binding.tvTitle.text = message
        binding.tvSeries.text = ""
        binding.tvSeasonEpisode.text = ""
        binding.tvRating.text = ""
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}