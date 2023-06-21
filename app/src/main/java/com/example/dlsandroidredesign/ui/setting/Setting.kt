@file:OptIn(ExperimentalSheetApi::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class
)

package com.example.dlsandroidredesign.ui.setting
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dlsandroidredesign.ImageLocationInfoViewModel
import com.example.dlsandroidredesign.ModalBottomSheetLoginAndWaypointgroups
import com.example.dlsandroidredesign.R
import com.example.dlsandroidredesign.data.CheckBoxKey
import com.example.dlsandroidredesign.data.local.PreferencesDataStore
import com.example.dlsandroidredesign.domain.entity.User
import com.example.dlsandroidredesign.ui.login.LogInViewModel
import com.example.dlsandroidredesign.ui.mainScreen.MainScreenViewModel
import eu.wewox.modalsheet.ExperimentalSheetApi
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun settingFragment(viewModel: MainScreenViewModel= hiltViewModel(),loginViewModel: LogInViewModel= hiltViewModel()){

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
            SaveOptions()
        }


        //UPLOAD OPTION
        Column(modifier = Modifier.padding(20.dp,0.dp,20.dp,20.dp)){
            Text(text = "UPLOAD OPTIONS")
            UploadOptions(
                onLogInPressed= {
                    coroutineScope.launch { loginViewModel.setLoginVisible(true)}
                },
                onWaypointgroupsPressed = {
                    coroutineScope.launch { viewModel.waypointGroupSheetState.show() }
                }
            )
        }
    }

    ModalBottomSheetLoginAndWaypointgroups(waypointGroupSheetState = viewModel.waypointGroupSheetState)

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
fun DisplayOption(settingFragmentViewModel: SettingFragmentViewModel= hiltViewModel(),imageLocationInfoViewModel: ImageLocationInfoViewModel= hiltViewModel() ){
    val preferenceDataStore = PreferencesDataStore(LocalContext.current)
    val coroutineScope = rememberCoroutineScope()
    val settingCheckbox by settingFragmentViewModel.checkBox.collectAsStateWithLifecycle()
    val menuTitleList :List<String> = listOf("Latitude/Longitude","Elevation","Grid Location","Distance from Grid Lines","Heading","Address","Date and Time","UTM Coordinates","Custom Text: ")
//    var customText =preferenceDataStore.getCustomText.collectAsState(initial = "")




    Box(modifier = Modifier
        .background(color = Color.White, shape = RoundedCornerShape(8.dp))
        .padding(20.dp, 0.dp, 20.dp, 4.dp),

        ){
        for (menuTitle in menuTitleList){

        }
        Column(modifier = Modifier
        ) {
            MyPhotoDisplaySwitch("Latitude/Longitude",settingCheckbox.latLon,
                onCheckChanged = {
                    settingFragmentViewModel.setCheckBox(CheckBoxKey.latlon.name, it)
                }


            )
            Divider( thickness = 0.2.dp, color = Color.Black)

            MyPhotoDisplaySwitch("Elevation",settingCheckbox.elevation,
                onCheckChanged = {settingFragmentViewModel.setCheckBox(CheckBoxKey.elevation.name,it)
                }

            )

            Divider( thickness = 0.2.dp, color = Color.Black)

            MyPhotoDisplaySwitch("Grid Location",settingCheckbox.gridLocation,
                onCheckChanged = {settingFragmentViewModel.setCheckBox(CheckBoxKey.gridLocation.name,it)
                }
            )
            Divider( thickness = 0.2.dp, color = Color.Black)

            MyPhotoDisplaySwitch("Distance from Grid Lines",settingCheckbox.distance,
                onCheckChanged = {settingFragmentViewModel.setCheckBox(CheckBoxKey.distance.name,it)
                })
            Divider( thickness = 0.2.dp, color = Color.Black)

            MyPhotoDisplaySwitch("Heading",settingCheckbox.bearing,
                onCheckChanged = { settingFragmentViewModel.setCheckBox(CheckBoxKey.bearing.name,it)
                })
            Divider( thickness = 0.2.dp, color = Color.Black)

            MyPhotoDisplaySwitch("Address",settingCheckbox.address,
                onCheckChanged = {settingFragmentViewModel.setCheckBox(CheckBoxKey.address.name,it)
                })
            Divider( thickness = 0.2.dp, color = Color.Black)

            MyPhotoDisplaySwitch("Date and Time",settingCheckbox.date,
                onCheckChanged ={settingFragmentViewModel.setCheckBox(CheckBoxKey.date.name,it)
                })
            Divider( thickness = 0.2.dp, color = Color.Black)

            MyPhotoDisplaySwitch("UTM Coordinates",settingCheckbox.utmCoordinate,
                onCheckChanged = { settingFragmentViewModel.setCheckBox(CheckBoxKey.utmCoordinate.name,it)
                })
            Divider( thickness = 0.2.dp, color = Color.Black)

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)) {
                Text(text = "Custom Text: ")
                BasicTextField(value = settingFragmentViewModel.cusText.collectAsState(initial = "").value
                    , onValueChange = {settingFragmentViewModel.setCusText(it)
                        imageLocationInfoViewModel.setCusTextLocationObject(it)

                    })
                Spacer(modifier = Modifier.weight(1f))
                Checkbox(checked =settingCheckbox.cusText ,
                    onCheckedChange = {settingFragmentViewModel.setCheckBox(CheckBoxKey.cusText.name,it)},
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color.Green
                    ),
                    modifier = Modifier.clip(CircleShape))
            }

        }
    }
}




