@file:OptIn(ExperimentalMaterialApi::class)

package com.example.dlsandroidredesign.ui.waypointGroup

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dlsandroidredesign.R
import com.example.dlsandroidredesign.domain.entity.User
import com.example.dlsandroidredesign.domain.entity.Waypointgroup
import com.example.dlsandroidredesign.ui.login.LogInViewModel

@Composable
fun WaypointgroupsFragment(logInViewModel: LogInViewModel = hiltViewModel(), waypointgroupViewModel: WaypointgroupViewModel = hiltViewModel()) {
    WaypointgroupsFragment(
        onRefreshPress = { waypointgroupViewModel.getWaypointgroup() },
        onWaypointgroupChecked = { groupID: String, groupName: String -> waypointgroupViewModel.setGroupIdAndName(groupID, groupName) },
        waypointgroupList = logInViewModel.currentUser.collectAsState(initial = User("", "", emptyList(), "", "")).value?.waypointgroups,
        groupIdCheck = logInViewModel.currentUser.collectAsState(initial = User("", "", emptyList(), "", "")).value?.groupIdCheck
    )
}

@Composable
fun WaypointgroupsFragment(onRefreshPress: () -> Unit, onWaypointgroupChecked: (groupID: String, groupName: String) -> Unit, waypointgroupList: List<Waypointgroup>?, groupIdCheck: String?) {
//    val apiKey = preferenceDataStore.getLoginSharedInfoList().collectAsState(initial = LoginDTO(null,null,null,null,null)).value.id
//    val retrofitInstance = RetoInstance().getInstance()
//    val apiService = retrofitInstance.create(DLSService::class.java)

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // TOP LAYOUT
        Row(
            Modifier
                .fillMaxWidth()
                .size(25.dp, 35.dp)
                .background(color = Color(0xFFDDDDDD))
                .padding(3.dp, 3.dp)
                .align(Alignment.CenterHorizontally)

        ) {
            Image(painter = painterResource(id = R.drawable.ic_xmark_single), contentDescription = null, modifier = Modifier.size(17.dp, 17.dp))
            Text(text = " Options", color = Color(0xFF00B0FF), fontSize = 23.sp, modifier = Modifier.weight(1f))
            Text(text = "Waypoint Groups", color = Color(0xFF000000), fontSize = 23.sp, textAlign = TextAlign.Center)
        }
        Row(modifier = Modifier.align(Alignment.End)) {
            Image(painter = painterResource(id = R.drawable.ic_arrow_triangle_2_circlepath_single), contentDescription = null, modifier = Modifier.size(23.dp, 23.dp))
            Text(
                text = "Refesh List",
                modifier = Modifier.clickable { onRefreshPress() },
                color = Color.Blue,
                fontSize = 20.sp
            )
        }
        if (waypointgroupList.isNullOrEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp, 4.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(text = "You are currently do not have any groupWaypoint")
            }
        } else {
            for (waypointgroup in waypointgroupList!!) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp, 4.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "${waypointgroup.groupname}",
                        modifier = Modifier
                            .clickable {
                                onWaypointgroupChecked(waypointgroup.groupid!!, waypointgroup.groupname!!)
                            }
                            .weight(1f),
                        fontSize = 18.sp
                    )
                    if (groupIdCheck == waypointgroup.groupid)Image(painter = painterResource(id = R.drawable.ic_checkmark_single), contentDescription = null, modifier = Modifier.size(19.dp, 19.dp), alignment = Alignment.BottomEnd)
                }
                Divider(thickness = 0.2.dp, color = Color.Black)
            }
        }
    }
}
