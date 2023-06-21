@file:OptIn(ExperimentalMaterialApi::class)

package com.example.dlsandroidredesign
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dlsandroidredesign.ui.login.LogInScreen
import com.example.dlsandroidredesign.ui.login.LogInViewModel
import com.example.dlsandroidredesign.ui.waypointGroup.WaypointgroupsFragment
import eu.wewox.modalsheet.ExperimentalSheetApi
import eu.wewox.modalsheet.ModalSheet
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class, ExperimentalSheetApi::class)
@Composable
fun ModalBottomSheetLoginAndWaypointgroups(logInViewModel: LogInViewModel= hiltViewModel(), waypointGroupSheetState:ModalBottomSheetState){
    val coroutineScope = rememberCoroutineScope()

    ModalSheet(
        visible = logInViewModel.loginVisible.value,
        onVisibleChange = {
            logInViewModel.errorMessage.value= ""
            coroutineScope.launch { logInViewModel.setLoginVisible(it)} },
        content={
                LogInScreen()
        }
    )

    ModalSheet(
        sheetState = waypointGroupSheetState,
        onSystemBack = {
            coroutineScope.launch { waypointGroupSheetState.hide() }
        },
        content={
                WaypointgroupsFragment()
        }
    )
}