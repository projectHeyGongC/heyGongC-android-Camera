package com.ranicorp.heygongccamera.data.source.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

abstract class DataStoreManager(private val dataStore: DataStore<Preferences>) {

    protected suspend fun removeValue(key: String) {
        val wrappedKey = stringPreferencesKey(key)
        dataStore.edit {
            it.remove(wrappedKey)
        }
    }

    protected suspend fun saveValue(key: String, value: String) {
        val wrappedKey = stringPreferencesKey(key)
        dataStore.edit {
            it[wrappedKey] = value
        }
    }

    protected suspend fun getStringValue(key: String, default: String = ""): String {
        val wrappedKey = stringPreferencesKey(key)
        val valueFlow: Flow<String> = dataStore.data.map {
            it[wrappedKey] ?: default
        }
        return valueFlow.first()
    }

    protected suspend fun saveValue(key: String, value: Boolean) {
        val wrappedKey = booleanPreferencesKey(key)
        dataStore.edit {
            it[wrappedKey] = value
        }
    }

    protected suspend fun getBooleanValue(key: String, default: Boolean = false): Boolean {
        val wrappedKey = booleanPreferencesKey(key)
        val valueFlow: Flow<Boolean> = dataStore.data.map {
            it[wrappedKey] ?: default
        }
        return valueFlow.first()
    }

    protected suspend fun clear() {
        dataStore.edit {
            it.clear()
        }
    }
}