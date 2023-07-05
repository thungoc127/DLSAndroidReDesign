package com.example.dlsandroidredesign.ui.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dlsandroidredesign.data.CheckBoxKey
import com.example.dlsandroidredesign.ui.mainScreen.ImageLocationInfoViewModel

@Composable
fun PhotoDisplayOptions(settingFragmentViewModel: SettingFragmentViewModel = hiltViewModel(), imageLocationInfoViewModel: ImageLocationInfoViewModel = hiltViewModel()) {
    val settingCheckbox by settingFragmentViewModel.checkBox.collectAsStateWithLifecycle()
    val menuTitleList: List<String> = listOf("Latitude/Longitude", "Elevation", "Grid Location", "Distance from Grid Lines", "Heading", "Address", "Date and Time", "UTM Coordinates", "Custom Text: ")

    Column(modifier = Modifier.padding(20.dp, 0.dp, 20.dp, 20.dp)) {
        Text(text = "PHOTO DISPLAY OPTIONS")
        Box(
            modifier = Modifier
                .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                .padding(20.dp, 0.dp, 20.dp, 4.dp)

        ) {
            for (menuTitle in menuTitleList) {
            }
            Column(
                modifier = Modifier
            ) {
                MyPhotoDisplaySwitch(
                    "Latitude/Longitude",
                    settingCheckbox.latLon,
                    onCheckChanged = {
                        settingFragmentViewModel.setCheckBox(CheckBoxKey.LatLon.name, it)
                    }

                )
                Divider(thickness = 0.2.dp, color = Color.Black)

                MyPhotoDisplaySwitch(
                    "Elevation",
                    settingCheckbox.elevation,
                    onCheckChanged = {
                        settingFragmentViewModel.setCheckBox(CheckBoxKey.Elevation.name, it)
                    }

                )

                Divider(thickness = 0.2.dp, color = Color.Black)

                MyPhotoDisplaySwitch(
                    "Grid Location",
                    settingCheckbox.gridLocation,
                    onCheckChanged = {
                        settingFragmentViewModel.setCheckBox(CheckBoxKey.GridLocation.name, it)
                    }
                )
                Divider(thickness = 0.2.dp, color = Color.Black)

                MyPhotoDisplaySwitch(
                    "Distance from Grid Lines",
                    settingCheckbox.distance,
                    onCheckChanged = {
                        settingFragmentViewModel.setCheckBox(CheckBoxKey.Distance.name, it)
                    }
                )
                Divider(thickness = 0.2.dp, color = Color.Black)

                MyPhotoDisplaySwitch(
                    "Heading",
                    settingCheckbox.bearing,
                    onCheckChanged = {
                        settingFragmentViewModel.setCheckBox(CheckBoxKey.Bearing.name, it)
                    }
                )
                Divider(thickness = 0.2.dp, color = Color.Black)

                MyPhotoDisplaySwitch(
                    "Address",
                    settingCheckbox.address,
                    onCheckChanged = {
                        settingFragmentViewModel.setCheckBox(CheckBoxKey.Address.name, it)
                    }
                )
                Divider(thickness = 0.2.dp, color = Color.Black)

                MyPhotoDisplaySwitch(
                    "Date and Time",
                    settingCheckbox.date,
                    onCheckChanged = {
                        settingFragmentViewModel.setCheckBox(CheckBoxKey.Date.name, it)
                    }
                )
                Divider(thickness = 0.2.dp, color = Color.Black)

                MyPhotoDisplaySwitch(
                    "UTM Coordinates",
                    settingCheckbox.utmCoordinate,
                    onCheckChanged = {
                        settingFragmentViewModel.setCheckBox(CheckBoxKey.UtmCoordinate.name, it)
                    }
                )
                Divider(thickness = 0.2.dp, color = Color.Black)

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Custom Text: ")
                    BasicTextField(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        value = settingFragmentViewModel.cusText.collectAsState(initial = "").value,
                        onValueChange = {
                            settingFragmentViewModel.setCusText(it)
                            imageLocationInfoViewModel.setCusTextLocationObject(it)
                        }
                    )
                    Checkbox(
                        checked = settingCheckbox.cusText,
                        onCheckedChange = { settingFragmentViewModel.setCheckBox(CheckBoxKey.CusText.name, it) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color.Green
                        ),
                        modifier = Modifier.clip(CircleShape)
                    )
                }
            }
        }
    }
}
