package com.shreyd.co2tracker.datastore

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.shreyd.co2tracker.application.MyApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Created by Jyotish Biswas on 26,August,2023
 */
private const val USER_DATA_STORE = "user-data-store"
private const val PREF_KEY_USER_ID = "user-id-key"
private const val PREF_KEY_AUTH_TOKEN = "auth-token-key"


class UserDataStore private constructor(private val context: Context) {

    private val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = USER_DATA_STORE)

    private val userIdPrefKey = stringPreferencesKey(name = PREF_KEY_USER_ID)
    private val authTokenPrefKey = stringPreferencesKey(name = PREF_KEY_AUTH_TOKEN)


    suspend fun setUserId(value: String) {
        context.datastore.edit {
            it[userIdPrefKey]= value
        }
    }

    val userIdFlow = context.datastore.data.map {
        it[userIdPrefKey]
    }.filterNotNull()

    suspend fun getUserId() = context.datastore.data.map {
        it[userIdPrefKey]
    }.firstOrNull()

    suspend fun setAuthToken(value: String) {
        context.datastore.edit {
            it[authTokenPrefKey]= value
        }
    }

    val authTokenFlow = context.datastore.data.map {
        it[authTokenPrefKey]
    }.filterNotNull()

    suspend fun getAuthToken() = context.datastore.data.map {
        it[authTokenPrefKey]
    }.firstOrNull()



    fun clearData() {
        CoroutineScope(Dispatchers.Main).launch {
            context.datastore.edit { it.clear() }
        }
    }

    companion object {

        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: UserDataStore? = null

        fun getInstance() = instance ?: synchronized(this) {
            instance ?: UserDataStore(MyApplication.instance).also {
                instance = it
            }
        }
    }
}