@file:Suppress("FunctionName")
@file:OptIn(ExperimentalMaterialApi::class)

package com.example.dlsandroidredesign

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.FLASH_MODE_OFF
import androidx.camera.core.ImageCapture.FLASH_MODE_ON
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.example.dlsandroidredesign.data.remote.DLSService
import com.example.dlsandroidredesign.data.local.PreferencesDataStore
import com.google.gson.JsonObject
import com.smarttoolfactory.screenshot.rememberScreenshotState
import eu.wewox.modalsheet.ExperimentalSheetApi
import eu.wewox.modalsheet.ModalSheet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.OutputStream
import java.util.Calendar
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalSheetApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FullPreviewScreen(viewModel: ImageLocationInfoViewModel) {

    /////////////////SETUP
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    //Camera
    var cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val lifecycleOwner = LocalLifecycleOwner.current
    var cameraSelectorState by remember { mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA) }
    var flashModeState by remember{ mutableStateOf(FLASH_MODE_OFF) }
    val imageCapture by remember{ mutableStateOf( ImageCapture.Builder().build()) }
    val preview by remember { mutableStateOf(androidx.camera.core.Preview.Builder().build()) }

    //BottomFragment
    var galleryModalSheetVisible by remember { mutableStateOf(false) }
    val preferenceDataStore = PreferencesDataStore(LocalContext.current)
    var settingModalSheetVisible by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden, skipHalfExpanded = true)
    var isSettingFragmentShow by remember{ mutableStateOf(false) }
    var isLoginFragmentShow by remember{ mutableStateOf(false) }
    var isWaypointgroupsFragmentShow by remember{ mutableStateOf(false) }


    val previewView = remember { PreviewView(context) }
    var savedUri by remember { mutableStateOf<Uri?>(null) }
//ScreenShotBox
    val screenshotState = rememberScreenshotState()
    var bmp by remember { mutableStateOf<Bitmap?>(null) }
    var locationInfoLeft = preferenceDataStore.getLocationInfoLeft.collectAsState(initial = "0.0").value
    var locationInfoRight = preferenceDataStore.getLocationInfoRight.collectAsState(initial = "0.0").value

    val settingCheckbox = preferenceDataStore.getSettingCheckbox().collectAsState(initial = hashSetOf<String>("LatLon","Elevation","GridLocation","Distance","Heading","Address","Date","Utm","CustomText")).value



    //Zoom value
    var zoomRatio by remember {
        mutableStateOf<Float>(0.0f)
    }



    // Create time-stamped file name and MediaStore entry
    val fileNameCapture = "Temp.jpg"
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, fileNameCapture)
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/DLSPhotoCompose")
        }

    }

    // Create output options
    val outputOptions = ImageCapture.OutputFileOptions.Builder(
        context.contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
    ).build()

    fun bindCameraUseCases() {
        val cameraProvider = cameraProviderFuture.get()
        cameraProvider.unbindAll()
        // Bind the preview use case to the camera with the specified selector
       val camera= cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelectorState,
            preview,
            imageCapture
        )
        preview.setSurfaceProvider(previewView.surfaceProvider)
    }

    fun deleteImageByName(context: Context, imageName: String) {
        val contentResolver: ContentResolver = context.contentResolver

        val selection = "${MediaStore.Images.Media.DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(imageName)

        val deletedRows = contentResolver.delete(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            selection,
            selectionArgs
        )

        if (deletedRows > 0) {
                } else {

        }
    }

    fun ConvertUriToMultipart(ImageUri:Uri?): MultipartBody.Part {

        val test = context.contentResolver.openInputStream(ImageUri!!)
        val byteBuff = ByteArrayOutputStream()

        val buffSize = 1024
        val buff = ByteArray(buffSize)

        var len = 0
        while (test!!.read(buff).also { len = it } != -1) {
            byteBuff.write(buff, 0, len)
        }

        val requestFileFront: RequestBody = byteBuff.toByteArray().toRequestBody("image/png".toMediaTypeOrNull())
        val picture = MultipartBody.Part.createFormData(
            "photodata",
            "picture.png",
            requestFileFront
        )
        Toast.makeText(context, "Convert Success ", Toast.LENGTH_SHORT).show()
        return picture
    }


    fun addTextOnImageAndSave(savedUriCapture: Uri?): Uri? {
        val inputStream = context.contentResolver.openInputStream(savedUriCapture!!)
        var bitmap = BitmapFactory.decodeStream(inputStream)
        var bitmapConfig = bitmap.config
        if (bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888
        }
        bitmap = bitmap.copy(bitmapConfig, true)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 80f
            textAlign = Paint.Align.LEFT
        }
        val paintRight = Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 80f
            textAlign = Paint.Align.RIGHT

        }



        var xleft = 100f
        var yleft = 120f
        val locationInfoLeft = locationInfoLeft.split("\n")
        locationInfoLeft.forEach { line ->
            // Process each line
            // Example: Print each line
            canvas.drawText(line,xleft,yleft,paint)
            yleft += 140f
        }

        var xright = bitmap.width.toFloat()
        var yright = 120f
        val locationInfoRight = locationInfoRight.split("\n")
        locationInfoRight.forEach { line ->
            // Process each line
            // Example: Print each line
            canvas.drawText(line,xright,yright,paintRight)
            yright += 140f
        }

        val existingImageFile = File(context.filesDir, fileNameCapture)

        if (existingImageFile.exists()) {
            existingImageFile.delete()
        }

        val fileName = "IMG_${System.currentTimeMillis()}.jpg"
        val mimeType = "image/jpeg"
        bmp=bitmap

