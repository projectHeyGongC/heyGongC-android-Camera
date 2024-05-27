package com.heyGongC.heyGongCCamera.ui.streaming

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.zxing.BarcodeFormat
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.heyGongC.heyGongCCamera.MainViewModel
import com.heyGongC.heyGongCCamera.databinding.FragmentStreamingBinding
import com.journeyapps.barcodescanner.BarcodeEncoder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StreamingFragment : Fragment() {

    private var _binding: FragmentStreamingBinding? = null
    private val binding: FragmentStreamingBinding
        get() = _binding!!
    private val mainViewModel: MainViewModel by activityViewModels()
    private val streamingViewModel: StreamingViewModel by viewModels()
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                viewLifecycleOwner.lifecycleScope.launch {
                    streamingViewModel.startRecording()
                }
            } else {
                //Permission Denied
            }
        }
    private val cameraPermissionListener: PermissionListener = object : PermissionListener {
        override fun onPermissionGranted() {
            startStreaming()
        }

        override fun onPermissionDenied(deniedPermissions: List<String>) {
            Toast.makeText(
                requireContext(),
                "카메라 권한이 거부되었습니다.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun startStreaming() {
        TODO("Not yet implemented")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStreamingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isRegisteredDevice()
        getDeviceInfo()
        handleSoundSensing()
        handleFlash()
    }

    private fun isRegisteredDevice() {
        viewLifecycleOwner.lifecycleScope.launch {
            streamingViewModel.isRegisteredDevice.collectLatest { isRegistered ->
                binding.groupScanQr.isVisible = !isRegistered
                binding.streaming.isVisible = isRegistered
                if (isRegistered) {
                    //TODO()카메라 권한 확인 후 카메라 켜서 스트리밍
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            streamingViewModel.savedAccessToken.collectLatest {
                //로컬에 accessToken 저장
                if (it) {
                    binding.groupScanQr.isVisible = false
                    binding.streaming.isVisible = true
                }
            }
        }
    }

    fun checkPermission() {
        TedPermission.create()
            .setPermissionListener(cameraPermissionListener)
            .setDeniedMessage("권한이 거부되었습니다. 설정 > 권한에서 허용해주세요.")
            .setPermissions(Manifest.permission.CAMERA)
            .check()
    }

    private fun handleFlash() {
        viewLifecycleOwner.lifecycleScope.launch {
            mainViewModel.isFlashOn.collectLatest {
                val cameraManager =
                    context?.getSystemService(Context.CAMERA_SERVICE) as CameraManager
                val cameraId = cameraManager.cameraIdList[0]
                cameraManager.setTorchMode(cameraId, it)
            }
        }
    }

    private fun handleSoundSensing() {
        viewLifecycleOwner.lifecycleScope.launch {
            mainViewModel.isRecordingOn.collectLatest {
                if (it) {
                    checkPermissionAndRecord()
                } else {
                    viewLifecycleOwner.lifecycleScope.launch {
                        streamingViewModel.stopRecording()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            //TODO() 녹음본 서버로 보내기
            //TODO() 소리 감지 발생할 때마다 서버로 보내기
        }
    }

    private fun checkPermissionAndRecord() {
        if (context == null) return
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        } else {
            viewLifecycleOwner.lifecycleScope.launch {
                streamingViewModel.startRecording()
            }
        }
    }

    private fun getDeviceInfo() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            val deviceToken = task.result
            val deviceUuid =
                Settings.Secure.getString(context?.contentResolver, Settings.Secure.ANDROID_ID)
            val deviceModel = Build.MODEL
            generateQRCode(deviceToken, deviceUuid, deviceModel)
            viewLifecycleOwner.lifecycleScope.launch {
                streamingViewModel.getAccessToken(deviceUuid, deviceModel, "AOS", deviceToken)
            }
        })
    }

    private fun generateQRCode(deviceToken: String, deviceUuid: String, deviceModel: String) {
        val barcodeEncoder = BarcodeEncoder()
        val barcodeBitmap = barcodeEncoder.encodeBitmap(
            deviceToken + "\\" + deviceUuid + "\\" + deviceModel,
            BarcodeFormat.QR_CODE,
            148,
            148
        )
        binding.ivQrCode.setImageBitmap(barcodeBitmap)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}