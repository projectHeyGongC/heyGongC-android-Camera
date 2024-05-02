package com.heyGongC.heyGongCCamera.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.content.Intent
import android.util.Log
import com.google.gson.Gson
import com.heyGongC.heyGongCCamera.MainActivity
import com.heyGongC.heyGongCCamera.util.Constants.ACTION
import com.heyGongC.heyGongCCamera.util.Constants.CONTENT
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        Log.e("TAG", "onMessageReceived: ${Gson().toJson(message)}")

        val action = message.data[ACTION]
        val content = message.data[CONTENT]

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra(ACTION, action)
        intent.putExtra(CONTENT, content)
        startActivity(intent)
    }
}