// Insert the image to the MediaStore
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, mimeType)
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/DLSPhotoCompose")
            }
        }

// Get the content resolver
        val resolver: ContentResolver = context.contentResolver

// Insert the image and get its content URI
        val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

// Open an output stream to write the bitmap data
        try {
            imageUri?.let { uri ->
                val outputStream: OutputStream? = resolver.openOutputStream(uri)

                // Compress the bitmap to JPEG format and write it to the output stream
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

                // Flush and close the output stream
                outputStream?.flush()
                outputStream?.close()

                // Optionally, you can display a toast message to indicate the image has been saved
                Toast.makeText(context, "Image saved to gallery", Toast.LENGTH_SHORT).show()
                deleteImageByName(context,fileNameCapture)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return imageUri
    }

    fun takePicture(){
        imageCapture?.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    // Image capture is successful
                    // Access the captured image from outputFileResults
                    val savedUriCapture = outputFileResults.savedUri
/*
//                    imageLocationInfoViewModel.insertImageLocationInfo(savedUriCapture!!, locationInfoObject = LocationObject())
*/

                    val picture= addTextOnImageAndSave(savedUriCapture)
                    // Handle further operations with the saved image URI
                    val multipartPic=ConvertUriToMultipart(picture)
                    val c = Calendar.getInstance().time
                    val df = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())
                    val formattedDate = df.format(c)
                    val obj = JSONObject()
                    val wayPointObj = JsonObject()
                    wayPointObj.addProperty("date", formattedDate)
                    wayPointObj.addProperty("lat", 51.125357)
                    wayPointObj.addProperty("lon", -114.165739)
                    wayPointObj.addProperty("groupid", "2e09b7b0-45d5-400f-89bd-2bc696c14c4f")
                    obj.put("waypoint", wayPointObj)
                    var waypoinID:String? = ""

                    GlobalScope.launch(Dispatchers.IO)  {
                        val waypointDeferred = async(Dispatchers.IO) { getWayPoint(apiKey = "1f593949-c520-4747-a162-1c37229a9f54", bean = wayPointObj) }
                        val waypointID = waypointDeferred.await()

                        launch(Dispatchers.Main) { uploadPhoto(apiKey = "1f593949-c520-4747-a162-1c37229a9f54", waypointId = waypointID, multipartPic) }
                    }

                }
                override fun onError(exception: ImageCaptureException) {
                    // Image capture failed
                    Log.e("CameraX", "Image capture failed", exception)
                }
            }

        )


    }


    fun switchCamera(){
        cameraSelectorState = if (cameraSelectorState == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
        bindCameraUseCases()
    }

    fun flashSwitch(){
        flashModeState = if (flashModeState==FLASH_MODE_OFF){FLASH_MODE_ON}else{FLASH_MODE_OFF}
        // Bind the preview use case to the camera with the specified selector
        imageCapture.flashMode=flashModeState
    }

    fun zoomCamera(){
        val cameraProvider = cameraProviderFuture.get()
        // Bind the preview use case to the camera with the specified selector
        val camera= cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelectorState,
            preview,
            imageCapture
        )

        val cameraControl = camera.cameraControl
        cameraControl.setLinearZoom(zoomRatio)
    }





