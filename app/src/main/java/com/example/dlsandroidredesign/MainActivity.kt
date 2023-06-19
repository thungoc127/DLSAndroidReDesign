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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.room.Room
import com.example.dlsandroidredesign.ui.theme.DLSAndroidReDesignTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream


@OptIn(ExperimentalMaterialApi::class)
class MainActivity : ComponentActivity() {

    @get:RequiresApi(Build.VERSION_CODES.O)
    @get:SuppressLint("SuspiciousIndentation")
    @ExperimentalPermissionsApi
    val context: Context = this

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            ImageLocationInfoDatabase::class.java,
            "contacts.db"
        )
            .addTypeConverter(Converters())
            .build()
    }
    @OptIn(ExperimentalPermissionsApi::class)
    private val viewModel by viewModels<ImageLocationInfoViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ImageLocationInfoViewModel(application,db.imageLocaitonInfoDAO) as T
                }
            }
        }
    )

    @SuppressLint("StateFlowValueCalledInComposition", "MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: Check file exist or not
        copySectionsAssetToFile()

        setContent {
            val locationObject by viewModel.locationObject.collectAsStateWithLifecycle()
            Log.d("location act: ", locationObject.toString())

            DLSAndroidReDesignTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Gray
                ) {
                    val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
                    val cameraPermission = Manifest.permission.CAMERA
                    val storagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE
                    val permissionState = rememberMultiplePermissionsState(
                        permissions = listOf(
                            cameraPermission,
                            locationPermission,
                            storagePermission
                        )
                    )

                    FullPreviewScreen(viewModel)

                    LaunchedEffect(permissionState) {
                        permissionState.launchMultiplePermissionRequest()
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



@Composable
fun test(location:LocationObject){
    Column {
        Column() {
            Text(text = "${location.date}")
            Text(text = "${location.lat}")
            Text(text = "${location.lon}")

        }
    }
}