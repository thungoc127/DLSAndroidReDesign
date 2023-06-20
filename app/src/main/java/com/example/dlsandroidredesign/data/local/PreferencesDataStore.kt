package com.example.dlsandroidredesign.data.local

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.dlsandroidredesign.LoginDTO
import com.example.dlsandroidredesign.Waypointgroup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val settingMap = mapOf(
    "latlon" to true,
    "elevation" to true,
    "gridLocation" to true,
    "distance" to true,
    "utmCoordinate" to true,
    "bearing" to true,
    "address" to true,
    "date" to true
)

class PreferencesDataStore(private val context: Context){
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("Setting_preferences")
        private var isLatitudeAndLongitudeChecked = booleanPreferencesKey("isLatitudeChecked")
        private var isElevationChecked = booleanPreferencesKey("isElevationChecked")
        private var isGridLocationChecked = booleanPreferencesKey("isLocationChecked")
        private var isDistanceChecked = booleanPreferencesKey("isDistanceChecked")
        private var isHeadingChecked = booleanPreferencesKey("isHeadingChecked")
        private var isAddressChecked = booleanPreferencesKey("isAddressChecked")
        private var isdDateAndTimeChecked = booleanPreferencesKey("isDateAndTimeChecked")
        private var isUtmChecked = booleanPreferencesKey("isUtmChecked")
        private var customText = stringPreferencesKey("customText")
        private var logInState = booleanPreferencesKey("logInState")
        private var settingState = booleanPreferencesKey("SettingState")
        private var locationInfoLeft = stringPreferencesKey("locationInfoLeft")
        private var locationInfoRight = stringPreferencesKey("locationInfoRight")
        private var uploadAuto = booleanPreferencesKey("uploadAuto")
        private var isLoginSuccessful = booleanPreferencesKey("logInStatus")
        private var userName = stringPreferencesKey("userName")
        private var passWord = stringPreferencesKey("password")
        private var loginSharedInfoList = stringPreferencesKey("loginSharedInfoList")
        private var waypointgroup = stringPreferencesKey("waypointgroupcheck")
        private var waypointgroupCheck = stringPreferencesKey("groupIdCheck")
        private var settingCheckbox = stringPreferencesKey("settingCheck")
    }

    ///CheckBox
    fun getSettingCheckbox(): Flow<HashSet<String>> {
        Log.d("getCheckBox","Get")
        return context.dataStore.data.map { preferences ->
            val serializedData = preferences[settingCheckbox]
            val settingCheckboxType = object : TypeToken<HashSet<String>>() {}.type
            Gson().fromJson(serializedData, settingCheckboxType) ?: hashSetOf<String>("LatLon","Elevation","GridLocation","Distance","Heading","Address","Date","Utm","CustomText")

        }
    }

     suspend fun setSettingChheckBox(value: String, hashSet: HashSet<String>) {
        if (hashSet.contains(value)) {
            hashSet.remove(value)
            Log.d("setCheckBox","Remove")
            Log.d("setCheckBox","$hashSet")

        } else {
            hashSet.add(value)
            Log.d("setCheckBox","Set")
            Log.d("setCheckBox","$hashSet")


        }
        val newSerializedData = Gson().toJson(hashSet)
        context.dataStore.edit { preferences ->
            preferences[settingCheckbox] = newSerializedData
        }
    }


    //groupIdCheck
    suspend fun setWaypointgroupCheck(WaypointgroupCheckInput: String) {
        context.dataStore.edit { it[waypointgroupCheck] = WaypointgroupCheckInput }
    }

    var getWaypointgroupCheck =  context.dataStore.data.map { it[waypointgroupCheck] ?: null }


    //Waypoint group ch

    suspend fun setWaypointGroup(waypointGroupsInput: List<Waypointgroup>) {
        val serializedList = Gson().toJson(waypointGroupsInput)
        context.dataStore.edit { preferences ->
            preferences[waypointgroup] = serializedList
        }
    }

    fun getWaypointgroup(): Flow<List<Waypointgroup>> {
        return context.dataStore.data.map { preferences ->
            val serializedData = preferences[waypointgroup]
            val typeToken = object : TypeToken<List<Waypointgroup>>() {}.type
            Gson().fromJson<List<Waypointgroup>>(serializedData, typeToken) ?: emptyList()
        }
    }


    //isLoginSuccessful

    suspend fun setIsLoginSuccessful(isSuccessFullInput: Boolean) {
        context.dataStore.edit { it[isLoginSuccessful] = isSuccessFullInput }
    }

    var getIsLoginSuccess =  context.dataStore.data.map { it[isLoginSuccessful] ?: false }

    //UserName
    suspend fun setUsername(userNameInput: String) {
        context.dataStore.edit { it[userName] = userNameInput }
    }

    var getUsername =  context.dataStore.data.map { it[userName] ?: "" }

    //Password
    suspend fun setPassword(passwordInput: String) {
        context.dataStore.edit { it[passWord] = passwordInput }
    }

    var getPassword =  context.dataStore.data.map { it[passWord] ?: "" }



    //UploadAuto
    suspend fun setUploadAuto(uploadAutoInput: Boolean) {
        context.dataStore.edit { it[uploadAuto] = uploadAutoInput }
    }

    var getUploadAuto =  context.dataStore.data.map { it[uploadAuto] ?: false }

    //LogInStatus
    suspend fun setLogInStatus(islogInStatus: Boolean) {
        context.dataStore.edit { it[isLoginSuccessful] = islogInStatus }
    }

    var getLogInStatus =  context.dataStore.data.map { it[isLoginSuccessful] ?: false }

    //SettingState
    suspend fun setSettingState(isSettingState: Boolean) {
        context.dataStore.edit { it[settingState] = isSettingState }
    }

    var getSettingState =  context.dataStore.data.map { it[settingState] ?: true }

    //logInState
    suspend fun setlogInState(islogInState: Boolean) {
        context.dataStore.edit { it[logInState] = islogInState }
    }

    var getlogInState =  context.dataStore.data.map { it[logInState] ?: false }


    //LeftInfo
    suspend fun setLocationInfoLeft(locationInfoLeftInput: String) {
        context.dataStore.edit { it[locationInfoLeft] = locationInfoLeftInput }
    }

    var getLocationInfoLeft =  context.dataStore.data.map { it[locationInfoLeft] ?: "" }

    //RightInfo
    suspend fun setLocationInfoRight(locationInfoRightInput: String) {
        context.dataStore.edit { it[locationInfoRight] = locationInfoRightInput }
    }

    var getLocationInfoRight =  context.dataStore.data.map { it[locationInfoRight] ?: "" }

    //Coordinate
    suspend fun setLatitudeAndLongitudeChecked(isLatitudeInput: Boolean) {
        context.dataStore.edit { it[isLatitudeAndLongitudeChecked] = isLatitudeInput }
    }

    var getLatitudeAndLongitudeChecked =  context.dataStore.data.map { it[isLatitudeAndLongitudeChecked] ?: true }


    //Elevation
    suspend fun setElevation(isElevationInput: Boolean) {
        context.dataStore.edit { it[isElevationChecked] = isElevationInput }
    }

    var getElevation =  context.dataStore.data.map { it[isElevationChecked] ?: true }

    //gridLocation
    suspend fun setGridLocation(isGridLocationInput: Boolean) {
        context.dataStore.edit { it[isGridLocationChecked] = isGridLocationInput }
    }

    var getGridLocation =  context.dataStore.data.map { it[isGridLocationChecked] ?: true }

    //distance
    suspend fun setDistance(isDistanceInput: Boolean) {
        context.dataStore.edit { it[isDistanceChecked] = isDistanceInput }
    }

    var getDistance =  context.dataStore.data.map { it[isDistanceChecked] ?: true }

    //Heading
    suspend fun setHeading(isHeadingInput: Boolean) {
        context.dataStore.edit { it[isHeadingChecked] = isHeadingInput }
    }

    var getHeading =  context.dataStore.data.map { it[isHeadingChecked] ?: true }

    //Address
    suspend fun setAddress(isAddressInput: Boolean) {
        context.dataStore.edit { it[isAddressChecked] = isAddressInput }
    }

    var getAddress =  context.dataStore.data.map { it[isAddressChecked] ?: true }

    //DateAndTime
    suspend fun setDateAndTime(isDateAndTimeInput: Boolean) {
        context.dataStore.edit { it[isdDateAndTimeChecked] = isDateAndTimeInput }
    }

    var getDateAndTime =  context.dataStore.data.map { it[isdDateAndTimeChecked] ?: true }

    //Utm
    suspend fun setUtm(isUtmInput: Boolean) {
        context.dataStore.edit { it[isUtmChecked] = isUtmInput }
    }

    var getUtm =  context.dataStore.data.map { it[isUtmChecked] ?: true }

    //CustomText

    suspend fun setCustomText(customTextInput: String) {
        context.dataStore.edit { it[customText] = customTextInput }
    }

    var getCustomText =  context.dataStore.data.map { it[customText] ?: "" }


}


/**
var preferenceDataStore = PreferenceDataStore(this)
btSaveDetails.setOnClickListener{
CoroutineScope(Dispatcher.IO).launch{
var channelDetails = ChannelDetails("Noob Developer",1000,"Education)
preferenceDataStore.setDetailts(channelDetails)

)
}

 **/

