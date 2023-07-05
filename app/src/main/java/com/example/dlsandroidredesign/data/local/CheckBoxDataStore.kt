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
        val latLon = booleanPreferencesKey(CheckBoxKey.LatLon.name)
        val elevation = booleanPreferencesKey(CheckBoxKey.Elevation.name)
        val gridLocation = booleanPreferencesKey(CheckBoxKey.GridLocation.name)
        val distance = booleanPreferencesKey(CheckBoxKey.Distance.name)
        val utmCoordinate = booleanPreferencesKey(CheckBoxKey.UtmCoordinate.name)
        val bearing = booleanPreferencesKey(CheckBoxKey.Bearing.name)
        val address = booleanPreferencesKey(CheckBoxKey.Address.name)
        val date = booleanPreferencesKey(CheckBoxKey.Date.name)
        val cusText = booleanPreferencesKey(CheckBoxKey.CusText.name)
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
