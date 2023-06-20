@file:OptIn(ExperimentalSheetApi::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class
)

package com.example.dlsandroidredesign
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dlsandroidredesign.data.local.PreferencesDataStore
import eu.wewox.modalsheet.ExperimentalSheetApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


@Composable
fun settingFragment(){
    var isLoginFragmentShow  by remember { mutableStateOf(false) }
    var isWaypointgroupsFragmentsShow  by remember { mutableStateOf(false) }
    val settingSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden, skipHalfExpanded = true)

    val coroutineScope = rememberCoroutineScope()
    Column(
        Modifier
            .fillMaxWidth()
            .background(color = Color(0xFFE1DFDF),)
            .verticalScroll(rememberScrollState()))
    {
        //TOP LAYOUT
        Row(
            Modifier
                .background(color = Color(0xFFF6F3F3))
                .fillMaxWidth()
                .size(17.dp, 30.dp)

            ,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(painter = painterResource(id = R.drawable.ic_xmark_single), contentDescription =null, modifier = Modifier.size(17.dp,17.dp) )
            Text(text = " Close", color = Color(0xFF00B0FF), fontSize = 20.sp,modifier=Modifier.weight(1f))
            Text(text = "Option",color= Color(0xFF000000),fontSize = 20.sp,modifier=Modifier.weight(1f))
        }

        //Spacer
        Spacer(modifier = Modifier.padding(bottom = 10.dp))
        //cardView
        Column(modifier = Modifier.padding(20.dp,0.dp,20.dp,20.dp)) {
            Text(text = "PHOTO DISPLAY OPTIONS")
            DisplayOption()
        }

        //SAVE TO PHOTO LIBRARY OPTIONS

        Column(modifier = Modifier.padding(20.dp,0.dp,20.dp,20.dp)){
            Text(text = "SAVE TO PHOTO LIBRARY OPTIONS")
            SaveOptions()      }


        //UPLOAD OPTION
        Column(modifier = Modifier.padding(20.dp,0.dp,20.dp,20.dp)){
            Text(text = "UPLOAD OPTIONS")
            UploadOptions(
                onLogInPressed= {isLoginFragmentShow=true
                    isWaypointgroupsFragmentsShow=false
                    coroutineScope.launch { settingSheetState.show() }
                },
                onWaypointgroupsPressed = {isWaypointgroupsFragmentsShow=true
                    isLoginFragmentShow=false
                    coroutineScope.launch { settingSheetState.show()}
                }
            )
        }
    }

    ModalBottomSheetLoginAndWaypointgroups(sheetState = settingSheetState, isLoginFragmentShow = isLoginFragmentShow, isWaypointgroupsFragment = isWaypointgroupsFragmentsShow)

    Log.d("Setting", "SettingFragemnt")


}

