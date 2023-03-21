package com.github.pakka_papad.whatsnew

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.pakka_papad.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class WhatsNewViewModel @Inject constructor(
    private val context: Application,
) : ViewModel() {

    private val _changelogsFlow = MutableStateFlow<Resource<List<Changelog>>>(Resource.Idle())
    val changelogsFlow = _changelogsFlow.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                val jsonAsString = loadJSONFromAsset("changelogs.json")
                    ?: throw RuntimeException("Changelogs not found")
                _changelogsFlow.update { Resource.Loading() }
                val jsonChangelogs = JSONArray(jsonAsString)
                val changelogs = ArrayList<Changelog>()
                for (i in 0 until jsonChangelogs.length()) {
                    val jsonChangelog = jsonChangelogs[i] as JSONObject
                    val jsonChanges = jsonChangelog.getJSONArray("changes")
                    val changes = ArrayList<String>()
                    for (j in 0 until jsonChanges.length()) {
                        changes += jsonChanges[j] as String
                    }
                    changelogs += Changelog(
                        versionCode = jsonChangelog.getInt("versionCode"),
                        versionName = jsonChangelog.getString("versionName"),
                        date = jsonChangelog.getString("date"),
                        changes = changes
                    )
                }
                _changelogsFlow.update { Resource.Success(data = changelogs.sortedByDescending { it.versionCode }) }
            } catch (e: Exception) {
                Timber.e(e)
                _changelogsFlow.update { Resource.Error("Error processing data") }
            }
        }
    }

    private fun loadJSONFromAsset(fileName: String): String? {
        return try {
            val inputStream = context.assets.open(fileName)
            val result = String(inputStream.readBytes(), Charsets.UTF_8)
            inputStream.close()
            result
        } catch (e: Exception) {
            Timber.e(e)
            null
        }
    }

}