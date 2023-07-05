@file:Suppress("FunctionName")
@file:OptIn(ExperimentalMaterialApi::class)

package com.example.dlsandroidredesign.ui.mainScreen

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.FLASH_MODE_OFF
import androidx.camera.core.ImageCapture.FLASH_MODE_ON
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dlsandroidredesign.data.local.PreferencesDataStore
import com.example.dlsandroidredesign.ui.gallery.GalleryScreen
import com.example.dlsandroidredesign.ui.login.LogInViewModel
import com.example.dlsandroidredesign.ui.setting.SettingFragmentViewModel
import com.example.dlsandroidredesign.ui.setting.settingFragment
import eu.wewox.modalsheet.ExperimentalSheetApi
import eu.wewox.modalsheet.ModalSheet
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalSheetApi::class, ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun FullPreviewScreen(viewModel: MainScreenViewModel = hiltViewModel(), imageLocationInfoViewModel: ImageLocationInfoViewModel = hiltViewModel(), settingFragmentViewModel: SettingFragmentViewModel = hiltViewModel(), loginViewModel: LogInViewModel = hiltViewModel(), mainScreenViewModel: MainScreenViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    // Camera
    var cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val lifecycleOwner = LocalLifecycleOwner.current
    var cameraSelectorState by remember { mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA) }
    var flashModeState by remember { mutableStateOf(FLASH_MODE_OFF) }
    val imageCapture by remember { mutableStateOf(ImageCapture.Builder().build()) }
    val preview by remember { mutableStateOf(androidx.camera.core.Preview.Builder().build()) }
    // BottomFragment
    val preferenceDataStore = PreferencesDataStore(LocalContext.current)
    val previewView = remember { PreviewView(context) }
    var savedUri by remember { mutableStateOf<Uri?>(null) }
// ScreenShotBox

    // Zoom value
    var zoomRatio by remember { mutableStateOf<Float>(0.0f) }
    // Create time-stamped file name and MediaStore entry

    // Create output options

    val outputOptions = ImageCapture.OutputFileOptions.Builder(
        context.contentResolver,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        imageLocationInfoViewModel.contentValues

    ).build()

    fun bindCameraUseCases() {
        val cameraProvider = cameraProviderFuture.get()
        cameraProvider.unbindAll()
        // Bind the preview use case to the camera with the specified selector
        val camera = cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelectorState,
            preview,
            imageCapture
        )
        preview.setSurfaceProvider(previewView.surfaceProvider)
    }

    fun takePicture() {
        imageCapture?.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    // Image capture is successful
                    // Access the captured image from outputFileResults
                    val savedUriCapture = outputFileResults.savedUri
                    Log.d("Upload", "savedUriCapture:$savedUriCapture")

                    coroutineScope.launch {
                        Log.d("Upload", "currentuser:${imageLocationInfoViewModel.currentUser.first()}")
                    }

                    Log.d("Upload", "uri:${imageLocationInfoViewModel.processedImage.value}")
                    imageLocationInfoViewModel.processAutoUpload(savedUriCapture)
                }

                override fun onError(exception: ImageCaptureException) {
                    // Image capture failed
                    Log.e("CameraX", "Image capture failed", exception)
                }
            }

        )
    }

    fun switchCamera() {
        cameraSelectorState = if (cameraSelectorState == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
        bindCameraUseCases()
    }

    fun flashSwitch() {
        flashModeState = if (flashModeState == FLASH_MODE_OFF) {
            FLASH_MODE_ON
        } else {
            FLASH_MODE_OFF
        }
        // Bind the preview use case to the camera with the specified selector
        imageCapture.flashMode = flashModeState
    }

    fun zoomCamera() {
        val cameraProvider = cameraProviderFuture.get()
        // Bind the preview use case to the camera with the specified selector
        val camera = cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelectorState,
            preview,
            imageCapture
        )

        val cameraControl = camera.cameraControl
        cameraControl.setLinearZoom(zoomRatio)
    }

    Row(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .weight(1f)
                .background(color = Color.Black),
            contentAlignment = Alignment.Center
        ) {
            ZoomView(
                onGalleryButtonPressed = {
                    mainScreenViewModel.getAllImage()
                    viewModel.galleryModalSheetVisible.value = true
                },
                onZoomOnePressed = {
                    zoomRatio = 0.0f
                    zoomCamera()
                },
                onZoomTwoPressed = {
                    zoomRatio = 0.1f
                    zoomCamera()
                },
                onZoomThreePressed = {
                    zoomRatio = 0.2f
                    zoomCamera()
                }
            )
        }
        Box(modifier = Modifier.weight(8f)) {
            Box(modifier = Modifier) {
                AndroidView(
                    factory = {
                        bindCameraUseCases()
                        previewView
                    },
                    modifier = Modifier.fillMaxSize()
                )
                LocationView()
            }
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .background(color = Color.Black)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Setting(
                onSettingPressed = {
                    coroutineScope.launch { viewModel.settingSheetState.show() }
                },
                onCameraCapturePressed = {
                    takePicture()
                },
                onSwitchCameraPress = { switchCamera() },
                onFlashPressed = { flashSwitch() },
                bmp = imageLocationInfoViewModel.bmp.value
            )
        }
    }

    ModalSheet(
        visible = viewModel.galleryModalSheetVisible.value,
        onVisibleChange = { viewModel.galleryModalSheetVisible.value = it }

    ) {
        GalleryScreen()
    }

    ModalSheet(
        sheetState = viewModel.settingSheetState,
        onSystemBack = { coroutineScope.launch { viewModel.settingSheetState.hide() } },
        content = {
            settingFragment()
        }
    )
}
