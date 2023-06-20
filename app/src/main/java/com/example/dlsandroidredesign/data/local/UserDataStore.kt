package com.example.dlsandroidredesign.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.dlsandroidredesign.domain.entity.User
import com.example.dlsandroidredesign.domain.entity.Waypointgroup
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserDataStore @Inject constructor(
    @ApplicationContext private val context: Context) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("UserPref")
        private val user = stringPreferencesKey("user")
    }

    suspend fun setUser(userId: String, waypointGroups: List<Waypointgroup>) {
        context.dataStore.edit { it[user] = Gson().toJson(User(userId, waypointGroups)) }
    }

    fun getUser(): Flow<User?> {
        return context.dataStore.data.map { preferences ->
            val serializedData = preferences[user]
            Gson().fromJson(serializedData, User::class.java)
        }
    }
}

