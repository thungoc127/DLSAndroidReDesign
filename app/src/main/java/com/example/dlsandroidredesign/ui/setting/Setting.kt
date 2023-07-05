@file:OptIn(
    ExperimentalSheetApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class
)

package com.example.dlsandroidredesign.ui.setting
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dlsandroidredesign.ModalBottomSheetLoginAndWaypointgroups
import com.example.dlsandroidredesign.R
import com.example.dlsandroidredesign.ui.login.LogInViewModel
import com.example.dlsandroidredesign.ui.mainScreen.MainScreenViewModel
import eu.wewox.modalsheet.ExperimentalSheetApi
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun settingFragment(viewModel: MainScreenViewModel = hiltViewModel(), loginViewModel: LogInViewModel = hiltViewModel()) {
    val coroutineScope = rememberCoroutineScope()
    Column(
        Modifier
            .fillMaxSize()
            .background(color = Color(0xFFE1DFDF))
            .verticalScroll(rememberScrollState())
    ) {
        // TOP LAYOUT
        Row(
            Modifier
                .background(color = Color(0xFFF6F3F3))
                .fillMaxWidth()
                .size(17.dp, 30.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(painter = painterResource(id = R.drawable.ic_xmark_single), contentDescription = null, modifier = Modifier.size(17.dp, 17.dp))
            Text(text = " Close", color = Color(0xFF00B0FF), fontSize = 20.sp, modifier = Modifier.weight(1f))
            Text(text = "Option", color = Color(0xFF000000), fontSize = 20.sp, modifier = Modifier.weight(1f))
        }

        // Spacer
        Spacer(modifier = Modifier.padding(bottom = 10.dp))
        // cardView
        PhotoDisplayOptions()
        // SAVE TO PHOTO LIBRARY OPTIONS
        SaveToPhotoLibraryOptions()
        // UPLOAD OPTION
        UploadOptions(
            onLogInPressed = { coroutineScope.launch { loginViewModel.setLoginVisible(true) } },
            onWaypointgroupsPressed = { coroutineScope.launch { viewModel.waypointGroupSheetState.show() } }
        )
    }

    ModalBottomSheetLoginAndWaypointgroups(waypointGroupSheetState = viewModel.waypointGroupSheetState)
}

@Composable
fun BoxSizeSave(modifier: Modifier, size: String, settingFragmentViewModel: SettingFragmentViewModel = hiltViewModel()) {
    val saveSize = settingFragmentViewModel.photoSize.collectAsStateWithLifecycle(initialValue = "Original").value

    Box(
        modifier = modifier
            .background(
                color = if (saveSize == size) Color.White else Color.LightGray,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = { settingFragmentViewModel.setPhotoSize(size) })
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = size)
    }
}

@Composable
fun BoxSizeUpload(modifier: Modifier, size: String, settingFragmentViewModel: SettingFragmentViewModel = hiltViewModel()) {
    val uploadSize = settingFragmentViewModel.uploadSize.collectAsStateWithLifecycle(initialValue = "Original").value

    Box(
        modifier = modifier
            .background(
                color = if (uploadSize == size) Color.White else Color.LightGray,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = { settingFragmentViewModel.setUploadSize(size) })
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = size)
    }
}

@Composable
fun MyPhotoDisplaySwitch(title: String, checked: Boolean, onCheckChanged: (Boolean) -> Unit) {
    Row(
        modifier = Modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, color = Color(0xFF00B0FF))
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
