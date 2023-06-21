@file:OptIn(ExperimentalPermissionsApi::class)

package com.example.dlsandroidredesign

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.dlsandroidredesign.ui.mainScreen.FullPreviewScreen
import com.example.dlsandroidredesign.ui.theme.DLSAndroidReDesignTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

@OptIn(ExperimentalMaterialApi::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @get:RequiresApi(Build.VERSION_CODES.O)
    @get:SuppressLint("SuspiciousIndentation")
    @ExperimentalPermissionsApi
    val context: Context = this
    @OptIn(ExperimentalPermissionsApi::class)
    @SuppressLint("StateFlowValueCalledInComposition", "MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: Check file exist or not
        copySectionsAssetToFile()




        setContent {
            val locationPermission = remember{mutableStateOf(Manifest.permission.ACCESS_FINE_LOCATION)}.value
            val cameraPermission = remember { mutableStateOf(Manifest.permission.CAMERA) }.value
            val storagePermission = remember { mutableStateOf(Manifest.permission.WRITE_EXTERNAL_STORAGE) }.value
            val permissionState = rememberMultiplePermissionsState(
                permissions = listOf(
                    cameraPermission,
                    locationPermission,
                    storagePermission
                )
            )
            Log.d("getLocationProcess: ", "${permissionState.toString()}")
            val viewModel: ImageLocationInfoViewModel by viewModels()


            LaunchedEffect(permissionState) {
                permissionState.launchMultiplePermissionRequest()
            }
            DLSAndroidReDesignTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Gray
                ) {
                    FullPreviewScreen()
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



