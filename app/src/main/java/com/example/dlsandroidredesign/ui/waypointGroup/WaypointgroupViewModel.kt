package com.example.dlsandroidredesign.ui.waypointGroup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dlsandroidredesign.domain.usecase.GetCurrentUser
import com.example.dlsandroidredesign.domain.usecase.RefreshListWayPointGroup
import com.example.dlsandroidredesign.domain.usecase.SetGroupIdAndName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WaypointgroupViewModel @Inject constructor(private val refreshListWayPointGroup: RefreshListWayPointGroup, private val getCurrentUser: GetCurrentUser, private val setGroupId: SetGroupIdAndName) : ViewModel() {
    fun getWaypointgroup() {
        viewModelScope.launch { refreshListWayPointGroup.invoke() }
    }
    fun setGroupIdAndName(groupId: String, groupName: String) {
        viewModelScope.launch {
            setGroupId.invoke(groupId, groupName)
        }
    }
}
