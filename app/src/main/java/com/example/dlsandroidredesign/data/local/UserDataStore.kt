package com.example.dlsandroidredesign.data.local

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.dlsandroidredesign.domain.entity.User
import com.example.dlsandroidredesign.domain.entity.Waypointgroup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("UserPref")
        private val user = stringPreferencesKey("user")
        private val isLoginSuccessful = booleanPreferencesKey("loginStatus")
        private val cusText = stringPreferencesKey("cusText")
        private val checkboxList = stringPreferencesKey("checkboxList")
        private val isAutomaticUpload = booleanPreferencesKey("autoUpload")
    }
    suspend fun setIsAutomaticUpload(isAutomaticUploadSInput: Boolean) {
        context.dataStore.edit { it[isAutomaticUpload] = isAutomaticUploadSInput }
    }

    var getIssAutomaticUpload = context.dataStore.data.map { it[isAutomaticUpload] ?: false }

    suspend fun setCheckboxList(checkBoxListInput: List<Boolean>) {
        Log.d("checkbox", "Set $checkBoxListInput")
        val serializedList = Gson().toJson(checkBoxListInput)
        context.dataStore.edit { preferences ->
            preferences[checkboxList] = serializedList
        }
        Log.d("checkbox", "Get $serializedList")
    }

    fun getCheckboxList(): Flow<List<Boolean>> {
        return context.dataStore.data.map { preferences ->
            val serializedData = preferences[checkboxList]
            Log.d("checkbox", "Get $serializedData")
            val settingCheckboxType = object : TypeToken<List<Boolean>>() {}.type
            Log.d("checkbox", "Get ${Gson().fromJson(serializedData,settingCheckboxType) ?: listOf(true,true,true,true,true,true,true,true,true)}")
            Gson().fromJson(serializedData, settingCheckboxType) ?: listOf(true, true, true, true, true, true, true, true, true)
        }
    }

    suspend fun setGroupIdAndName(groupIdCheck: String, groupName: String) {
        val currentUser = getUser().first()
        currentUser!!.groupIdCheck = groupIdCheck
        currentUser!!.groupNameCheck = groupName
        context.dataStore.edit { it[user] = Gson().toJson(currentUser) }
    }
    suspend fun setUser(userId: String, userName: String, waypointGroups: List<Waypointgroup>, groupIdCheck: String, groupNameCheck: String) {
        context.dataStore.edit { it[user] = Gson().toJson(User(userId, userName, waypointGroups, groupIdCheck, groupNameCheck)) }
    }

    fun getUser(): Flow<User?> {
        return context.dataStore.data.map { preferences ->
            val serializedData = preferences[user]
            Gson().fromJson(serializedData, User::class.java)
        }
    }

    suspend fun setIsLoginSuccessful(isSuccessFullInput: Boolean) {
        context.dataStore.edit { it[isLoginSuccessful] = isSuccessFullInput }
    }

    var getIsLoginSuccess = context.dataStore.data.map { it[isLoginSuccessful] ?: false }

    suspend fun setCusText(cusTextInput: String) {
        context.dataStore.edit { it[cusText] = cusTextInput }
    }

    var getCusText = context.dataStore.data.map { it[cusText] ?: "" }
}