//    fun UploadPicture(apiKey:String?,bean: JsonObject?){
//        val retrofitInstance = RetoInstance().getInstance()
//        val apiService = retrofitInstance.create(ApiInterfaceService::class.java)
//        val respond  = apiService.getWayPointID(apiKey,bean)
//        val wayPointObj: JsonObject = JsonObject()
//        val obj:JsonObject = JsonObject()
//
//        wayPointObj.addProperty("date", "John")
//        wayPointObj.addProperty("lat", "John")
//        wayPointObj.addProperty("lon", "John")
//        wayPointObj.addProperty("groupid", "John")
//        obj.add("waypoint",wayPointObj)
//        if(respond.isSuccessful){
//
//// Get the InputStream from the selectedUri
//            val contentResolver: ContentResolver = context.contentResolver
//            val inputStream = contentResolver.openInputStream()
//
//// Read the InputStream and convert it to a byte array
//            val byteBuff = ByteArrayOutputStream()
//            val buffSize = 1024
//            val buff = ByteArray(buffSize)
//
//            var len: Int
//            while (inputStream?.read(buff).also { len = it ?: -1 } != -1) {
//                byteBuff.write(buff, 0, len)
//            }
//
//// Create the request body using the byte array
//            val requestFileFront = byteBuff.toByteArray().toRequestBody("image/png".toMediaTypeOrNull())
//
//// Create the MultipartBody.Part using the request body
//            val picture = MultipartBody.Part.createFormData("photodata", "picture.png", requestFileFront)
//
//
//
//            val wayPointId = respond.body()!!.waypointid
//
//            val respond= apiService.uploadPhoto(apiKey,wayPointId,picture)
//            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
//            if(respond.isSuccessful) {
//                Toast.makeText(context, "Photo Uploaded Successfully!", Toast.LENGTH_SHORT).show()
//
//            }
//            }
//
//        }


    Row(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .weight(1f)
                .background(color = Color.Black), contentAlignment = Alignment.Center
        ) {
            ZoomView(onGalleryButtonPressed={galleryModalSheetVisible=true},
                onZoomOnePressed = {
                    zoomRatio=0.0f
                    zoomCamera()
                                   },
                onZoomTwoPressed = {
                    zoomRatio=0.1f
                    zoomCamera()
                },
                onZoomThreePressed = {
                    zoomRatio=0.2f
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
                        modifier = Modifier.fillMaxSize(),
                    )

                    locationView(viewModel)

                }
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .background(color = Color.Black)
                .fillMaxWidth(), contentAlignment = Alignment.Center
        ) {

            Setting(
                onSettingPressed = {isSettingFragmentShow = true
                                   coroutineScope.launch{sheetState.show()}
                                   },
                onCameraCapturePressed = {
                    takePicture()
                },
                onSwitchCameraPress = { switchCamera()},
                onFlashPressed = {flashSwitch()},
                bmp = bmp,
            )

        }
    }
    ModalSheet(visible = galleryModalSheetVisible, onVisibleChange ={galleryModalSheetVisible=it} ) {
        GalleryScreen()
    }
    ModalBottomSheetSetting(sheetState, isSettingFragmentShow = isSettingFragmentShow, viewModel )


}


