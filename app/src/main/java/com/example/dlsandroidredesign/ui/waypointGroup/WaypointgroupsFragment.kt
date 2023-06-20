@file:OptIn(ExperimentalMaterialApi::class)

package com.example.dlsandroidredesign.ui.waypointGroup

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable

@Composable
fun WaypointgroupsFragment(){
//    val coroutineScope= rememberCoroutineScope()
//    val context = LocalContext.current
//    val preferenceDataStore = PreferencesDataStore(context)
//    var waypointgroups = preferenceDataStore.getWaypointgroup().collectAsState(initial = emptyList()).value
////    val apiKey = preferenceDataStore.getLoginSharedInfoList().collectAsState(initial = LoginDTO(null,null,null,null,null)).value.id
////    val retrofitInstance = RetoInstance().getInstance()
////    val apiService = retrofitInstance.create(DLSService::class.java)
//    var groupIdCheck = preferenceDataStore.getWaypointgroupCheck.collectAsState(initial = null).value
//
//    suspend fun getWayPointGroups(apiKey:String?){
////    val response = apiService.getWayPointGroups(apiKey = apiKey)
//    val body = response.body()
//    withContext(Dispatchers.Main) {
//       if(response.isSuccessful ) {
//           preferenceDataStore.setWaypointGroup(body!!.waypointgroups!!)
//           preferenceDataStore.setWaypointgroupCheck(body.waypointgroups!![0].groupid!!)
//           Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
//       }
//       else
//       {
//           Toast.makeText(context, "Fail", Toast.LENGTH_SHORT).show()
//
//       }
//   }
//}
//    Column(
//        Modifier
//            .fillMaxSize()
//            .verticalScroll(rememberScrollState()))
//        {
//            //TOP LAYOUT
//            Row(
//                Modifier
//                    .fillMaxWidth()
//                    .size(25.dp, 35.dp)
//                    .background(color = Color(0xFFDDDDDD))
//                    .padding(3.dp, 3.dp)
//                    .align(Alignment.CenterHorizontally)
//
//            ) {
//                Image(painter = painterResource(id = R.drawable.ic_xmark_single), contentDescription =null, modifier = Modifier.size(17.dp,17.dp) )
//                Text(text = " Options", color = Color(0xFF00B0FF), fontSize = 23.sp,modifier= Modifier.weight(1f))
//                Text(text = "Waypoint Groups",color= Color(0xFF000000),fontSize = 23.sp, textAlign = TextAlign.Center)
//            }
//            Row(modifier = Modifier.align(Alignment.End)) {
//                Image(painter = painterResource(id = R.drawable.ic_arrow_triangle_2_circlepath_single),contentDescription = null, modifier = Modifier.size(23.dp,23.dp))
//                Text(text = "Refesh List",
//                modifier=Modifier.clickable { coroutineScope.launch(Dispatchers.Unconfined) { getWayPointGroups(apiKey) }  },
//                    color = Color.Blue, fontSize = 20.sp
//                    )
//            }
//            if (waypointgroups.isNullOrEmpty()) {
//                Row(modifier= Modifier
//                    .fillMaxWidth()
//                    .padding(4.dp, 4.dp)
//                    .align(Alignment.CenterHorizontally)){
//                    Text(text = "You are currently do not have any groupWaypoint")
//                }
//            }
//            else{
//                for (waypointgroup in waypointgroups!!){
//                    Row(modifier= Modifier
//                        .fillMaxWidth()
//                        .padding(4.dp, 4.dp)
//                        .align(Alignment.CenterHorizontally)){
//                        Text(text = "${waypointgroup.groupname}", modifier = Modifier
//                            .clickable {
//                                runBlocking { preferenceDataStore.setWaypointgroupCheck(
//                                    waypointgroup.groupid!!
//                                ) }
//                            }
//                            .weight(1f), fontSize = 18.sp)
//                        if(groupIdCheck==waypointgroup.groupid)Image(painter = painterResource(id = R.drawable.ic_checkmark_single), contentDescription =null, modifier = Modifier.size(19.dp,19.dp), alignment = Alignment.BottomEnd )
//                    }
//                    Divider( thickness = 0.2.dp, color = Color.Black)
//
//                }
//            }
//        }
    }

