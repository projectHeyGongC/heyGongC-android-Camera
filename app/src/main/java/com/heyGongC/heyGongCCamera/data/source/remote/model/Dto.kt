package com.heyGongC.heyGongCCamera.data.source.remote.model

import com.google.gson.annotations.SerializedName

data class CommonResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("msg") val msg: String,
    @SerializedName("data") val `data`: Any?
)

data class CheckIsRegisteredDeviceResponse(
    @SerializedName("isConnected") val isConnected: Boolean
)

data class GetAccessTokenResponse(
    @SerializedName("accessToken") val accessToken: String
)

data class GetTokenRequest(
    @SerializedName("deviceId") val deviceId: String,
    @SerializedName("modelName") val modelName: String,
    @SerializedName("deviceOs") val deviceOs: String,
    @SerializedName("fcmToken") val fcmToken: String
)