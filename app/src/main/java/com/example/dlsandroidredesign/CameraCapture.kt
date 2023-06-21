///*
//package com.example.dlsandroidredesign
//
//import android.view.ViewGroup
//import androidx.camera.core.Preview
//import androidx.camera.core.UseCase
//import androidx.camera.view.PreviewView
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.viewinterop.AndroidView
//import androidx.compose.foundation.BorderStroke
//import androidx.compose.foundation.interaction.MutableInteractionSource
//import androidx.compose.foundation.interaction.collectIsPressedAsState
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.OutlinedButton
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.remember
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import android.util.Log
//import androidx.camera.core.CameraSelector
//import androidx.camera.core.ImageCapture
//import androidx.camera.core.ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY
//import androidx.compose.foundation.layout.Box
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.platform.LocalLifecycleOwner
//import com.google.accompanist.permissions.ExperimentalPermissionsApi
//import java.io.File
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.launch
//
//
//
//@Composable
//fun CameraPreview(
//    modifier: Modifier = Modifier,
//    scaleType: PreviewView.ScaleType = PreviewView.ScaleType.FILL_CENTER,
//    onUseCase: (UseCase) -> Unit = { }
//) {
//    AndroidView(
//        modifier = modifier,
//        factory = { context ->
//            val previewView = PreviewView(context).
//            apply {
//                this.scaleType = scaleType
//                layoutParams = ViewGroup.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.MATCH_PARENT
//                )
//            }
//            onUseCase(
//                androidx.camera.core.Preview.Builder()
//                    .build()
//                    .also {
//                        it.setSurfaceProvider(previewView.surfaceProvider)
//                    }
//            )
//            previewView
//        }
//    )
//}
//
//
//@Composable
//fun CapturePictureButton(
//    modifier: Modifier = Modifier,
//    onClick: () -> Unit = { },
//) {
//    val interactionSource = remember { MutableInteractionSource() }
//    val isPressed by interactionSource.collectIsPressedAsState()
//    val color = if (isPressed) Color.Blue else Color.Black
//    val contentPadding = PaddingValues(if (isPressed) 8.dp else 12.dp)
//    OutlinedButton(
//        modifier = modifier,
//        shape = CircleShape,
//        border = BorderStroke(2.dp, Color.Black),
//        contentPadding = contentPadding,
//        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black),
//        onClick = {},
//        enabled = false
//    ) {
//        Button(
//            modifier = Modifier
//                .fillMaxSize(),
//            shape = CircleShape,
//            interactionSource = interactionSource,
//            onClick = onClick
//        ) {
//            // No content
//        }
//    }
//}
//
//
//@ExperimentalPermissionsApi
//@ExperimentalCoroutinesApi
//@Composable
//fun CameraCapture(
//    modifier: Modifier = Modifier,
//    cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
//    onImageFile: (File) -> Unit = { }
//) {
//    val context = LocalContext.current
//        Box(modifier = modifier) {
//            val lifecycleOwner = LocalLifecycleOwner.current
//            val coroutineScope = rememberCoroutineScope()
//            var previewUseCase by remember { mutableStateOf<UseCase>(androidx.camera.core.Preview.Builder().build()) }
//            val imageCaptureUseCase by remember {
//                mutableStateOf(
//                    ImageCapture.Builder()
//                        .setCaptureMode(CAPTURE_MODE_MAXIMIZE_QUALITY)
//                        .build()
//                )
//            }
//            Box {
//                CameraPreview(
//                    modifier = Modifier.fillMaxSize(),
//                    onUseCase = {
//                        previewUseCase = it
//                    }
//                )
//                CapturePictureButton(
//                    modifier = Modifier
//                        .size(100.dp)
//                        .padding(16.dp)
//                        .align(Alignment.BottomCenter),
//                    onClick = {
//                        coroutineScope.launch {
//                            imageCaptureUseCase.takePicture().let {
//                                onImageFile(it)
//                            }
//                        }
//                    }
//                )
//            }
//            LaunchedEffect(previewUseCase) {
//                val cameraProvider = context.getCameraProvider()
//                try {
//                    // Must unbind the use-cases before rebinding them.
//                    cameraProvider.unbindAll()
//                    cameraProvider.bindToLifecycle(
//                        lifecycleOwner, cameraSelector, previewUseCase, imageCaptureUseCase
//                    )
//                } catch (ex: Exception) {
//                    Log.e("CameraCapture", "Failed to bind camera use cases", ex)
//                }
//            }
//        }
//    }
//}
//*/
