//package com.example.dlsandroidredesign.ui.mainScreen
//
//import android.content.ContentValues
//import android.content.Context
//import android.os.Build
//import android.os.Environment
//import android.provider.MediaStore
//import android.util.Log
//import androidx.camera.core.ImageCapture
//import androidx.camera.core.ImageCaptureException
//import androidx.camera.view.PreviewView
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.material3.Button
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.core.content.ContextCompat
//import java.io.File
//
//
//@Composable
//fun CameraPreview(
//    imageCapture: ImageCapture,
//    flashModeState: Int,
//    onFlashModeStateChanged: (flashMode: Int) -> Unit,
//    previewView: PreviewView
//) {
//
//
//    Box(modifier = Modifier.fillMaxSize()) {
//
//
//
//
///*        Button(onClick = {
//            cameraSelectorState = if (cameraSelectorState == CameraSelector.DEFAULT_BACK_CAMERA) {
//                CameraSelector.DEFAULT_FRONT_CAMERA
//            } else {
//                CameraSelector.DEFAULT_BACK_CAMERA
//            }
//        }) {
//            "Switch Camera"
//        }*/
//
//        //FLASH
//        Button(
//            onClick = {
//                onFlashModeStateChanged.invoke(
//                    if (flashModeState == ImageCapture.FLASH_MODE_ON) {
//                        Log.d("FlashButtonPress", "FLasOFF")
//                        ImageCapture.FLASH_MODE_OFF
//                    } else {
//                        Log.d("FlashButtonPress", "FLashON")
//                        ImageCapture.FLASH_MODE_ON
//                    }
//                )
//            },
//            modifier = Modifier.align(alignment = Alignment.TopCenter)
//
//        ) {
//        }
//    }
//}
//
//
//private fun takePicture(context: Context) {
//    var imageCapture = ImageCapture.Builder().build()
//    // Create time-stamped file name and MediaStore entry
//    val fileName = "IMG_${System.currentTimeMillis()}.jpg"
//    val contentValues = ContentValues().apply {
//        put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
//        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
//            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
//        }
//    }
//    val outputDirectoryName = "CameraX-Images"
//    val outputDirectory =
//        File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), outputDirectoryName)
//
//    // Create output options
//    val outputOptions = ImageCapture.OutputFileOptions.Builder(
//        context.contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
//    ).build()
//
//    // Capture the picture
//    imageCapture.takePicture(
//        outputOptions,
//        ContextCompat.getMainExecutor(context),
//        object : ImageCapture.OnImageSavedCallback {
//            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
//                // Image capture is successful
//                // Access the captured image from outputFileResults
//                val savedUri = outputFileResults.savedUri
//                // Handle further operations with the saved image URI
//            }
//
//            override fun onError(exception: ImageCaptureException) {
//                // Image capture failed
//                Log.e("CameraX", "Image capture failed", exception)
//            }
//        }
//    )
//}
//
//
///*
//@Composable
//fun SimpleCameraPreviewFront() {
//    val lifecycleOwner = LocalLifecycleOwner.current
//    val context = LocalContext.current
//    // Initialize the cameraProviderFuture
//    var cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
//    var cameraSelectorState by remember { mutableStateOf(CameraSelector.DEFAULT_FRONT_CAMERA) }
//    val cameraSelector = rememberUpdatedState(cameraSelectorState)
//
//
//    val previewView = PreviewView(context)
//
//    // Set up the camera and preview
//    // Create a preview use case
//    // Set the surface provider for the preview
//
//
//    // Unbind any existing use cases from the camera provider
//    LaunchedEffect(cameraSelector.value) {
//        val cameraProvider = cameraProviderFuture.get()
//        cameraProvider.unbindAll()
//        val preview = androidx.camera.core.Preview.Builder().build()
//        preview.setSurfaceProvider(previewView.surfaceProvider)
//        // Bind the preview use case to the camera with the specified selector
//        cameraProvider.bindToLifecycle(
//            lifecycleOwner,
//            cameraSelector.value,
//            preview
//        )
//    }
//    AndroidView(
//        factory = { previewView },
//        modifier = Modifier.fillMaxSize(),
//    )
//}
//
//@Composable
//fun Parent(){
//    val context = LocalContext.current
//    var cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
//    val cameraProvider = cameraProviderFuture.get()
//
//    var showFrontView by remember { mutableStateOf(true) }
//
//    Row(){
//        Button(onClick = {   showFrontView = showFrontView != true }) {
//        }
//        if (showFrontView){
//            cameraProvider.shutdown()
//            SimpleCameraPreview()}
//        else {
//            cameraProvider.shutdown()
//            SimpleCameraPreviewFront()}
//    }
//
//}*/