/////PHOTO DISPLAY OPTIONS
@Composable
fun MyPhotoDisplaySwitch(title: String, checked: Boolean, onCheckChanged: (Boolean) -> Unit) {
    Row(modifier = Modifier,horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically) {
        Text(text =title, color= Color(0xFF00B0FF))
        Spacer(modifier = Modifier.weight(1f))
        Checkbox(
            checked = checked,
            onCheckedChange = {
                onCheckChanged(it)
            },
            colors = CheckboxDefaults.colors(
                checkedColor = Color.Green
            ),
            modifier = Modifier.clip(CircleShape)


        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayOption( ){
    val preferenceDataStore = PreferencesDataStore(LocalContext.current)
    val coroutineScope = rememberCoroutineScope()
    var settingCheckbox = preferenceDataStore.getSettingCheckbox().collectAsState(initial = hashSetOf<String>("LatLon","Elevation","GridLocation","Distance","Heading","Address","Date","Utm","CustomText")).value
    val menuTitleList :List<String> = listOf("Latitude/Longitude","Elevation","Grid Location","Distance from Grid Lines","Heading","Address","Date and Time","UTM Coordinates","Custom Text: ")
    var customText =preferenceDataStore.getCustomText.collectAsState(initial = "")


    Box(modifier = Modifier
        .background(color = Color.White, shape = RoundedCornerShape(8.dp))
        .padding(20.dp, 0.dp, 20.dp, 4.dp),

        ){
        for (menuTitle in menuTitleList){

        }


        Column(modifier = Modifier
        ) {
            MyPhotoDisplaySwitch("Latitude/Longitude",settingCheckbox.contains("LatLon"),
                onCheckChanged = {coroutineScope.launch {preferenceDataStore.setSettingCheckBox("LatLon",settingCheckbox) }
                }
            )

            Divider( thickness = 0.2.dp, color = Color.Black)

            MyPhotoDisplaySwitch("Elevation",settingCheckbox.contains("Elevation"),
                onCheckChanged = {coroutineScope.launch {preferenceDataStore.setSettingCheckBox("Elevation",settingCheckbox)}
                }
            )

            Divider( thickness = 0.2.dp, color = Color.Black)

            MyPhotoDisplaySwitch("Grid Location",settingCheckbox.contains("Location"),
                onCheckChanged = {coroutineScope.launch {preferenceDataStore.setSettingCheckBox("GridLocation",settingCheckbox)}})
            Divider( thickness = 0.2.dp, color = Color.Black)

            MyPhotoDisplaySwitch("Distance from Grid Lines",settingCheckbox.contains("LatLon"), onCheckChanged = { newValue-> coroutineScope.launch { preferenceDataStore.setDistance(newValue)}})
            Divider( thickness = 0.2.dp, color = Color.Black)

            MyPhotoDisplaySwitch("Heading",settingCheckbox.contains("LatLon"), onCheckChanged = { newValue-> coroutineScope.launch { preferenceDataStore.setHeading(newValue)}})
            Divider( thickness = 0.2.dp, color = Color.Black)

            MyPhotoDisplaySwitch("Address",settingCheckbox.contains("LatLon"), onCheckChanged = { newValue-> coroutineScope.launch { preferenceDataStore.setAddress(newValue)}})
            Divider( thickness = 0.2.dp, color = Color.Black)

            MyPhotoDisplaySwitch("Date and Time",settingCheckbox.contains("LatLon"), onCheckChanged ={ newValue-> coroutineScope.launch { preferenceDataStore.setDateAndTime(newValue)}})
            Divider( thickness = 0.2.dp, color = Color.Black)

            MyPhotoDisplaySwitch("UTM Coordinates",settingCheckbox.contains("LatLon"), onCheckChanged = { newValue-> coroutineScope.launch { preferenceDataStore.setUtm(newValue)}})
            Divider( thickness = 0.2.dp, color = Color.Black)

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)) {
                Text(text = "Custom Text: ")
                BasicTextField(value = "${customText.value}", onValueChange = {newValue-> coroutineScope.launch(Dispatchers.IO) { preferenceDataStore.setCustomText(newValue)}})
            }

        }

    }
    Log.d("Setting", "Displayoption")

}



////SAVE TO PHOTO LIBRARY OPTIONS
@Composable
fun SaveOptions(){
    var photoOptionIndex by remember { mutableStateOf(0) }
    Row(modifier = Modifier
        .background(color = Color.White, shape = RoundedCornerShape(8.dp))
        .fillMaxWidth()
        .padding(15.dp, 4.dp, 18.dp, 4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "PHOTO SIZE", fontSize = 16.sp)
        Spacer(modifier = Modifier
            .padding(8.dp)
            .width(1.dp)
            .background(color = Color.Black))
        Box(modifier = Modifier
            .background(color = Color.LightGray, shape = RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .padding(1.dp, 0.dp, 1.dp, 0.dp)){
            Row(Modifier.padding(4.dp),horizontalArrangement = Arrangement.spacedBy(space=3.dp)){
                //Tiny
                Box(
                    Modifier
                        .weight(1f)
                        .background(
                            color = if (photoOptionIndex == 1) Color.White else Color.LightGray,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable(onClick = { photoOptionIndex = 1 })
                        .padding(2.dp), contentAlignment = Alignment.Center) {
                    Text(text = "Tiny")
                }
                Divider(
                    color = Color.Gray,
                    modifier = Modifier
                        .height(25.dp)  //fill the max height
                        .width(1.dp)
                )
                //Small
                Box(
                    Modifier
                        .weight(1f)
                        .background(
                            color = if (photoOptionIndex == 2) Color.White else Color.LightGray,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable(onClick = { photoOptionIndex = 2 })
                        .padding(2.dp), contentAlignment = Alignment.Center) {
                    Text(text = "Small")
                }
                Divider(
                    color = Color.Gray,
                    modifier = Modifier
                        .height(25.dp)  //fill the max height
                        .width(1.dp)
                )
                //Medium
                Box(
                    Modifier
                        .weight(1f)
                        .background(
                            color = if (photoOptionIndex == 3) Color.White else Color.LightGray,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable(onClick = { photoOptionIndex = 3 })
                        .padding(2.dp), contentAlignment = Alignment.Center) {
                    Text(text = "Medium")
                }
                Divider(
                    color = Color.Gray,
                    modifier = Modifier
                        .height(25.dp)  //fill the max height
                        .width(1.dp)
                )
                //Large
                Box(
                    Modifier
                        .weight(1f)
                        .background(
                            color = if (photoOptionIndex == 4) Color.White else Color.LightGray,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable(onClick = { photoOptionIndex = 4 })
                        .padding(2.dp), contentAlignment = Alignment.Center) {
                    Text(text = "Large")
                }
                Divider(
                    color = Color.Gray,
                    modifier = Modifier
                        .height(25.dp)  //fill the max height
                        .width(1.dp)
                )
                //Original
                Box(
                    Modifier
                        .weight(1f)
                        .background(
                            color = if (photoOptionIndex == 5) Color.White else Color.LightGray,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable(onClick = { photoOptionIndex = 5 })
                        .padding(2.dp), contentAlignment = Alignment.Center) {
                    Text(text = "Original")
                }

            }

        }

    }

    Log.d("Setting", "SaveOptions")
}


@Composable
fun TabRowPhotoOption(){
    var tabPhotoSizeIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Tiny", "Small", "Medium","Large","Original")
    Row(modifier = Modifier
        .background(color = Color.White, shape = RoundedCornerShape(8.dp))
        .fillMaxWidth()
        .padding(18.dp, 4.dp, 18.dp, 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(text = "PHOTO SIZE", fontSize = 14.sp)

        TabRow(selectedTabIndex = tabPhotoSizeIndex,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color.LightGray)
        ) {
            tabs.forEachIndexed { index, title ->

                Tab(text = { Text(title) },
                    selected = tabPhotoSizeIndex == index,
                    onClick = { tabPhotoSizeIndex = index },
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))//Round shape for each item
                        .padding(3.dp, 3.dp),

                    )

            }
        }
    }}

@Composable
fun UploadOptions(onLogInPressed:()->Unit,onWaypointgroupsPressed:()->Unit){
    val context = LocalContext.current
    val preferenceDataStore = PreferencesDataStore(context)
    var isLoginSuccessful by remember { mutableStateOf(false) }
    var usernameDisplay :String? by remember { mutableStateOf(null) }
    val isAutoUpload =preferenceDataStore.getUploadAuto.collectAsState(initial = false).value

    LaunchedEffect(Unit) {
        val loginStatus = preferenceDataStore.getIsLoginSuccess.first()
        val usernameDataStore = preferenceDataStore.getLoginSharedInfoList().first().name

        // Call a function to update the value in your Composable's state
        isLoginSuccessful = loginStatus
        usernameDisplay = usernameDataStore
    }


    var UploadSizeIndex by remember { mutableStateOf(0) }

    Column(modifier = Modifier
        .background(color = Color.White, shape = RoundedCornerShape(8.dp))
        .fillMaxWidth()
        .padding(5.dp, 4.dp, 0.dp, 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(text = "Upload Photos to AbaData", color = Color(0xFF00B0FF),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .clickable { onLogInPressed() })
            if(isLoginSuccessful) {
                Row(modifier = Modifier.align(Alignment.TopEnd)) {
                    Text(text = "User: $usernameDisplay", color = Color(0xFF00B0FF))
                    Spacer(modifier = Modifier.width(20.dp))
                    Image(
                        painter = painterResource(id = R.drawable.ic_checkmark_circle_fill_single),
                        modifier = Modifier
                            .size(30.dp, 30.dp),
                        contentDescription = null
                    )
                }
            }
        }

        Divider( thickness = 0.2.dp, color = Color.Black, modifier = Modifier.padding(5.dp,5.dp,5.dp,5.dp))

        if(isLoginSuccessful){
            Box(modifier = Modifier
                .fillMaxWidth()
                .clickable {})
            {
                Text(text = "Upload Automatically", color= Color(0xFF00B0FF),
                    modifier = Modifier
                        .align(Alignment.TopStart))
                Image(painter = painterResource(id = R.drawable.ic_checkmark_circle_fill_single),
                    modifier= Modifier
                        .size(30.dp, 30.dp)
                        .align(Alignment.TopEnd),
                    contentDescription =null )
            }

            Divider( thickness = 0.2.dp, color = Color.Black, modifier = Modifier.padding(5.dp,5.dp,5.dp,5.dp))}


        Row(modifier = Modifier
            .background(color = Color.White, shape = RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .padding(15.dp, 4.dp, 18.dp, 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(text = "UPLOAD SIZE", fontSize = 14.sp)

            Spacer(modifier = Modifier
                .padding(8.dp)
                .width(1.dp)
                .background(color = Color.Black))
            Box(modifier = Modifier
                .background(color = Color.LightGray, shape = RoundedCornerShape(8.dp))
                .fillMaxWidth()
                .padding(1.dp, 0.dp, 1.dp, 0.dp)){
                Row(Modifier.padding(4.dp),horizontalArrangement = Arrangement.spacedBy(space=3.dp)){
                    //Tiny
                    Box(
                        Modifier
                            .weight(1f)
                            .background(
                                color = if (UploadSizeIndex == 1) Color.White else Color.LightGray,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable(onClick = { UploadSizeIndex = 1 })
                            .padding(2.dp), contentAlignment = Alignment.Center) {
                        Text(text = "Tiny")
                    }
                    Divider(
                        color = Color.Gray,
                        modifier = Modifier
                            .height(25.dp)  //fill the max height
                            .width(1.dp)
                    )
                    //Small
                    Box(
                        Modifier
                            .weight(1f)
                            .background(
                                color = if (UploadSizeIndex == 2) Color.White else Color.LightGray,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable(onClick = { UploadSizeIndex = 2 })
                            .padding(2.dp), contentAlignment = Alignment.Center) {
                        Text(text = "Small")
                    }
                    Divider(
                        color = Color.Gray,
                        modifier = Modifier
                            .height(25.dp)  //fill the max height
                            .width(1.dp)
                    )
                    //Medium
                    Box(
                        Modifier
                            .weight(1f)
                            .background(
                                color = if (UploadSizeIndex == 3) Color.White else Color.LightGray,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable(onClick = { UploadSizeIndex = 3 })
                            .padding(2.dp), contentAlignment = Alignment.Center) {
                        Text(text = "Medium")
                    }
                    Divider(
                        color = Color.Gray,
                        modifier = Modifier
                            .height(25.dp)  //fill the max height
                            .width(1.dp)
                    )
                    //Large
                    Box(
                        Modifier
                            .weight(1f)
                            .background(
                                color = if (UploadSizeIndex == 4) Color.White else Color.LightGray,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable(onClick = { UploadSizeIndex = 4 })
                            .padding(2.dp), contentAlignment = Alignment.Center) {
                        Text(text = "Large")
                    }
                    Divider(
                        color = Color.Gray,
                        modifier = Modifier
                            .height(25.dp)  //fill the max height
                            .width(1.dp)
                    )
                    //Original
                    Box(
                        Modifier
                            .weight(1f)
                            .background(
                                color = if (UploadSizeIndex == 5) Color.White else Color.LightGray,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable(onClick = { UploadSizeIndex = 5 })
                            .padding(2.dp), contentAlignment = Alignment.Center) {
                        Text(text = "Original")
                    }

                }
            }
        }

        Divider( thickness = 0.2.dp, color = Color.Black, modifier = Modifier.padding(5.dp,5.dp,5.dp,5.dp))
        if(isLoginSuccessful){
            Row(modifier = Modifier.clickable { onWaypointgroupsPressed() }) {
                Text(text = "Waypoint Group")
                Text(text = "ReplaceByGWayPointGroupName")
            }
        }
    }
    Log.d("Setting", "UploadOptions")
}