@Composable
fun ZoomView(onGalleryButtonPressed:()->Unit,onZoomOnePressed:()->Unit, onZoomTwoPressed:()->Unit,onZoomThreePressed:()->Unit) {
    Column(
        modifier = Modifier
            .background(Color.Black)
            .padding(0.dp, 20.dp, 0.dp, 30.dp)
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


        Column(modifier = Modifier.weight(4f), verticalArrangement = Arrangement.Center) {
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
                contentAlignment = Alignment.Center

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



@Composable
fun Setting(onSettingPressed: () -> Unit, onCameraCapturePressed: () -> Unit, onSwitchCameraPress:()->Unit, onFlashPressed:()->Unit,bmp:Bitmap?) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var painter = rememberAsyncImagePainter(model = bmp)

/*    val inputStream = context.contentResolver.openInputStream(bmp!!)
    val BitmapImage =  BitmapFactory.decodeStream(inputStream)
    val mutableBitmap = BitmapImage.copy(Bitmap.Config.ARGB_8888, true)
    val canvas = Canvas(mutableBitmap)
    canvas.drawText("Hello",100f,100f, Paint())
    Image(bitmap = mutableBitmap.asImageBitmap(), contentDescription = null)*/

    Column(
        modifier = Modifier
            .background(Color.Black)
            .padding(0.dp, 20.dp, 0.dp, 15.dp)
            .fillMaxHeight(),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //Setting
        Box(
            modifier = Modifier
                .size(50.dp, 50.dp)
                .background(
                    shape = RoundedCornerShape(200.dp),
                    color = Color.Gray

                )
                .clickable { onSettingPressed() },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.ic_gearshape_fill_single),
                modifier = Modifier.size(20.dp, 20.dp),
                contentDescription = null
            )
        }

        //Spacer
        Spacer(modifier = Modifier.height(8.dp))
        //Flash (ic_bolt_slash_fill_single.xml)
        Box(
            modifier = Modifier
                .size(50.dp, 50.dp)
                .background(
                    shape = RoundedCornerShape(200.dp),
                    color = Color.Gray
                )
                .clickable { onFlashPressed() },
            contentAlignment = Alignment.Center

        ) {
            Image(
                painter = painterResource(R.drawable.ic_bolt_single),
                modifier = Modifier.size(20.dp, 20.dp),
                contentDescription = null
            )
        }
        //Spacer
        Spacer(modifier = Modifier.height(8.dp))
        //Camera Capture
        Box(
            modifier = Modifier
                .size(70.dp, 70.dp)
                .background(
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White
                )
                .clickable {
                    onCameraCapturePressed()
                },
            contentAlignment = Alignment.Center,

            ) {
            Image(
                painter = painterResource(R.mipmap.ic_capture),
                modifier = Modifier.size(70.dp, 70.dp),
                contentDescription = null
            )

        }
        //Spacer
        Spacer(modifier = Modifier.height(8.dp))
        //rotate camera
        Box(
            modifier = Modifier
                .size(50.dp, 50.dp)
                .background(
                    shape = RoundedCornerShape(18.dp),
                    color = Color.Gray
                )
                .clickable { onSwitchCameraPress() },
            contentAlignment = Alignment.Center,

            ) {
            Image(
                painter = painterResource(R.drawable.ic_camera_rotate_fill_single),
                modifier = Modifier.size(20.dp, 20.dp),
                contentDescription = null
            )
        }
        //Spacer
        Spacer(modifier = Modifier.height(8.dp))
        //camera image
        Box(
            modifier = Modifier
                .size(70.dp, 70.dp)
                .background(
                    shape = RoundedCornerShape(18.dp),
                    color = Color.Gray
                ),
            contentAlignment = Alignment.Center,

            ) {

            Image(
                painter = painter,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(shape = RoundedCornerShape(18.dp)),contentScale = ContentScale.FillBounds
                    ,
                contentDescription = null
            )

        }

    }
}

fun getMostRecentImage(context: Context): Uri? {
    val projection = arrayOf(MediaStore.Images.Media._ID)
    val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC LIMIT 1"

    val query = context.contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection,
        null,
        null,
        sortOrder
    )

    query?.use { cursor ->
        if (cursor.moveToNext()) {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            return ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                cursor.getLong(columnIndex)
            )
        }
    }

    return null
}

