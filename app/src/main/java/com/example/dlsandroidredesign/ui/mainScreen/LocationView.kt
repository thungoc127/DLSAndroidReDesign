package com.example.dlsandroidredesign.ui.mainScreen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dlsandroidredesign.R
import com.example.dlsandroidredesign.data.local.PreferencesDataStore


@SuppressLint("MissingPermission", "CoroutineCreationDuringComposition")
@Composable
fun LocationView(viewModel: ImageLocationInfoViewModel= hiltViewModel()) {
    val location = viewModel.getLocationObject.collectAsStateWithLifecycle().value
//////CheckedVariable
    val preferenceDataStore = PreferencesDataStore(LocalContext.current)
    val settingCheckbox = preferenceDataStore.getSettingCheckbox().collectAsState(initial =hashSetOf<String>("LatLon","Elevation","GridLocation","Distance","Heading","Address","Date","Utm","CustomText")).value
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.align(Alignment.TopStart)) {

            if (settingCheckbox.contains("LatLon")) {
                Text(
                    text = "Lat/Lon",
                    fontSize = 14.sp,
                    color = colorResource(id = R.color.textOverlay),
                    style = TextStyle(
                        fontSize = 24.sp,
                        shadow = Shadow(
                            color = Color.White, offset = Offset(3.0f, 3.0f), blurRadius = 5f
                        )
                    ),
                    modifier = Modifier
                        .padding(start = 5.dp, bottom = 2.dp)
                )
            }
            //

            if (settingCheckbox.contains("LatLon")) {
                //lat
                Text(
                    text = "${location.lat}",
                    fontSize = 14.sp,
                    color = colorResource(id = R.color.textOverlay),
                    style = TextStyle(
                        fontSize = 24.sp,
                        shadow = Shadow(
                            color = Color.White, offset = Offset(3.0f, 3.0f), blurRadius = 5f
                        )
                    ),
                    modifier = Modifier
                        .padding(start = 5.dp, bottom = 2.dp)

                )
            }


            //lng
            if (settingCheckbox.contains("LatLon")) {
                Text(
                    text = "${location.lon}",
                    fontSize = 14.sp,
                    color = colorResource(id = R.color.textOverlay),
                    style = TextStyle(
                        fontSize = 24.sp,
                        shadow = Shadow(
                            color = Color.White, offset = Offset(3.0f, 3.0f), blurRadius = 5f
                        )
                    ),
                    modifier = Modifier
                        .padding(start = 5.dp, bottom = 2.dp)

                )
            }
            //elevation
            if (settingCheckbox.contains("Elevation")) {
                Text(
                    text = "${location.elevation}",
                    fontSize = 14.sp,
                    color = colorResource(id = R.color.textOverlay),
                    style = TextStyle(
                        fontSize = 24.sp,
                        shadow = Shadow(
                            color = Color.White, offset = Offset(3.0f, 3.0f), blurRadius = 5f
                        )
                    ),
                    modifier = Modifier
                        .padding(start = 5.dp, bottom = 2.dp)

                )
            }

            //GridLocation
            if (settingCheckbox.contains("GridLocation")) {
                Text(
                    text = "${location.gridLocation}",
                    fontSize = 14.sp,
                    color = colorResource(id = R.color.textOverlay),
                    style = TextStyle(
                        fontSize = 24.sp,
                        shadow = Shadow(
                            color = Color.White, offset = Offset(3.0f, 3.0f), blurRadius = 5f
                        )
                    ),
                    modifier = Modifier
                        .padding(start = 5.dp, bottom = 2.dp),


                    )
            }
            //DistanceFromGridLine
            if (settingCheckbox.contains("Distance")) {
                Text(
                    text = "${location.distance}",
                    fontSize = 14.sp,
                    color = colorResource(id = R.color.textOverlay),
                    style = TextStyle(
                        fontSize = 24.sp,
                        shadow = Shadow(
                            color = Color.White, offset = Offset(3.0f, 3.0f), blurRadius = 5f
                        )
                    ),
                    modifier = Modifier
                        .padding(start = 5.dp, bottom = 2.dp),


                    )
            }
            //utmTxt
            if (settingCheckbox.contains("Utm")) {
                Text(
                    text = "${location.utmCoordinate}",
                    fontSize = 14.sp,
                    color = colorResource(id = R.color.textOverlay),
                    style = TextStyle(
                        fontSize = 24.sp,
                        shadow = Shadow(
                            color = Color.White, offset = Offset(3.0f, 3.0f), blurRadius = 5f
                        )
                    ),
                    modifier = Modifier
                        .padding(start = 5.dp, bottom = 2.dp)

                )
            }
        }



        Column(
            modifier = Modifier.align(Alignment.TopEnd), horizontalAlignment = Alignment.End
        ) {
            //bearingTxt
            if (settingCheckbox.contains("Heading")) {
                Text(
                    text = " ${location.bearing}",
                    fontSize = 14.sp,
                    color = colorResource(id = R.color.textOverlay),
                    style = TextStyle(
                        fontSize = 24.sp,
                        shadow = Shadow(
                            color = Color.White, offset = Offset(3.0f, 3.0f), blurRadius = 5f
                        )
                    ),
                    modifier = Modifier
                        .padding(start = 5.dp, bottom = 2.dp)

                )
            }
            //addressTxt
            if (settingCheckbox.contains("Address")) {
                Text(
                    text = "${location.address}",
                    fontSize = 14.sp,
                    color = colorResource(id = R.color.textOverlay),
                    style = TextStyle(
                        fontSize = 24.sp,
                        shadow = Shadow(
                            color = Color.White, offset = Offset(3.0f, 3.0f), blurRadius = 5f
                        )
                    ),
                    modifier = Modifier
                        .padding(start = 5.dp, bottom = 2.dp)

                )
            }


            if (settingCheckbox.contains("Date")) {
                Text(

                    text = "${location.date}",
                    fontSize = 14.sp,
                    color = colorResource(id = R.color.textOverlay),
                    style = TextStyle(
                        fontSize = 24.sp,
                        shadow = Shadow(
                            color = Color.White, offset = Offset(3.0f, 3.0f), blurRadius = 5f
                        )
                    ),
                    modifier = Modifier
                        .padding(start = 5.dp, bottom = 2.dp)

                )

            }
        }

        if(settingCheckbox.contains("CustomText")){
            Text(
                text = location.custText,
                fontSize = 14.sp,
                color = colorResource(id = R.color.textOverlay),
                style = TextStyle(
                    fontSize = 24.sp,
                    shadow = Shadow(
                        color = Color.White, offset = Offset(3.0f, 3.0f), blurRadius = 5f
                    )
                ),
                modifier = Modifier
                    .padding(start = 5.dp, bottom = 2.dp)
                    .align(Alignment.BottomCenter)
            )
        }}


}

