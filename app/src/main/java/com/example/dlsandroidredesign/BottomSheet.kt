@file:OptIn(ExperimentalMaterialApi::class)

package com.example.dlsandroidredesign
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.example.dlsandroidredesign.ui.login.LogInScreen
import com.example.dlsandroidredesign.ui.setting.settingFragment
import com.example.dlsandroidredesign.ui.waypointGroup.WaypointgroupsFragment
import eu.wewox.modalsheet.ExperimentalSheetApi
import eu.wewox.modalsheet.ModalSheet
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalSheetApi::class)
@Composable
fun ModalBottomSheetSetting(sheetState:ModalBottomSheetState,isSettingFragmentShow: Boolean){

}

@OptIn(ExperimentalMaterialApi::class, ExperimentalSheetApi::class)
@Composable
fun ModalBottomSheetLoginAndWaypointgroups(sheetState:ModalBottomSheetState, isLoginFragmentShow:Boolean, isWaypointgroupsFragment:Boolean){
    val CorotineScope = rememberCoroutineScope()

    ModalSheet(
        sheetState = sheetState,
        onSystemBack = {
            CorotineScope.launch { sheetState.hide() }
                       },
        content={
            if(isLoginFragmentShow)
            {
                LogInScreen()
            }
            if(isWaypointgroupsFragment)
            {
                WaypointgroupsFragment()
            }
        }
    )
}