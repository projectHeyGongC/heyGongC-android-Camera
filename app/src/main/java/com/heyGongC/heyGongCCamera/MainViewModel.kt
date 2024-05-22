package com.heyGongC.heyGongCCamera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heyGongC.heyGongCCamera.data.repository.DeviceRepository
import com.heyGongC.heyGongCCamera.util.Constants.ON
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val deviceRepository: DeviceRepository
) : ViewModel() {

    private val _hasLocalAccessToken = MutableSharedFlow<Boolean>()
    val hasLocalAccessToken: SharedFlow<Boolean> = _hasLocalAccessToken
    private val _isRecordingOn = MutableSharedFlow<Boolean>()
    val isRecordingOn: SharedFlow<Boolean> = _isRecordingOn
    private val _isFlashOn = MutableStateFlow(false)
    val isFlashOn: StateFlow<Boolean> = _isFlashOn

    fun handleSoundSensing(content: String?) {
        viewModelScope.launch {
            _isRecordingOn.emit(content == ON)
        }
    }

    fun handleFlash(content: String?) {
        _isFlashOn.value = content == ON
    }

    fun getLocalAccessToken() {
        viewModelScope.launch {
            deviceRepository.hasLocalAccessToken().collectLatest {
                _hasLocalAccessToken.emit(it)
            }
        }
    }
}