package com.example.dlsandroidredesign.ui.mainScreen

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dlsandroidredesign.ui.setting.SettingFragmentViewModel

@SuppressLint("MissingPermission", "CoroutineCreationDuringComposition")
@Composable
fun LocationView(viewModel: ImageLocationInfoViewModel = hiltViewModel(), settingViewModel: SettingFragmentViewModel = hiltViewModel()) {
    val location = viewModel.locationObject.collectAsStateWithLifecycle().value
    Log.d("getLocationProcess","locationview$location")
// ////CheckedVariable
    val settingCheckbox by settingViewModel.checkBox.collectAsStateWithLifecycle()
    fun Modifier.customThemeModifier() = padding(start = 5.dp, bottom = 2.dp)
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.align(Alignment.TopStart)) {
            if (settingCheckbox.latLon) {
                Text(
                    text = "Lat/Lon",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.customThemeModifier()
                )
            }
            if (settingCheckbox.latLon) {
                // lat
                Text(
                    // TODO: the location.lat is already string. Doesn't need to put in ""
                    text = location.lat,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.customThemeModifier()
                )
            }
            // lng
            if (settingCheckbox.latLon) {
                Text(
                    text = location.lon,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.customThemeModifier()
                )
            }
            // elevation
            if (settingCheckbox.latLon) {
                Text(
                    text = location.elevation,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.customThemeModifier()
                )
            }
            // GridLocation
            if (settingCheckbox.gridLocation) {
                Text(
                    text = location.gridLocation,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.customThemeModifier()
                )
            }
            // DistanceFromGridLine
            if (settingCheckbox.distance) {
                Text(
                    text = location.distance,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.customThemeModifier()
                )
            }
            // utmTxt
            if (settingCheckbox.utmCoordinate) {
                Text(
                    text = location.utmCoordinate,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.customThemeModifier()
                )
            }
        }

        Column(
            modifier = Modifier.align(Alignment.TopEnd),
            horizontalAlignment = Alignment.End
        ) {
            // bearingTxt
            if (settingCheckbox.bearing) {
                Text(
                    text = location.bearing,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.customThemeModifier()
                )
            }
            // addressTxt
            if (settingCheckbox.address) {
                Text(
                    text = location.address,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.customThemeModifier()
                )
            }

            if (settingCheckbox.date) {
                Text(
                    text = location.date,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.customThemeModifier()
                )
            }
        }

        if (settingCheckbox.cusText) {
            Text(
                text = settingViewModel.cusText.collectAsState(initial = "").value,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .customThemeModifier()
                    .align(Alignment.BottomCenter)
            )
        }
    }
}
