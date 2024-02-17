package com.ranicorp.heygongccamera.ui.qr

import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.ranicorp.heygongccamera.databinding.FragmentQrBinding

class QrFragment : Fragment() {

    private var _binding: FragmentQrBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDeviceInfo()
    }

    private fun getDeviceInfo() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            val deviceToken = task.result
            val deviceUuid =
                Settings.Secure.getString(context?.contentResolver, Settings.Secure.ANDROID_ID)
            val deviceModel = Build.MODEL
            generateQRCode(deviceToken, deviceUuid, deviceModel)
        })
    }

    private fun generateQRCode(deviceToken: String, deviceUuid: String, deviceModel: String) {
        val barcodeEncoder = BarcodeEncoder()
        val barcodeBitmap = barcodeEncoder.encodeBitmap(
            deviceToken + deviceUuid + deviceModel,
            BarcodeFormat.QR_CODE,
            148,
            148
        )
        binding.ivQrCode.setImageBitmap(barcodeBitmap)
    }
}