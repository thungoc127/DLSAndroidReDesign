package com.example.dlsandroidredesign.data.local

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.example.dlsandroidredesign.data.CheckBoxKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CheckBoxDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("CheckBoxPref")
        val latLon = booleanPreferencesKey(CheckBoxKey.latlon.name)
        val elevation = booleanPreferencesKey(CheckBoxKey.elevation.name)
        val gridLocation = booleanPreferencesKey(CheckBoxKey.gridLocation.name)
        val distance = booleanPreferencesKey(CheckBoxKey.distance.name)
        val utmCoordinate = booleanPreferencesKey(CheckBoxKey.utmCoordinate.name)
        val bearing = booleanPreferencesKey(CheckBoxKey.bearing.name)
        val address = booleanPreferencesKey(CheckBoxKey.address.name)
        val date = booleanPreferencesKey(CheckBoxKey.date.name)
        val cusText = booleanPreferencesKey(CheckBoxKey.cusText.name)
    }

    suspend fun setCheckBox(checkBoxKey: String, value: Boolean) {
        Log.d("checkbox", "Set $value")

        context.dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(checkBoxKey)] = value
        }
        Log.d("checkbox", "Get $value")
    }

    fun getCheckBox(): Flow<Preferences> {
        return context.dataStore.data
    }
}
