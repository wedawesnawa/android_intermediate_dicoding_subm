package id.example.storyapp.local.preference

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import id.example.storyapp.model.UserModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

    suspend fun saveSession(user: UserModel) {
        dataStore.edit { prefs ->
            prefs[EMAIL_KEY] = user.email
            prefs[TOKEN_KEY] = user.token
            prefs[IS_LOGIN_KEY] = true
        }
    }

    fun getSession(): Flow<UserModel> = dataStore.data.map { prefs ->
        UserModel(
            email = prefs[EMAIL_KEY] ?: "",
            token = prefs[TOKEN_KEY] ?: "",
            isLogin = prefs[IS_LOGIN_KEY] ?: false
        )
    }

    suspend fun logout() {
        dataStore.edit { it.clear() }
    }

    companion object {
        @Volatile
        private var instance: UserPreference? = null

        private val EMAIL_KEY = stringPreferencesKey("email")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val IS_LOGIN_KEY = booleanPreferencesKey("isLogin")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return instance ?: synchronized(this) {
                UserPreference(dataStore).also { instance = it }
            }
        }
    }
}
