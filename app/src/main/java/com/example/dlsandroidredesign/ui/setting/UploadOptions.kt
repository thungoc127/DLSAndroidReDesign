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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dlsandroidredesign.R
import com.example.dlsandroidredesign.domain.entity.User
import com.example.dlsandroidredesign.ui.login.LogInViewModel

@Composable
fun UploadOptions(onLogInPressed: () -> Unit, onWaypointgroupsPressed: () -> Unit, loginViewModel: LogInViewModel = hiltViewModel(), settingFragmentViewModel: SettingFragmentViewModel = hiltViewModel()) {
    val isAutoUpload = settingFragmentViewModel.autoUploadStatus.collectAsState(initial = false)
    val uploadSize = settingFragmentViewModel.uploadSize.collectAsStateWithLifecycle(initialValue = "Original").value



    Column(modifier = Modifier.padding(20.dp, 0.dp, 20.dp, 20.dp)) {
        Text(text = "UPLOAD OPTIONS")
        Column(
            modifier = Modifier
                .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                .fillMaxWidth()
                .padding(5.dp, 4.dp, 0.dp, 4.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Upload Photos to AbaData",
                    color = Color(0xFF00B0FF),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .clickable { onLogInPressed() }
                )
                if (loginViewModel.isLogInSuccess.collectAsState(initial = false).value!!) {
                    Row(modifier = Modifier.align(Alignment.TopEnd)) {
                        Text(text = "User: ${loginViewModel.currentUser.collectAsState(initial = User("","", emptyList(),"","")).value!!.userName}", color = Color(0xFF00B0FF))
                        Spacer(modifier = Modifier.width(20.dp))
                        Checkbox(
                            modifier = Modifier
                                .size(30.dp, 30.dp),
                            checked = true,
                            onCheckedChange = {},
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color.Green
                            )
                        )
                    }
                }
            }

            Divider(thickness = 0.2.dp, color = Color.Black, modifier = Modifier.padding(5.dp, 5.dp, 5.dp, 5.dp))

            if (loginViewModel.isLogInSuccess.collectAsState(initial = false).value!!) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (isAutoUpload.value) {
                                settingFragmentViewModel.setAutoUpload(false)
                            } else {
                                settingFragmentViewModel.setAutoUpload(true)
                            }
                        }
                ) {
                    Text(
                        text = "Upload Automatically",
                        color = Color(0xFF00B0FF),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                    )
                    Checkbox(
                        modifier = Modifier
                            .size(30.dp, 30.dp)
                            .align(Alignment.TopEnd),
                        checked = isAutoUpload.value,
                        onCheckedChange = { settingFragmentViewModel.setAutoUpload(it) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color.Green
                        )
                    )
//                if(isAutoUpload.value){
//                    Image(painter = painterResource(id = R.drawable.ic_checkmark_circle_fill_single),
//                        modifier= Modifier
//                            .size(30.dp, 30.dp)
//                            .align(Alignment.TopEnd),
//                        contentDescription =null )
//                }
                }

                Divider(thickness = 0.2.dp, color = Color.Black, modifier = Modifier.padding(5.dp, 5.dp, 5.dp, 5.dp))
            }

            Row(
                modifier = Modifier
                    .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                    .fillMaxWidth()
                    .padding(15.dp, 4.dp, 18.dp, 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "UPLOAD SIZE", fontSize = 16.sp)

                Spacer(
                    modifier = Modifier
                        .padding(8.dp)
                        .width(1.dp)
                        .background(color = Color.Black)
                )
                Box(
                    modifier = Modifier
                        .background(color = Color.LightGray, shape = RoundedCornerShape(8.dp))
                        .fillMaxWidth()
                        .padding(1.dp, 0.dp, 1.dp, 0.dp)
                ) {
                    Row(Modifier.padding(4.dp), horizontalArrangement = Arrangement.spacedBy(space = 3.dp)) {
                        // Tiny
                        BoxSizeUpload(modifier = Modifier.weight(1f), size = "Tiny")

                        Divider(
                            color = Color.Gray,
                            modifier = Modifier
                                .height(25.dp) // fill the max height
                                .width(1.dp)
                        )
                        // Small
                        BoxSizeUpload(modifier = Modifier.weight(1f), size = "Small")
                        Divider(
                            color = Color.Gray,
                            modifier = Modifier
                                .height(25.dp) // fill the max height
                                .width(1.dp)
                        )
                        // Medium
                        BoxSizeUpload(modifier = Modifier.weight(1f), size = "Medium")
                        Divider(
                            color = Color.Gray,
                            modifier = Modifier
                                .height(25.dp) // fill the max height
                                .width(1.dp)
                        )
                        // Large
                        BoxSizeUpload(modifier = Modifier.weight(1f), size = "Large")
                        Divider(
                            color = Color.Gray,
                            modifier = Modifier
                                .height(25.dp) // fill the max height
                                .width(1.dp)
                        )
                        // Original
                        BoxSizeUpload(modifier = Modifier.weight(1f), size = "Original")
                    }
                }
            }

            Divider(thickness = 0.2.dp, color = Color.Black, modifier = Modifier.padding(5.dp, 5.dp, 5.dp, 5.dp))
            if (loginViewModel.isLogInSuccess.collectAsState(initial = false).value!!) {
                Box(
                    modifier = Modifier
                        .clickable { onWaypointgroupsPressed() }
                        .fillMaxWidth()
                ) {
                    Text(text = "Waypoint Group")
                    Row(modifier = Modifier.align(Alignment.TopEnd)) {
                        Text(text = "${loginViewModel.currentUser.collectAsState(initial = User("","", emptyList(),"","")).value!!.groupNameCheck}")
                        Image(
                            painterResource(id = R.drawable.ic_chevron_right_single),
                            contentDescription = null,
                            modifier = Modifier
                                .size(23.dp, 23.dp)
                        )
                    }
                }
            }
        }
        Log.d("Setting", "UploadOptions")
    }


}