////SAVE TO PHOTO LIBRARY OPTIONS
@Composable
fun SaveOptions(viewModel: SettingFragmentViewModel= hiltViewModel()){
    val photoOptionIndex = viewModel.photoSize.collectAsState(initial = "Original").value
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
                            color = if (photoOptionIndex == "Tiny") Color.White else Color.LightGray,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable(onClick = { viewModel.setPhotoSize("Tiny") })
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
                            color = if (photoOptionIndex == "Small") Color.White else Color.LightGray,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable(onClick = { viewModel.setPhotoSize("Small") })
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
                            color = if (photoOptionIndex == "Medium") Color.White else Color.LightGray,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable(onClick = { viewModel.setPhotoSize("Medium") })
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
                            color = if (photoOptionIndex == "Large") Color.White else Color.LightGray,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable(onClick = { viewModel.setPhotoSize("Large") })
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
                            color = if (photoOptionIndex == "Original") Color.White else Color.LightGray,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable(onClick = { viewModel.setPhotoSize("Original") })
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
fun UploadOptions(onLogInPressed:()->Unit,onWaypointgroupsPressed:()->Unit,loginViewModel: LogInViewModel= hiltViewModel(),settingFragmentViewModel: SettingFragmentViewModel= hiltViewModel()){
    val context = LocalContext.current
    val isAutoUpload = settingFragmentViewModel.autoUploadStatus.collectAsState(initial = false)

    val uploadSize=settingFragmentViewModel.uploadSize.collectAsStateWithLifecycle(initialValue = "Original").value

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
            if(loginViewModel.isLogInSuccess.collectAsState(initial = false).value!!) {
                Row(modifier = Modifier.align(Alignment.TopEnd)) {
                    Text(text = "User: ${loginViewModel.currentUser.collectAsState(initial = User("","", emptyList(),"","")).value!!.userName}", color = Color(0xFF00B0FF))
                    Spacer(modifier = Modifier.width(20.dp))
                    Checkbox(modifier= Modifier
                        .size(30.dp, 30.dp),
                        checked = true,
                        onCheckedChange = {},
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color.Green
                        ))
//                    Image(
//                        painter = painterResource(id = R.drawable.ic_checkmark_circle_fill_single),
//                        modifier = Modifier
//                            .size(30.dp, 30.dp),
//                        contentDescription = null
//                    )
                }
            }
        }

        Divider( thickness = 0.2.dp, color = Color.Black, modifier = Modifier.padding(5.dp,5.dp,5.dp,5.dp))

        if(loginViewModel.isLogInSuccess.collectAsState(initial = false).value!!){

            Box(modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    if (isAutoUpload.value) {
                        settingFragmentViewModel.setAutoUpload(false)
                    } else {
                        settingFragmentViewModel.setAutoUpload(true)
                    }
                })
            {
                Text(text = "Upload Automatically", color= Color(0xFF00B0FF),
                    modifier = Modifier
                        .align(Alignment.TopStart))
                Checkbox(modifier= Modifier
                    .size(30.dp, 30.dp)
                    .align(Alignment.TopEnd),
                    checked = isAutoUpload.value,
                    onCheckedChange = {settingFragmentViewModel.setAutoUpload(it)},
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color.Green
                    ))
//                if(isAutoUpload.value){
//                    Image(painter = painterResource(id = R.drawable.ic_checkmark_circle_fill_single),
//                        modifier= Modifier
//                            .size(30.dp, 30.dp)
//                            .align(Alignment.TopEnd),
//                        contentDescription =null )
//                }


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
                                color = if (uploadSize == "Tiny") Color.White else Color.LightGray,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable(onClick = { settingFragmentViewModel.setUploadSize("Tiny") })
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
                                color = if (uploadSize == "Small") Color.White else Color.LightGray,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable(onClick = { settingFragmentViewModel.setUploadSize("Small")})
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
                                color = if (uploadSize == "Medium") Color.White else Color.LightGray,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable(onClick = { settingFragmentViewModel.setUploadSize("Medium") })
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
                                color = if (uploadSize == "Large") Color.White else Color.LightGray,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable(onClick = {settingFragmentViewModel.setUploadSize("Large") })
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
                                color = if (uploadSize == "Original") Color.White else Color.LightGray,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable(onClick = { settingFragmentViewModel.setUploadSize("Original") })
                            .padding(2.dp), contentAlignment = Alignment.Center) {
                        Text(text = "Original")
                    }

                }
            }
        }

        Divider( thickness = 0.2.dp, color = Color.Black, modifier = Modifier.padding(5.dp,5.dp,5.dp,5.dp))
        if(loginViewModel.isLogInSuccess.collectAsState(initial = false).value!!){
            Box(modifier = Modifier
                .clickable { onWaypointgroupsPressed() }
                .fillMaxWidth()
                ) {
                Text(text = "Waypoint Group")
                Row(modifier = Modifier.align(Alignment.TopEnd)) {
                    Text(text = "${loginViewModel.currentUser.collectAsState(initial = User("","", emptyList(),"","")).value!!.groupNameCheck}")
                    Image(painterResource(id = R.drawable.ic_chevron_right_single)
                        , contentDescription =null
                        ,modifier = Modifier
                            .size(23.dp, 23.dp)
                    )
                }

            }
        }
    }
    Log.d("Setting", "UploadOptions")
}








