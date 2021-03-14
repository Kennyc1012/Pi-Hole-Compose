package com.kennyc.pi_hole

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kennyc.data.pi_hole.PiholeRepository
import com.kennyc.data.pi_hole.model.PiholeSummary
import com.kennyc.data.pi_hole.model.PiholeSystemStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MainViewModel @Inject constructor(private val repo: PiholeRepository) : ViewModel() {

    private val _stats = MutableLiveData<Pair<PiholeSystemStatus, PiholeSummary>>()

    val stats: LiveData<Pair<PiholeSystemStatus, PiholeSummary>> = _stats

    init {
        viewModelScope.launch(Dispatchers.IO) {
            fetchStats()
        }
    }

    fun enablePihole() {
        viewModelScope.launch {
            repo.enable()
            fetchStats()
        }
    }

    fun disablePihole(durationInSeconds: Int?) {
        viewModelScope.launch {
            repo.disablePihole(durationInSeconds)
            fetchStats()
        }
    }

    private suspend fun fetchStats() {
        val status = repo.getSystemStatus()
        val summary = repo.getSummary()

        withContext(Dispatchers.Main) {
            _stats.value = Pair(status, summary)
        }
    }
}