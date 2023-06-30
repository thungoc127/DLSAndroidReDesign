// package com.example.dlsandroidredesign.ui.mainScreen
//
// import android.content.ContentResolver
// import android.content.ContentValues
// import android.content.Context
// import android.graphics.Bitmap
// import android.graphics.BitmapFactory
// import android.graphics.Canvas
// import android.graphics.Paint
// import android.icu.text.SimpleDateFormat
// import android.net.Uri
// import android.os.Build
// import android.provider.MediaStore
// import android.util.Log
// import android.widget.Toast
// import androidx.camera.core.CameraSelector
// import androidx.camera.core.ImageCapture
// import androidx.camera.core.ImageCaptureException
// import androidx.camera.lifecycle.ProcessCameraProvider
// import androidx.camera.view.PreviewView
// import androidx.compose.foundation.Image
// import androidx.compose.foundation.background
// import androidx.compose.foundation.clickable
// import androidx.compose.foundation.layout.Box
// import androidx.compose.foundation.layout.Column
// import androidx.compose.foundation.layout.Spacer
// import androidx.compose.foundation.layout.fillMaxHeight
// import androidx.compose.foundation.layout.fillMaxSize
// import androidx.compose.foundation.layout.height
// import androidx.compose.foundation.layout.padding
// import androidx.compose.foundation.layout.size
// import androidx.compose.foundation.shape.RoundedCornerShape
// import androidx.compose.material.ExperimentalMaterialApi
// import androidx.compose.material.ModalBottomSheetValue
// import androidx.compose.material.rememberModalBottomSheetState
// import androidx.compose.material3.MaterialTheme
// import androidx.compose.runtime.Composable
// import androidx.compose.runtime.getValue
// import androidx.compose.runtime.mutableStateOf
// import androidx.compose.runtime.remember
// import androidx.compose.runtime.rememberCoroutineScope
// import androidx.compose.runtime.setValue
// import androidx.compose.ui.Alignment
// import androidx.compose.ui.Modifier
// import androidx.compose.ui.draw.clip
// import androidx.compose.ui.graphics.Color
// import androidx.compose.ui.layout.ContentScale
// import androidx.compose.ui.platform.LocalContext
// import androidx.compose.ui.platform.LocalLifecycleOwner
// import androidx.compose.ui.res.painterResource
// import androidx.compose.ui.unit.dp
// import androidx.core.content.ContextCompat
// import androidx.hilt.navigation.compose.hiltViewModel
// import coil.compose.rememberAsyncImagePainter
// import com.example.dlsandroidredesign.R
// import com.example.dlsandroidredesign.ui.setting.settingFragment
// import com.google.gson.JsonObject
// import eu.wewox.modalsheet.ExperimentalSheetApi
// import eu.wewox.modalsheet.ModalSheet
// import kotlinx.coroutines.launch
// import org.json.JSONObject
// import java.io.File
// import java.io.IOException
// import java.io.OutputStream
// import java.util.Calendar
// import java.util.Locale
//
// @OptIn(ExperimentalMaterialApi::class, ExperimentalSheetApi::class)
// @Composable
// fun Setting(viewModel:MainScreenViewModel= hiltViewModel()){
//    val coroutineScope= rememberCoroutineScope()
//    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden, skipHalfExpanded = true)
//    val context = LocalContext.current
//    //Camera
//    var cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
//    val lifecycleOwner = LocalLifecycleOwner.current
//    var cameraSelectorState by remember { mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA) }
//    var flashModeState by remember{ mutableStateOf(ImageCapture.FLASH_MODE_OFF) }
//    val imageCapture by remember{ mutableStateOf( ImageCapture.Builder().build()) }
//    val preview by remember { mutableStateOf(androidx.camera.core.Preview.Builder().build()) }
//    val previewView = remember { PreviewView(context) }
//
//    // Create time-stamped file name and MediaStore entry
//    val fileNameCapture = "Temp.jpg"
//    val contentValues = ContentValues().apply {
//        put(MediaStore.Images.Media.DISPLAY_NAME, fileNameCapture)
//        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
//            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/DLSPhotoCompose")
//        }
//
//    }
//    // Create output options
//    val outputOptions = ImageCapture.OutputFileOptions.Builder(
//        context.contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
//    ).build()
//
//    fun deleteImageByName(context: Context, imageName: String) {
//        val contentResolver: ContentResolver = context.contentResolver
//
//        val selection = "${MediaStore.Images.Media.DISPLAY_NAME} = ?"
//        val selectionArgs = arrayOf(imageName)
//
//        val deletedRows = contentResolver.delete(
//            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//            selection,
//            selectionArgs
//        )
//
//        if (deletedRows > 0) {
//        } else {
//
//        }
//    }
//
//    fun addTextOnImageAndSave(savedUriCapture: Uri?): Uri? {
//        val inputStream = context.contentResolver.openInputStream(savedUriCapture!!)
//        var bitmap = BitmapFactory.decodeStream(inputStream)
//        var bitmapConfig = bitmap.config
//        if (bitmapConfig == null) {
//            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888
//        }
//        bitmap = bitmap.copy(bitmapConfig, true)
//        val canvas = Canvas(bitmap)
//        val paint = Paint().apply {
//            color = android.graphics.Color.WHITE
//            textSize = 80f
//            textAlign = Paint.Align.LEFT
//        }
//        val paintRight = Paint().apply {
//            color = android.graphics.Color.WHITE
//            textSize = 80f
//            textAlign = Paint.Align.RIGHT
//
//        }
//
//        var xleft = 100f
//        var yleft = 120f
//        val locationInfoLeft = locationInfoLeft.split("\n")
//        locationInfoLeft.forEach { line ->
//            // Process each line
//            // Example: Print each line
//            canvas.drawText(line,xleft,yleft,paint)
//            yleft += 140f
//        }
//
//        var xright = bitmap.width.toFloat()
//        var yright = 120f
//        val locationInfoRight = locationInfoRight.split("\n")
//        locationInfoRight.forEach { line ->
//            // Process each line
//            // Example: Print each line
//            canvas.drawText(line,xright,yright,paintRight)
//            yright += 140f
//        }
//
//        val existingImageFile = File(context.filesDir, fileNameCapture)
//
//        if (existingImageFile.exists()) {
//            existingImageFile.delete()
//        }
//
//        val fileName = "IMG_${System.currentTimeMillis()}.jpg"
//        val mimeType = "image/jpeg"
//        viewModel.bmp.value= bitmap
//
// // Insert the image to the MediaStore
//        val values = ContentValues().apply {
//            put(MediaStore.Images.Media.TITLE, fileName)
//            put(MediaStore.Images.Media.MIME_TYPE, mimeType)
//            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
//                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/DLSPhotoCompose")
//            }
//        }
//
// // Get the content resolver
//        val resolver: ContentResolver = context.contentResolver
//
// // Insert the image and get its content URI
//        val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
//
// // Open an output stream to write the bitmap data
//        try {
//            imageUri?.let { uri ->
//                val outputStream: OutputStream? = resolver.openOutputStream(uri)
//
//                // Compress the bitmap to JPEG format and write it to the output stream
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
//
//                // Flush and close the output stream
//                outputStream?.flush()
//                outputStream?.close()
//
//                // Optionally, you can display a toast message to indicate the image has been saved
//                Toast.makeText(context, "Image saved to gallery", Toast.LENGTH_SHORT).show()
//                deleteImageByName(context,fileNameCapture)
//            }
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//        return imageUri
//    }
//
//
//    fun bindCameraUseCases() {
//        val cameraProvider = cameraProviderFuture.get()
//        cameraProvider.unbindAll()
//        // Bind the preview use case to the camera with the specified selector
//        val camera= cameraProvider.bindToLifecycle(
//            lifecycleOwner,
//            cameraSelectorState,
//            preview,
//            imageCapture
//        )
//        preview.setSurfaceProvider(previewView.surfaceProvider)
//    }
//    fun takePicture(){
//        imageCapture?.takePicture(
//            outputOptions,
//            ContextCompat.getMainExecutor(context),
//            object : ImageCapture.OnImageSavedCallback {
//                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
//                    // Image capture is successful
//                    // Access the captured image from outputFileResults
//                    val savedUriCapture = outputFileResults.savedUri
//                    /*
//                    //                    imageLocationInfoViewModel.insertImageLocationInfo(savedUriCapture!!, locationInfoObject = LocationObject())
//                    */
//
// //                    val picture= addTextOnImageAndSave(savedUriCapture)
// //                    // Handle further operations with the saved image URI
// //                    val multipartPic=ConvertUriToMultipart(picture)
//                    val c = Calendar.getInstance().time
//                    val df = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())
//                    val formattedDate = df.format(c)
//                    val obj = JSONObject()
//                    val wayPointObj = JsonObject()
//                    wayPointObj.addProperty("date", formattedDate)
//                    wayPointObj.addProperty("lat", 51.125357)
//                    wayPointObj.addProperty("lon", -114.165739)
//                    wayPointObj.addProperty("groupid", "2e09b7b0-45d5-400f-89bd-2bc696c14c4f")
//                    obj.put("waypoint", wayPointObj)
//                    var waypoinID:String? = ""
//
//                }
//                override fun onError(exception: ImageCaptureException) {
//                    // Image capture failed
//                    Log.e("CameraX", "Image capture failed", exception)
//                }
//            }
//
//        )
//
//
//    }
//
//
//    fun switchCamera(){
//        cameraSelectorState = if (cameraSelectorState == CameraSelector.DEFAULT_BACK_CAMERA) {
//            CameraSelector.DEFAULT_FRONT_CAMERA
//        } else {
//            CameraSelector.DEFAULT_BACK_CAMERA
//        }
//        bindCameraUseCases()
//    }
//
//    fun flashSwitch(){
//        flashModeState = if (flashModeState== ImageCapture.FLASH_MODE_OFF){
//            ImageCapture.FLASH_MODE_ON
//        }else{
//            ImageCapture.FLASH_MODE_OFF
//        }
//        // Bind the preview use case to the camera with the specified selector
//        imageCapture.flashMode=flashModeState
//    }
//
//    fun zoomCamera(){
//        val cameraProvider = cameraProviderFuture.get()
//        // Bind the preview use case to the camera with the specified selector
//        val camera= cameraProvider.bindToLifecycle(
//            lifecycleOwner,
//            cameraSelectorState,
//            preview,
//            imageCapture
//        )
//
//        val cameraControl = camera.cameraControl
//        cameraControl.setLinearZoom(viewModel.zoomRatio.value)
//    }
//
//
//    Setting(
//        onSettingPressed = {coroutineScope.launch { sheetState.show() }},
//        onCameraCapturePressed = { takePicture() },
//        onSwitchCameraPress = { switchCamera() },
//        onFlashPressed = { flashSwitch() },
//        bmp = viewModel.bmp.value
//    )
//    ModalSheet(
//        sheetState = sheetState,
//        onSystemBack = {coroutineScope.launch { sheetState.hide() }},
//        content={
//                settingFragment()
//        },
//        shape= MaterialTheme.shapes.extraLarge
//
//    )
//
// }
//
//
//
//
// @Composable
// fun Setting(onSettingPressed: () -> Unit, onCameraCapturePressed: () -> Unit, onSwitchCameraPress:()->Unit, onFlashPressed:()->Unit,bmp: Bitmap?) {
//    val context = LocalContext.current
//    var imageUri by remember { mutableStateOf<Uri?>(null) }
//    var painter = rememberAsyncImagePainter(model = bmp)
//
//    /*    val inputStream = context.contentResolver.openInputStream(bmp!!)
//        val BitmapImage =  BitmapFactory.decodeStream(inputStream)
//        val mutableBitmap = BitmapImage.copy(Bitmap.Config.ARGB_8888, true)
//        val canvas = Canvas(mutableBitmap)
//        canvas.drawText("Hello",100f,100f, Paint())
//        Image(bitmap = mutableBitmap.asImageBitmap(), contentDescription = null)*/
//
//    Column(
//        modifier = Modifier
//            .background(Color.Black)
//            .padding(0.dp, 20.dp, 0.dp, 15.dp)
//            .fillMaxHeight(),
//
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        //Setting
//        Box(
//            modifier = Modifier
//                .size(50.dp, 50.dp)
//                .background(
//                    shape = RoundedCornerShape(200.dp),
//                    color = Color.Gray
//
//                )
//                .clickable { onSettingPressed() },
//            contentAlignment = Alignment.Center
//        ) {
//            Image(
//                painter = painterResource(R.drawable.ic_gearshape_fill_single),
//                modifier = Modifier.size(20.dp, 20.dp),
//                contentDescription = null
//            )
//        }
//
//        //Spacer
//        Spacer(modifier = Modifier.height(8.dp))
//        //Flash (ic_bolt_slash_fill_single.xml)
//        Box(
//            modifier = Modifier
//                .size(50.dp, 50.dp)
//                .background(
//                    shape = RoundedCornerShape(200.dp),
//                    color = Color.Gray
//                )
//                .clickable { onFlashPressed() },
//            contentAlignment = Alignment.Center
//
//        ) {
//            Image(
//                painter = painterResource(R.drawable.ic_bolt_single),
//                modifier = Modifier.size(20.dp, 20.dp),
//                contentDescription = null
//            )
//        }
//        //Spacer
//        Spacer(modifier = Modifier.height(8.dp))
//        //Camera Capture
//        Box(
//            modifier = Modifier
//                .size(70.dp, 70.dp)
//                .background(
//                    shape = RoundedCornerShape(18.dp),
//                    color = Color.White
//                )
//                .clickable {
//                    onCameraCapturePressed()
//                },
//            contentAlignment = Alignment.Center,
//
//            ) {
//            Image(
//                painter = painterResource(R.mipmap.ic_capture),
//                modifier = Modifier.size(70.dp, 70.dp),
//                contentDescription = null
//            )
//
//        }
//        //Spacer
//        Spacer(modifier = Modifier.height(8.dp))
//        //rotate camera
//        Box(
//            modifier = Modifier
//                .size(50.dp, 50.dp)
//                .background(
//                    shape = RoundedCornerShape(18.dp),
//                    color = Color.Gray
//                )
//                .clickable { onSwitchCameraPress() },
//            contentAlignment = Alignment.Center,
//
//            ) {
//            Image(
//                painter = painterResource(R.drawable.ic_camera_rotate_fill_single),
//                modifier = Modifier.size(20.dp, 20.dp),
//                contentDescription = null
//            )
//        }
//        //Spacer
//        Spacer(modifier = Modifier.height(8.dp))
//        //camera image
//        Box(
//            modifier = Modifier
//                .size(70.dp, 70.dp)
//                .background(
//                    shape = RoundedCornerShape(18.dp),
//                    color = Color.Gray
//                ),
//            contentAlignment = Alignment.Center,
//
//            ) {
//
//            Image(
//                painter = painter,
//                modifier = Modifier
//                    .fillMaxSize()
//                    .clip(shape = RoundedCornerShape(18.dp)),contentScale = ContentScale.FillBounds
//                ,
//                contentDescription = null
//            )
//
//        }
//
//    }
//
//
//
// }
