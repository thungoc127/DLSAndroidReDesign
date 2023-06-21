package com.example.dlsandroidredesign.ui.mainScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dlsandroidredesign.R



@Composable
fun ZoomView(onGalleryButtonPressed:()->Unit,onZoomOnePressed:()->Unit, onZoomTwoPressed:()->Unit,onZoomThreePressed:()->Unit) {
    Column(
        modifier = Modifier
            .background(Color.Black)
            .padding(0.dp, 20.dp, 0.dp, 30.dp),
    ) {
        //1x
//Zoom VIEW
        var zoomRatio by remember {
            mutableStateOf("1x")
        }
        Box(modifier = Modifier.size(40.dp, 40.dp), contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(R.drawable.ic_camera_metering_center_weighted_average_single),
                contentDescription = null
            )
            Text(
                text = "$zoomRatio",
                Modifier.size(20.dp, 20.dp),
                fontSize = 16.sp,
                color = Color.White
            )

        }


        Column(modifier = Modifier.weight(4f).align(Alignment.CenterHorizontally), verticalArrangement = Arrangement.spacedBy(5.dp),) {
            //3x
            Box(modifier = Modifier
                .size(40.dp, 40.dp)
                .background(
                    shape = RoundedCornerShape(200.dp),
                    color = Color.Gray
                )
                .clickable {
                    zoomRatio = "3x"
                    onZoomThreePressed()
                },
                contentAlignment = Alignment.Center,

            ) {
                Text(text = "3x", fontSize = 16.sp, color = Color.White)
            }
            //2x
            Box(modifier = Modifier
                .size(40.dp, 40.dp)
                .background(
                    shape = RoundedCornerShape(200.dp),
                    color = Color.Gray
                )
                .clickable {
                    zoomRatio = "2x"
                    onZoomTwoPressed()
                },
                contentAlignment = Alignment.Center

            ) {
                Text(text = "2x", fontSize = 16.sp, color = Color.White)
            }
            //1x
            Box(
                modifier = Modifier
                    .size(40.dp, 40.dp)
                    .background(
                        shape = RoundedCornerShape(200.dp),
                        color = Color.Gray
                    )
                    .clickable {
                        zoomRatio = "1x"
                        onZoomOnePressed()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "1x", fontSize = 16.sp, color = Color.White)
            }
        }


        //uploadBtn
        Box(
            modifier = Modifier
                .size(40.dp, 40.dp)
                .background(
                    shape = RoundedCornerShape(200.dp),
                    color = Color.Gray
                )
                .clickable { onGalleryButtonPressed() },
            contentAlignment = Alignment.Center,

            ) {
            Image(
                painter = painterResource(R.drawable.ic_square_and_arrow_up_on_square_single),
                modifier = Modifier.size(20.dp, 20.dp),
                contentDescription = null
            )
        }
    }
}

