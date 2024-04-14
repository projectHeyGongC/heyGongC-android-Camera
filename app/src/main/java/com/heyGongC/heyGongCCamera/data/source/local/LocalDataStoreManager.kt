package com.heyGongC.heyGongCCamera.data.source.local

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.heyGongC.heyGongCCamera.util.Constants.ACCESS_TOKEN
import com.heyGongC.heyGongCCamera.util.Constants.LOCAL_PREFERENCES
import com.heyGongC.heyGongCCamera.util.Constants.TERMS_AGREED_STATE
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

val Context.dataStore by preferencesDataStore(name = LOCAL_PREFERENCES)

class LocalDataStoreManager @Inject constructor(@ApplicationContext context: Context) :
    DataStoreManager(context.dataStore) {

    suspend fun getLocalTermsAgreedState() = getBooleanValue(TERMS_AGREED_STATE)

    suspend fun setLocalTermsAgreedState(agreedState: Boolean) {
        saveValue(TERMS_AGREED_STATE, agreedState)
    }

    suspend fun getAccessToken() = getStringValue(ACCESS_TOKEN)

    suspend fun setAccessToken(accessToken: String) {
        saveValue(ACCESS_TOKEN, accessToken)
    }
}