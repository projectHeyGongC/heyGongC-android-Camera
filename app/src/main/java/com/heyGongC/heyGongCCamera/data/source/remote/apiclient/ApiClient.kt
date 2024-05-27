package com.heyGongC.heyGongCCamera.data.source.remote.apiclient

import com.heyGongC.heyGongCCamera.data.source.remote.apicalladapter.ApiResponse
import com.heyGongC.heyGongCCamera.data.source.remote.model.CheckIsRegisteredDeviceResponse
import com.heyGongC.heyGongCCamera.data.source.remote.model.CommonResponse
import com.heyGongC.heyGongCCamera.data.source.remote.model.GetAccessTokenResponse
import com.heyGongC.heyGongCCamera.data.source.remote.model.GetTokenRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiClient {

    @GET("v1/cameras/sound/occur")
    suspend fun sendSoundOccur(): ApiResponse<CommonResponse>

    @GET("v1/cameras/status")
    suspend fun checkIsRegisteredDevice(): ApiResponse<CheckIsRegisteredDeviceResponse>

    @POST("v1/cameras/subscribe")
    suspend fun getAccessToken(@Body body: GetTokenRequest): ApiResponse<GetAccessTokenResponse>
}