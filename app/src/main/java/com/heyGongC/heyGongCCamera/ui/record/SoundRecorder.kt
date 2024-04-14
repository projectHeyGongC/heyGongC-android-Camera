package com.heyGongC.heyGongCCamera.ui.record

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SoundRecorder(activity: Activity, private val coroutineScope: CoroutineScope) {
    companion object {
        const val SAMPLE_RATE = 48000
    }
    private lateinit var audioRecord: AudioRecord
    private lateinit var buffer: ShortArray
    private val recordingData = mutableListOf<Short>()
    private val _soundData = MutableStateFlow<List<Double>>(emptyList())
    val soundData: StateFlow<List<Double>> get() = _soundData

    private var _isRecording = false
    val isRecording: Boolean
        get() = this._isRecording

    init {
        val minBufferSize = AudioRecord.getMinBufferSize(
            SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT
        )
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity, arrayOf(Manifest.permission.RECORD_AUDIO), 0
            )
        } else {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.VOICE_RECOGNITION, SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, minBufferSize
            )
            buffer = ShortArray(minBufferSize)
        }
    }

    fun startRecording() {
        _isRecording = true
        recordingData.clear()        // clear recording data for the next recording
        Thread(Runnable {
            audioRecord.startRecording()
            while (_isRecording) {
                audioRecord.read(buffer, 0, buffer.size)
                recordingData.addAll(buffer.toList())
            }
        }).start()
    }

    fun stopRecording() {
        _isRecording = false
        audioRecord.stop()

        coroutineScope.launch(Dispatchers.Default) {
            val processor = PCMDataProcessor(recordingData.toList())
            val topAvg = processor.calculateAverageOfTopTenPercent()

            // Update your StateFlow with the new value
            _soundData.emit(listOf(topAvg))  // Assuming you want to emit the single topAvg value as a list

            Log.d("DataSize", "Size of recordingData: ${recordingData.size}")
            recordingData.clear() // Clear the data
        }
    }
}