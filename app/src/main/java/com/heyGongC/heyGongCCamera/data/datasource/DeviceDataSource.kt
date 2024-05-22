package com.heyGongC.heyGongCCamera.data.datasource

import com.heyGongC.heyGongCCamera.data.source.remote.apicalladapter.ApiResponse
import com.heyGongC.heyGongCCamera.data.source.remote.apiclient.ApiClient
import com.heyGongC.heyGongCCamera.data.source.remote.model.CheckIsRegisteredDeviceResponse
import com.heyGongC.heyGongCCamera.data.source.remote.model.GetAccessTokenResponse
import com.heyGongC.heyGongCCamera.data.source.remote.model.GetTokenRequest
import javax.inject.Inject

class DeviceDataSource @Inject constructor() {

    @Inject
    lateinit var apiClient: ApiClient

    suspend fun checkIsRegisteredDevice(): ApiResponse<CheckIsRegisteredDeviceResponse> {
        return apiClient.checkIsRegisteredDevice()
    }



    suspend fun getAccessToken(deviceId: String,
                               modelName: String,
                               deviceOs: String,
                               fcmToken: String
    ): ApiResponse<GetAccessTokenResponse> {
        return apiClient.getAccessToken(GetTokenRequest(deviceId, modelName,deviceOs, fcmToken))
    }
}