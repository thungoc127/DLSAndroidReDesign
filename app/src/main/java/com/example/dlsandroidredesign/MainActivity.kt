@file:OptIn(ExperimentalPermissionsApi::class)

package com.example.dlsandroidredesign

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.dlsandroidredesign.ui.mainScreen.FullPreviewScreen
import com.example.dlsandroidredesign.ui.mainScreen.ImageLocationInfoViewModel
import com.example.dlsandroidredesign.ui.theme.DLSAndroidReDesignTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

@OptIn(ExperimentalPermissionsApi::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: ImageLocationInfoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: Check file exist or not
        copySectionsAssetToFile()
        setContent {
            val permissionState = rememberMultiplePermissionsState(
                permissions = listOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_MEDIA_IMAGES
                    )
            )
//            Log.d("getLocationProcess: ", "$permissionState")

            LaunchedEffect(Unit) {
                permissionState.launchMultiplePermissionRequest()
            }

            if (permissionState.permissions.firstOrNull { it.permission == Manifest.permission.ACCESS_FINE_LOCATION }?.status?.isGranted == true) {
                viewModel.startFetchingLocation()
            }

            DLSAndroidReDesignTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Gray
                ) {
                    Box(){
                        FullPreviewScreen()
                        Column(
                            modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                    if(permissionState.permissions.firstOrNull{ it.permission == Manifest.permission.CAMERA }?.status?.isGranted == false||
                        permissionState.permissions.firstOrNull{ it.permission == Manifest.permission.ACCESS_FINE_LOCATION }?.status?.isGranted == false
                            ){

                            if(permissionState.permissions.firstOrNull{ it.permission == Manifest.permission.CAMERA }?.status?.isGranted == false)
                            {
                                Text(text = "Please grant camera permission to use the camera feature.", color = Color.White)
                            }
                            if(permissionState.permissions.firstOrNull{ it.permission == Manifest.permission.ACCESS_FINE_LOCATION }?.status?.isGranted == false)
                            {
                                Text(text = "Please grant location permission to use the location feature.", color = Color.White)
                            }
                                Button(onClick = { permissionState.launchMultiplePermissionRequest() }) {
                                    Text(text = "Grant Permission")
                                }

                        }
                        }

                }
            }
        }
        }
    }


    private fun copySectionsAssetToFile() {
        try {
            val fileName = "sections.mmpk"
            val assetManager = assets
            val inputStream: InputStream = assetManager.open(fileName)

            val outputDir = File(getExternalFilesDir(null), fileName)
            val outputStream: OutputStream = FileOutputStream(outputDir)

            val bufferSize = 1024
            val buffer = ByteArray(bufferSize)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }

            inputStream.close()
            outputStream.close()
        } catch (error: Exception) {
            error
        }
    }
}
