package com.example.dlsandroidredesign.ui.setting

import android.util.Log
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SaveToPhotoLibraryOptions(viewModel: SettingFragmentViewModel = hiltViewModel()) {
    val photoOptionIndex = viewModel.photoSize.collectAsState(initial = "Original").value
    Column(modifier = Modifier.padding(20.dp, 0.dp, 20.dp, 20.dp)) {
        Text(text = "SAVE TO PHOTO LIBRARY OPTIONS")
        Row(
            modifier = Modifier
                .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                .fillMaxWidth()
                .padding(15.dp, 4.dp, 18.dp, 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "PHOTO SIZE", fontSize = 16.sp)
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
                    Box(
                        Modifier
                            .weight(1f)
                            .background(
                                color = if (photoOptionIndex == "Tiny") Color.White else Color.LightGray,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable(onClick = { viewModel.setPhotoSize("Tiny") })
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Tiny")
                    }
                    Divider(
                        color = Color.Gray,
                        modifier = Modifier
                            .height(25.dp) // fill the max height
                            .width(1.dp)
                    )
                    // Small
                    BoxSizeSave(modifier = Modifier.weight(1f), size = "Small")

                    Divider(
                        color = Color.Gray,
                        modifier = Modifier
                            .height(25.dp) // fill the max height
                            .width(1.dp)
                    )
                    // Medium
                    BoxSizeSave(modifier = Modifier.weight(1f), size = "Medium")

                    Divider(
                        color = Color.Gray,
                        modifier = Modifier
                            .height(25.dp) // fill the max height
                            .width(1.dp)
                    )
                    // Large
                    BoxSizeSave(modifier = Modifier.weight(1f), size = "Large")

                    Divider(
                        color = Color.Gray,
                        modifier = Modifier
                            .height(25.dp) // fill the max height
                            .width(1.dp)
                    )
                    // Original
                    BoxSizeSave(modifier = Modifier.weight(1f), size = "Original")
                }
            }
        }
    }

    Log.d("Setting", "SaveOptions")
}
