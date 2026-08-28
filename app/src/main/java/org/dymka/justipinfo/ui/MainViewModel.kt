package org.dymka.justipinfo.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.dymka.justipinfo.data.AppRepository

class MainViewModel(
    private val repository: AppRepository,
) : ViewModel() {
    private val _logs = MutableStateFlow<List<String>>(emptyList())
    val logs: StateFlow<List<String>> = _logs.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(repository.isDarkTheme())
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _baseUrl = MutableStateFlow(repository.getBaseUrl())
    val baseUrl: StateFlow<String> = _baseUrl.asStateFlow()

    init {
        loadLogs()
    }

    private fun loadLogs() {
        viewModelScope.launch { _logs.value = repository.getLogs() }
    }

    fun onRequestClicked() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _logs.value = repository.getAndLogIpInfo()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onClearClicked() {
        viewModelScope.launch { _logs.value = repository.clearLogs() }
    }

    fun onDeleteLogEntry(index: Int) {
        viewModelScope.launch { _logs.value = repository.deleteLogEntry(index) }
    }

    fun toggleDarkTheme(isDark: Boolean) {
        _isDarkTheme.value = isDark
        repository.setDarkTheme(isDark)
    }

    fun updateBaseUrl(url: String) {
        _baseUrl.value = url
        repository.setBaseUrl(url)
    }

    fun resetBaseUrl() {
        updateBaseUrl(AppRepository.DEFAULT_URL)
    }

    companion object {
        fun factory(repository: AppRepository): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    MainViewModel(repository)
                }
            }
    }
}
