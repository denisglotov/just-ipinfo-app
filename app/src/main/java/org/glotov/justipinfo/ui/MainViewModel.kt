package org.glotov.justipinfo.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.glotov.justipinfo.data.AppRepository

class MainViewModel(private val repository: AppRepository) : ViewModel() {

  private val _logs = MutableStateFlow("")
  val logs: StateFlow<String> = _logs.asStateFlow()

  private val _isLoading = MutableStateFlow(false)
  val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

  init {
    loadLogs()
  }

  private fun loadLogs() {
    viewModelScope.launch { _logs.value = repository.getLogs() }
  }

  fun onRequestClicked() {
    viewModelScope.launch {
      _isLoading.value = true
      _logs.value = repository.getAndLogIpInfo()
      _isLoading.value = false
    }
  }

  fun onClearClicked() {
    viewModelScope.launch { _logs.value = repository.clearLogs() }
  }
}

class MainViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
      @Suppress("UNCHECKED_CAST") return MainViewModel(repository) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}
