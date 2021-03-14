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
import java.net.UnknownHostException
import javax.inject.Inject

class MainViewModel @Inject constructor(private val repo: PiholeRepository) : ViewModel() {

    private val _stats = MutableLiveData<Pair<PiholeSystemStatus, PiholeSummary>>()
    val stats: LiveData<Pair<PiholeSystemStatus, PiholeSummary>> = _stats

    private val _statusError = MutableLiveData<Unit>()
    val statusError: LiveData<Unit> = _statusError

    private val _errorMessage = MutableLiveData<Int>()
    val errorMessage: LiveData<Int> = _errorMessage

    init {
        refresh()
    }

    fun enablePihole() {
        viewModelScope.launch {
            val result = repo.enable()

            if (result) {
                fetchStats()
            } else {
                withContext(Dispatchers.Main) {
                    _statusError.value = Unit
                }
            }
        }
    }

    fun disablePihole(durationInSeconds: Int?) {
        viewModelScope.launch {
            val result = repo.disablePihole(durationInSeconds)

            if (result) {
                fetchStats()
            } else {
                withContext(Dispatchers.Main) {
                    _statusError.value = Unit
                }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            fetchStats()
        }
    }

    private suspend fun fetchStats() {
        try {
            val status = repo.getSystemStatus()
            val summary = repo.getSummary()

            withContext(Dispatchers.Main) {
                _errorMessage.value = -1
                _stats.value = Pair(status, summary)
            }
        } catch (e: Exception) {
            val msg = when (e) {
                is UnknownHostException -> R.string.error_not_found
                else -> R.string.error_generic
            }

            withContext(Dispatchers.Main) {
                _errorMessage.value = msg
            }
        }
    }
}