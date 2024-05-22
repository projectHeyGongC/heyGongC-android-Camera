package com.heyGongC.heyGongCCamera.data.repository

import com.heyGongC.heyGongCCamera.data.datasource.DeviceDataSource
import com.heyGongC.heyGongCCamera.data.source.local.LocalDataStoreManager
import com.heyGongC.heyGongCCamera.data.source.remote.onError
import com.heyGongC.heyGongCCamera.data.source.remote.onException
import com.heyGongC.heyGongCCamera.data.source.remote.onSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import javax.inject.Inject

class DeviceRepository @Inject constructor(
    private val deviceDataSource: DeviceDataSource,
    private val dataStoreManager: LocalDataStoreManager
) {

    suspend fun checkIsRegisteredDevice(
        onComplete: () -> Unit,
        onError: () -> Unit
    ): Flow<Boolean> = flow {
        val response = deviceDataSource.checkIsRegisteredDevice()
        response.onSuccess { response ->
            if (response?.isConnected == null) {
                onError()
            } else {
                emit(response.isConnected)
            }
        }.onError { code, message ->
            onError()
        }.onException {
            onError()
        }
    }.onCompletion {
        onComplete()
    }

    suspend fun getAccessToken(
        onComplete: () -> Unit,
        onError: () -> Unit,
        deviceId: String,
        modelName: String,
        deviceOs: String,
        fcmToken: String
    ): Flow<String> = flow {
        val response = deviceDataSource.getAccessToken(deviceId, modelName, deviceOs, fcmToken)
        response.onSuccess { response ->
            if (response == null) {
                onError()
            } else {
                emit(response.accessToken)
            }
        }.onError { code, message ->
            onError()
        }.onException {
            onError()
        }
    }.onCompletion {
        onComplete()
    }

    suspend fun hasLocalAccessToken(): Flow<Boolean> = flow {
        emit(dataStoreManager.getAccessToken().isNotEmpty())
    }
}