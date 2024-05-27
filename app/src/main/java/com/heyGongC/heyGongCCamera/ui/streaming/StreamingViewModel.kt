package com.heyGongC.heyGongCCamera.ui.streaming

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heyGongC.heyGongCCamera.data.repository.DeviceRepository
import com.heyGongC.heyGongCCamera.ui.record.PCMDataProcessor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("MissingPermission")
@HiltViewModel
class StreamingViewModel @Inject constructor(
    private val deviceRepository: DeviceRepository
) : ViewModel() {

    private val _isRegisteredDevice = MutableSharedFlow<Boolean>()
    val isRegisteredDevice = _isRegisteredDevice.asSharedFlow()
    private val _savedAccessToken = MutableSharedFlow<Boolean>()
    val savedAccessToken = _savedAccessToken.asSharedFlow()
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _isError = MutableStateFlow(false)
    val isError: StateFlow<Boolean> = _isError
    private val SAMPLE_RATE = 48000
    private var audioRecord: AudioRecord
    private var minBufferSize: Int
    private var buffer: ShortArray
    private val recordingData = mutableListOf<Short>()
    private val _soundData = MutableStateFlow<List<Double>>(emptyList())
    val soundData: StateFlow<List<Double>> get() = _soundData

    init {
        minBufferSize = AudioRecord.getMinBufferSize(
            SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT
        )

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.VOICE_RECOGNITION, SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, minBufferSize
        )
        buffer = ShortArray(minBufferSize)
    }

    fun checkIsRegisteredDevice() {
        viewModelScope.launch {
            deviceRepository.checkIsRegisteredDevice(
                onComplete = { _isLoading.value = false },
                onError = { _isError.value = true }
            ).collectLatest {
                _isRegisteredDevice.emit(it)
            }
        }
    }

    fun getAccessToken(
        deviceId: String,
        modelName: String,
        deviceOs: String,
        fcmToken: String
    ) {
        viewModelScope.launch {
            deviceRepository.getAccessToken(
                onComplete = { _isLoading.value = false },
                onError = { _isError.value = true },
                deviceId, modelName, deviceOs, fcmToken
            ).collectLatest {
                saveLocalAccessToken(it)
            }
        }
    }

    private fun saveLocalAccessToken(accessToken: String) {
        viewModelScope.launch {
            deviceRepository.saveLocalAccessToken(accessToken).collectLatest {
                _savedAccessToken.emit(true)
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startRecording() {
        buffer = ShortArray(minBufferSize)
        recordingData.clear()
        viewModelScope.launch {
            audioRecord.startRecording()
            audioRecord.read(buffer, 0, buffer.size)
            recordingData.addAll(buffer.toList())
        }
    }

    fun stopRecording() {
        audioRecord.stop()
        viewModelScope.launch {
            val processor = PCMDataProcessor(recordingData.toList())
            val topAvg = processor.calculateAverageOfTopTenPercent()
            _soundData.emit(listOf(topAvg))
            recordingData.clear()
        }
    }
}