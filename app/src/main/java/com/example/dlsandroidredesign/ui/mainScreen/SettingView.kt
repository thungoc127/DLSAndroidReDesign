package com.example.dlsandroidredesign.ui.mainScreen

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.dlsandroidredesign.R

@Composable
fun Setting(onSettingPressed: () -> Unit, onCameraCapturePressed: () -> Unit, onSwitchCameraPress: () -> Unit, onFlashPressed: () -> Unit, bmp: Bitmap?) {
    var painter = rememberAsyncImagePainter(model = bmp)
    var flashState by remember {
        mutableStateOf(true)
    }
    fun Modifier.customThemeModifierRound() = size(50.dp, 50.dp).background(shape = RoundedCornerShape(200.dp), color = Color.Gray)
    fun Modifier.customThemeModifierSquare() = size(70.dp, 70.dp).background(shape = RoundedCornerShape(18.dp), color = Color.White)
    fun Modifier.customThemeImage() = size(20.dp, 20.dp)

    Column(
        modifier = Modifier
            .background(Color.Black)
            .padding(0.dp, 30.dp, 0.dp, 15.dp)
            .fillMaxHeight(),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Setting
        Box(
            modifier = Modifier.customThemeModifierRound()
                .clickable { onSettingPressed() }
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.ic_gearshape_fill_single),
                modifier = Modifier.customThemeImage(),
                contentDescription = null
            )
        }

        // Spacer
        Spacer(modifier = Modifier.height(8.dp))
        // Flash (ic_bolt_slash_fill_single.xml)
        Box(
            modifier = Modifier.customThemeModifierRound()
                .weight(1f)
                .clickable {
                    onFlashPressed()
                    flashState = !flashState
                },
            contentAlignment = Alignment.Center

        ) {
            Image(
                painter = painterResource(R.drawable.ic_bolt_single),
                modifier = Modifier.customThemeImage(),
                contentDescription = null
            )
            if (flashState) {
                Image(
                    painter = painterResource(R.drawable.ic_bolt_slash_fill_single),
                    modifier = Modifier.customThemeImage(),
                    contentDescription = null
                )
            }
        }
        // Spacer
        Spacer(modifier = Modifier.height(8.dp))
        // Camera Capture
        Box(
            modifier = Modifier.customThemeModifierSquare()
                .clickable {
                    onCameraCapturePressed()
                }
                .weight(1.3f),
            contentAlignment = Alignment.Center

        ) {
            Image(
                painter = painterResource(R.mipmap.ic_capture),
                modifier = Modifier.size(70.dp, 70.dp),
                contentDescription = null
            )
        }
        // Spacer
        Spacer(modifier = Modifier.height(8.dp))
        // rotate camera
        Box(
            modifier = Modifier.customThemeModifierRound()
                .clickable { onSwitchCameraPress() }
                .weight(1f),
            contentAlignment = Alignment.Center

        ) {
            Image(
                painter = painterResource(R.drawable.ic_camera_rotate_fill_single),
                modifier = Modifier.customThemeImage(),
                contentDescription = null
            )
        }
        // Spacer
        Spacer(modifier = Modifier.height(8.dp))
        // camera image
        Box(
            modifier = Modifier.customThemeModifierSquare()
                .weight(1.3f),
            contentAlignment = Alignment.Center

        ) {
            Image(
                painter = painter,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(shape = RoundedCornerShape(18.dp)),
                contentScale = ContentScale.FillBounds,
                contentDescription = null
            )
        }
    }
}
