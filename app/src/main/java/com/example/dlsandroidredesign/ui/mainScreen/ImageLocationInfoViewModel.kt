package com.example.dlsandroidredesign.ui.mainScreen

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.compose.runtime.mutableStateOf
import androidx.exifinterface.media.ExifInterface
import androidx.exifinterface.media.ExifInterface.TAG_GPS_LATITUDE
import androidx.exifinterface.media.ExifInterface.TAG_GPS_LATITUDE_REF
import androidx.exifinterface.media.ExifInterface.TAG_GPS_LONGITUDE
import androidx.exifinterface.media.ExifInterface.TAG_GPS_LONGITUDE_REF
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcgismaps.LoadStatus
import com.arcgismaps.data.QueryParameters
import com.arcgismaps.data.SpatialRelationship
import com.arcgismaps.geometry.CoordinateFormatter
import com.arcgismaps.geometry.Envelope
import com.arcgismaps.geometry.SpatialReference
import com.arcgismaps.geometry.UtmConversionMode
import com.arcgismaps.mapping.MobileMapPackage
import com.arcgismaps.mapping.layers.FeatureLayer
import com.example.dlsandroidredesign.data.CheckBoxKey
import com.example.dlsandroidredesign.domain.entity.LocationObject
import com.example.dlsandroidredesign.domain.entity.PhotoSize
import com.example.dlsandroidredesign.domain.usecase.GetAutoUploadStatus
import com.example.dlsandroidredesign.domain.usecase.GetCheckBoxUseCase
import com.example.dlsandroidredesign.domain.usecase.GetCurrentUser
import com.example.dlsandroidredesign.domain.usecase.GetCusText
import com.example.dlsandroidredesign.domain.usecase.GetPhotoSize
import com.example.dlsandroidredesign.domain.usecase.GetUploadSize
import com.example.dlsandroidredesign.domain.usecase.GetWayPointId
import com.example.dlsandroidredesign.domain.usecase.InsertImageLocationInfo
import com.example.dlsandroidredesign.domain.usecase.UploadPicture
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.abs

// TODO: If possible, avoid using context in viewModel. Try to move things that need context into view or repository.
// TODO: Lots of logic in this viewModel, possibly move logics in to use cases and repository
// TODO: These will be a huge change. but after changed, will be a nice clean view model.
@Suppress("DEPRECATION")
@SuppressLint("MissingPermission")
@HiltViewModel
class ImageLocationInfoViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getCheckBoxUsecase: GetCheckBoxUseCase,
    private val getWayPointId: GetWayPointId,
    private val getCurrentUser: GetCurrentUser,
    private val uploadPicture: UploadPicture,
    private val autoUploadStatus: GetAutoUploadStatus,
    private val insertImagelocationinfo: InsertImageLocationInfo,
    private val uploadSize: GetUploadSize,
    private val photoSize: GetPhotoSize,
    private val getCusText: GetCusText

) : ViewModel() {
    private val fileNameCapture = "temp.jpeg"
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, fileNameCapture)
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/DLSPhotoCompose")
        }
    }
    var bmp = mutableStateOf<Bitmap?>(null)
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private val locationRequest: LocationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()
    private val packagePath = File(context.getExternalFilesDir(null), "sections.mmpk").path
    private val mobileMapPackage = MobileMapPackage(packagePath)
    private var sectionLayer: FeatureLayer? = null
    private val dec = DecimalFormat("#.000").apply { roundingMode = RoundingMode.CEILING }
    var locationInfoLeft = MutableStateFlow("")
    var locationInfoRight = MutableStateFlow("")
    private val _locationObject = MutableStateFlow(LocationObject())
    private val contentResolver: ContentResolver = context.contentResolver
    private var _wayPointId = MutableStateFlow<String?>(null)
    private val wayPointId = _wayPointId
    val locationObjectState: StateFlow<LocationObject> = _locationObject
    val currentUser = getCurrentUser.invoke()
    val uriSet: MutableStateFlow<MutableSet<Uri>> = MutableStateFlow(emptySet<Uri>().toMutableSet())
    init {
        viewModelScope.launch {
            mobileMapPackage.load()
            mobileMapPackage.loadStatus.collect {
                Log.d("getLocationProcess: ", "${LoadStatus.Loaded}")
                when (it) {
                    LoadStatus.Loaded -> {
                        sectionLayer = mobileMapPackage.maps
                            .getOrNull(0)
                            ?.operationalLayers
                            ?.getOrNull(0) as? FeatureLayer
                        Log.d("getLocationProcess: ", "$sectionLayer")
                    }

                    else -> {
                        Log.d("getLocationProcess: ", "getTodidntloadmap")
                    }
                }
                Log.d("getLocationProcess: ", it.toString())
            }
        }
    }

    fun startFetchingLocation() {
        viewModelScope.launch {
            fetchLocationUpdates().collect { location ->
                Log.d("getLocationProcess: ", "${location?.latitude}")

                // Location retrieved successfully
                if (location != null) {
                    launch {
                        val newLocationObject = withContext(Dispatchers.IO) {
                            Log.d("getLocationProcess: ", "${location.latitude}")
                            getCompleteAddress(location)
                        }
                        _locationObject.value = newLocationObject
                        Log.d("getLocationProcess: ", "$newLocationObject")
                    }
                } else {
                    Log.d("getLocationProcess: ", "LocationNull")
                }
            }
        }
    }

    fun setCusTextLocationObject(newvalue: String) {
        _locationObject.value.custText = newvalue
    }

    fun checkUriContain(imageUri: Uri): Boolean {
        return uriSet.value.contains(imageUri)
    }
    fun setUriSet(imageUri: Uri) {
        if (!uriSet.value.contains(imageUri)) { uriSet.value.add(imageUri) } else { uriSet.value.remove(imageUri) }
    }
    fun getLocationFromPicture(imageUri: Uri): LocationObject {
        val inputStream = contentResolver.openInputStream(imageUri)
        val exif = ExifInterface(inputStream!!)
        val latitudes = exif.getAttribute(TAG_GPS_LATITUDE)?.split(',') ?: return LocationObject()
        val latitudesRef = exif.getAttribute(TAG_GPS_LATITUDE_REF)
        val longitudes = exif.getAttribute(TAG_GPS_LONGITUDE)?.split(',') ?: return LocationObject()
        val longitudesRef = exif.getAttribute(TAG_GPS_LONGITUDE_REF)
        Log.d("getLocation", "latitudes$latitudes")
        Log.d("getLocation", "longitudes$longitudes")

        val latitude = convertToDegree(latitudes)
        val longitude = convertToDegree(longitudes)
        val locationObject = LocationObject()
        if (latitudesRef == "N") { locationObject.lat = String.format("%.6f", latitude) } else { locationObject.lat = String.format("%.6f", -latitude) }
        if (longitudesRef == "E") { locationObject.lon = String.format("%.6f", longitude) } else { locationObject.lon = String.format("%.6f", -longitude) }
        Log.d("getLocation", "$locationObject")
        return locationObject
    }
    private fun addLocationToImageUri(imageUri: Uri, locationObject: LocationObject) {
        val latitude = locationObject.lat.toDouble()
        val longitude = locationObject.lon.toDouble()

        val exif = ExifInterface(context.contentResolver.openFileDescriptor(imageUri, "rw")?.fileDescriptor!!)
        exif.setAttribute(
            TAG_GPS_LATITUDE,
            convert(latitude)
        )
        exif.setAttribute(
            TAG_GPS_LATITUDE_REF,
            if (latitude > 0) "N" else "S"
        )
        exif.setAttribute(
            TAG_GPS_LONGITUDE,
            convert(longitude)
        )
        exif.setAttribute(
            TAG_GPS_LONGITUDE_REF,
            if (longitude > 0) "E" else "W"
        )
        exif.saveAttributes()

        val getLocationObjectfromPic = getLocationFromPicture(imageUri)
        Log.d("getLocation", "urifromAddexif: $imageUri")
        Log.d("getLocation", "finishSetExif ${exif.getAttribute(TAG_GPS_LONGITUDE)}")
        Log.d("getLocation", "finishSetExif getLocationObjectfromPic $getLocationObjectfromPic")
    }

    private fun convert(coordinate: Double): String {
        var coord = coordinate
        coord = abs(coord)
        val degrees = coord.toInt()
        coord = (coord - degrees) * 60
        val minutes = coord.toInt()
        coord = (coord - minutes) * 60
        val seconds = (coord * 1000).toInt()
        return "$degrees/1,$minutes/1,$seconds/1000"
    }

    private fun convertToDegree(coordinate: List<String>): Double {
        val degrees = coordinate[0].split("/")
        val minutes = coordinate[1].split("/")
        val seconds = coordinate[2].split("/")

        val degreesValue = degrees[0].toDouble() / degrees[1].toDouble()
        val minutesValue = minutes[0].toDouble() / minutes[1].toDouble() / 60
        val secondsValue = seconds[0].toDouble() / seconds[1].toDouble() / 3600

        val decimalDegrees = degreesValue + minutesValue + secondsValue
        return decimalDegrees
    }

    fun insertImagelocationinfo(imageUri: Uri, locationObject: LocationObject) {
        viewModelScope.launch { insertImagelocationinfo.invoke(imageUri, locationObject) }
    }

    private suspend fun createText(locationObject: LocationObject) {
        val checkBoxList = getCheckBoxUsecase.invoke().first()
        Log.d("createText", "locationObject: $locationObject")
        val latlon: String = if (checkBoxList.latLon) { "Lat/lon\n${locationObject.lat}\n${locationObject.lon}\n" } else { "" }
        val elevationNew: String = if (checkBoxList.elevation) { "Eleve: ${CheckBoxKey.Elevation} m" } else { "" }
        val gridLocationNew: String = if (checkBoxList.gridLocation) { "${locationObject.gridLocation}\n" } else { "" }
        val distanceNew: String = if (checkBoxList.distance) { "${locationObject.distance}\n" } else { "" }
        val utmCoordinateNew: String = if (checkBoxList.utmCoordinate) { "${locationObject.utmCoordinate}\n" } else { "" }
        Log.d("createText", "info: ${"$latlon$elevationNew$gridLocationNew$distanceNew$utmCoordinateNew"}")
        locationInfoLeft.value = "$latlon$elevationNew$gridLocationNew$distanceNew$utmCoordinateNew"
        Log.d("createText", "locationInfoLeft: ${locationInfoLeft.value}")

        val bearingNew: String = if (checkBoxList.bearing) { locationObject.bearing + "\n" } else { "" }
        val addressNew: String = if (checkBoxList.address) { locationObject.address + "\n" } else { "" }
        val dateTimeFormattedNew: String = if (checkBoxList.date) { locationObject.date } else { "" }
        locationInfoRight.value = "$bearingNew$addressNew$dateTimeFormattedNew"
        Log.d("createText", "locationInfoLeft second: ${locationInfoRight.value}")
    }
    private fun deleteImageByName(imageName: String) {
        val selection = "${MediaStore.Images.Media.DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(imageName)

        val deletedRows = contentResolver.delete(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            selection,
            selectionArgs
        )
        if (deletedRows > 0) {
            Log.d("deleteRows", "Morethan1picneedtodelel")
        } else {
        }
    }
    private suspend fun addTextOnImageAndSave(savedUriCapture: Uri): Uri {
        val inputStream = context.contentResolver.openInputStream(savedUriCapture)
        var bitmap = BitmapFactory.decodeStream(inputStream)
        var bitmapConfig = bitmap.config
        if (bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888
        }
        bitmap = bitmap.copy(bitmapConfig, true)
//         bitmap = scaleDown(bitmap,300f,true)
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
        val xleft = 100f
        var yleft = 120f
        val locationInfoLeftnew = locationInfoLeft.value.split("\n")
        locationInfoLeftnew.forEach { line ->
            // Process each line
            // Example: Print each line
            canvas.drawText(line, xleft, yleft, paint)
            yleft += 140f
        }

        val xright = (bitmap.width - 20).toFloat()
        var yright = 120f
        val locationInfoRightnew = locationInfoRight.value.split("\n")
        locationInfoRightnew.forEach { line ->
            // Process each line
            // Example: Print each line
            canvas.drawText(line, xright, yright, paintRight)
            yright += 140f
        }

        val cusText = getCusText.invoke().first()

        val textBounds = Rect()
        paint.getTextBounds(cusText, 0, cusText.length, textBounds)

        val paintMid = Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 80f
            textAlign = Paint.Align.LEFT
        }
        val xMid = (bitmap.width - paint.measureText(cusText)) / 2
        val yMid = bitmap.height - 60f

        canvas.drawText(cusText, xMid, yMid, paintMid)

        val existingImageFile = File(context.filesDir, fileNameCapture)

        if (existingImageFile.exists()) {
            existingImageFile.delete()
        }

        val fileName = "IMG_${System.currentTimeMillis()}.JPEG"
        val mimeType = "image/jpeg"
        bmp.value = bitmap

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
        val imageUri: Uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!

// Open an output stream to write the bitmap data
        try {
            imageUri.let { uri ->
                val outputStream: OutputStream? = resolver.openOutputStream(uri)

                // Compress the bitmap to JPEG format and write it to the output stream
                val autoUploadStatus = autoUploadStatus.invoke().first()

                if (autoUploadStatus) {
                    val size = PhotoSize()
                    when (uploadSize.invoke().first()) {
                        "Tiny" -> bitmap.compress(Bitmap.CompressFormat.JPEG, size.tiny, outputStream)
                        "Small" -> bitmap.compress(Bitmap.CompressFormat.JPEG, size.small, outputStream)
                        "Medium" -> bitmap.compress(Bitmap.CompressFormat.JPEG, size.medium, outputStream)
                        "Large" -> bitmap.compress(Bitmap.CompressFormat.JPEG, size.large, outputStream)
                        "Original" -> bitmap.compress(Bitmap.CompressFormat.JPEG, size.original, outputStream)
                    }
                } else {
                    val size = PhotoSize()
                    when (photoSize.invoke().first()) {
                        "Tiny" -> bitmap.compress(Bitmap.CompressFormat.JPEG, size.tiny, outputStream)
                        "Small" -> bitmap.compress(Bitmap.CompressFormat.JPEG, size.small, outputStream)
                        "Medium" -> bitmap.compress(Bitmap.CompressFormat.JPEG, size.medium, outputStream)
                        "Large" -> bitmap.compress(Bitmap.CompressFormat.JPEG, size.large, outputStream)
                        "Original" -> bitmap.compress(Bitmap.CompressFormat.JPEG, size.original, outputStream)
                    }
                }

                // Flush and close the output stream
                outputStream?.flush()
                outputStream?.close()

                // Optionally, you can display a toast message to indicate the image has been saved
                Toast.makeText(context, "Image saved to gallery", LENGTH_SHORT).show()
                deleteImageByName(fileNameCapture)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return imageUri
    }

    private suspend fun processImage(savedUriCapture: Uri?, locationObject: LocationObject): Uri {
        runBlocking {
            Log.d("Upload", "uriResultfromProcess:$locationObject")
            createText(locationObject)
        }

        val result = viewModelScope.async { addTextOnImageAndSave(savedUriCapture!!) }
        insertImagelocationinfo(result.await(), locationObject)
        addLocationToImageUri(result.await(), locationObject)
        Log.d("Upload", "uriResultFromProcess:$result")

        return result.await()
    }

    private suspend fun getWayPointId(uriImage: Uri?, locationObject: LocationObject): String {
        withContext(Dispatchers.IO) {
            Log.d("getWayPointId", "uriImage:$$uriImage")
            Log.d("getWayPointId", "locationObjectawait:$locationObject")
            val c = Calendar.getInstance().time
            val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formattedDate = df.format(c)
            val obj = JSONObject()
            val wayPointObj = JsonObject()
            wayPointObj.addProperty("date", formattedDate)
            wayPointObj.addProperty("lat", locationObject.lat)
            wayPointObj.addProperty("lon", locationObject.lon)
            wayPointObj.addProperty("groupid", currentUser.first()!!.groupIdCheck)
            Log.d("getWayPointId", "locationObjectawait:$locationObject")
            Log.d("AutoUpload", "groupId:${currentUser.first()!!.groupIdCheck}")
            Log.d("AutoUpload", "groupId:${currentUser.first()!!.groupIdCheck}")
            obj.put("waypoint", wayPointObj)
            val result = getWayPointId.invoke(currentUser.first()!!.id, wayPointObj)
            Log.d("AutoUpload", "wayPointObj:$wayPointObj")

            _wayPointId.value = result
        }

        return wayPointId.value!!
    }
    private fun convertUriToMultipart(ImageUri: Uri?): MultipartBody.Part {
        val test = context.contentResolver.openInputStream(ImageUri!!)
        val byteBuff = ByteArrayOutputStream()
        val buffSize = 1024
        val buff = ByteArray(buffSize)
        var len = 0
        while (test!!.read(buff).also { len = it } != -1) {
            byteBuff.write(buff, 0, len)
        }
        val requestFileFront: RequestBody =
            byteBuff.toByteArray().toRequestBody("image/png".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(
            "photodata",
            "picture.png",
            requestFileFront
        )
    }

    private val _processedImage = MutableStateFlow<Uri?>(null)
    val processedImage: StateFlow<Uri?> = _processedImage

    private suspend fun uploadPhoto(
        apiKey: String,
        wayPointId: String,
        photo: MultipartBody.Part?
    ) {
        uploadPicture.invoke(apiKey, wayPointId, photo)
    }
    fun processAutoUpload(savedUriCapture: Uri?) {
        viewModelScope.launch {
            val locationObject = locationObjectState.first()
            Log.d("processAutoUpload", "locationObject1:$$locationObject")
            val image = async { processImage(savedUriCapture, locationObject) }
            if (!currentUser.first()?.id.isNullOrEmpty()) {
                if (autoUploadStatus.invoke().first()) {
                    val apiKey = async { currentUser.first()?.id }
                    val wayPoint = async { getWayPointId(image.await(), locationObject) }
                    Log.d("AutoUpload", "apiKey:${apiKey.await()}")
                    Log.d("AutoUpload", "wayPointId:${wayPoint.await()}")
                    val pictureMultipartBody = async { convertUriToMultipart(image.await()) }
                    uploadPhoto(apiKey.await()!!, wayPoint.await(), pictureMultipartBody.await())
                    Log.d("AutoUpload", "uri:$$pictureMultipartBody.await()")
                }
            }
            val getLocationFromPicture = getLocationFromPicture(image.await())
            Log.d("AutoUpload", "finish upload:locationObject:$getLocationFromPicture")
        }
    }
    fun processUpload() {
        viewModelScope.launch {
            if (!currentUser.first()?.id.isNullOrEmpty()) {
                for (uri in uriSet.value) {
                    val locationObjectbyUri = getLocationFromPicture(uri)
                    Log.d("getLocation", "locationObjectbyUri:$$locationObjectbyUri")
                    Log.d("getLocation", "uri:$${currentUser.first()}")
                    if (!locationObjectbyUri.lat.isNullOrBlank()) {
                        val apiKey = async { currentUser.first()?.id }
                        val wayPoint = async { getWayPointId(uri, locationObjectbyUri) }
                        val pictureMultipartBody = async { convertUriToMultipart(uri) }
                        uploadPhoto(apiKey.await()!!, wayPoint.await(), pictureMultipartBody.await())
                    } else {
                        val currentLocationObject = locationObjectState.first()
                        val apiKey = async { currentUser.first()?.id }
                        val wayPoint = async { getWayPointId(uri, currentLocationObject) }
                        val pictureMultipartBody = async { convertUriToMultipart(uri) }
                        uploadPhoto(apiKey.await()!!, wayPoint.await(), pictureMultipartBody.await())
                    }
                }
            } else {
                Log.d("getLocation", "currentUser:$${currentUser.first()}")
                Toast.makeText(context, "Please login before uploading.", LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchLocationUpdates(): Flow<Location?> = callbackFlow {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val lastLocation = fusedLocationClient.lastLocation
                lastLocation.addOnSuccessListener { location ->
                    trySend(location)
                }.addOnFailureListener {
                    trySend(null)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
        awaitClose { fusedLocationClient.removeLocationUpdates(locationCallback) }
    }

    private suspend fun getCompleteAddress(
        location: Location?
    ) = suspendCoroutine { cont ->
        val lonDouble = location!!.longitude
        val latDouble = location.latitude
        val elevationDouble = location.altitude
        val bearingDouble = location.bearing
        val dataTime = LocalDateTime.now()

        viewModelScope.launch {
            val queryParams = QueryParameters()
            val agsString = "${latDouble}N${lonDouble}W"
            val pnt = CoordinateFormatter.fromLatitudeLongitudeOrNull(
                agsString,
                SpatialReference.webMercator()
            )
            queryParams.geometry = pnt
            queryParams.spatialRelationship = SpatialRelationship.Intersects
            val result = sectionLayer?.featureTable?.queryFeatures(queryParams)
            val utmString = CoordinateFormatter.toUtmOrNull(
                pnt!!,
                UtmConversionMode.NorthSouthIndicators,
                true
            )

            result?.apply {
                onSuccess { queryResult ->
                    val resultIterator = queryResult.iterator()
                    if (resultIterator.hasNext()) {
                        val feature = resultIterator.next()
                        val extent: Envelope = feature.geometry!!.extent
                        val attr: MutableMap<String, Any?> = feature.attributes
                        var secId: String? = ""
                        var secLbl: String? = ""
                        var secTxt: String? = ""

                        for (key in attr.keys) {
                            when (key) {
                                "TTTMRRSS" -> secId = attr[key] as String?
                                "LABEL1" -> secLbl = attr[key] as String?
                                "SEC" -> secTxt = attr[key] as String?
                            }
                        }

                        val xDelta = (lonDouble - extent.xMin) / (extent.xMax - extent.xMin)
                        val yDelta = (latDouble - extent.yMin) / (extent.yMax - extent.yMin)
                        val utmTup = sepUtm(utmString!!)
//                        val getCustomText = preferenceDataStore.getCustomText.last()

                        cont.resume(
                            LocationObject().apply {
                                this.lat = String.format("%.6f", latDouble)
                                this.lon = String.format("%.6f", lonDouble)
                                this.elevation = "Eleve: ${dec.format(elevationDouble)} m"
                                this.gridLocation = getGridLocation(secLbl, xDelta, yDelta)
                                this.distance = getDistances(
                                    utmTup.second!!.toInt(),
                                    utmTup.third!!.toInt(),
                                    extent
                                )
                                this.utmCoordinate =
                                    utmTup.third + " m " + utmTup.second + " m " + "Zone: " + utmTup.first
                                this.bearing =
                                    String.format("Bearing: %.0f", bearingDouble) + "\u2103 TN"
                                this.address = getAddressFromLocation(latDouble, lonDouble)
                                this.date = dataTime.format(
                                    DateTimeFormatter.ofPattern(
                                        "MMM d, yyyy HH:mm:ss",
                                        Locale.ENGLISH
                                    )

                                )
//                            this.custText = getCustomText
                            }
                        )
                    }
                }
                onFailure {
                    cont.resume(LocationObject())
                }
            }
        }
    }

    private fun getAddressFromLocation(
        latitude: Double?,
        longitude: Double?
    ): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        return try {
            val addresses: List<Address> = geocoder.getFromLocation(latitude!!, longitude!!, 1) ?: listOf()
            return if (addresses.isNotEmpty()) {
                val address: Address = addresses[0]
                // Retrieve the address information
                val addressLine: String = address.getAddressLine(0)
//                val city: String? = address.locality
//                val state: String? = address.adminArea
//                val country: String? = address.countryName
//                val postalCode: String? = address.postalCode
                addressLine
            } else {
                Log.d("getLocationProcess", "address:fail")
                ""
            }
        } catch (e: Exception) {
            Log.d("getLocationProcess", "address exception")
            e.printStackTrace()
            ""
        }
    }

    fun getDistances(x: Int, y: Int, ext: Envelope): String {
        val strMin = String.format("%.6f", ext.yMin) + "N " + String.format("%.6f", ext.xMin) + "W"
        val pntMin =
            CoordinateFormatter.fromLatitudeLongitudeOrNull(strMin, SpatialReference.webMercator())

        val utmMin =
            CoordinateFormatter.toUtmOrNull(pntMin!!, UtmConversionMode.NorthSouthIndicators, true)

        val minTup = sepUtm(utmMin!!)
        val minX: Int = try {
            minTup.component2()!!.toInt()
        } catch (e: Exception) {
            0
        }
        val minY: Int = try {
            minTup.component3()!!.toInt()
        } catch (e: Exception) {
            0
        }
        val xd = x - minX
        val yd = y - minY
        var dist = ""
        if (yd in 0..800) {
            dist = yd.toString() + "m North"
        } else if (yd > 800) {
            val temp = 1600 - yd
            dist = temp.toString() + "m South"
        }
        if (xd in 0..800) {
            dist += " " + xd.toString() + "m East"
        } else if (xd > 800) {
            val temp = 1600 - xd
            dist += " " + temp.toString() + "m West"
        }
        val ch = '\u223D'
        return "$ch $dist"
    }

    private fun sepUtm(str: String): Triple<String?, String?, String?> {
        val utmArr = str.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        val zone = utmArr[0].substring(0, utmArr[0].length - 1)
        val east = utmArr[1]
        val nor = utmArr[2]
        return Triple(zone, east, nor)
    }

    private fun getGridLocation(sec: String?, x: Double, y: Double): String {
        var qtr = ""
        var lsd = 0
        if (x > 0 && x <= 0.25) {
            if (y > 0 && y <= 0.25) {
                lsd = 4
                qtr = "SW"
            } else if (y > 0.25 && y <= 0.50) {
                lsd = 5
                qtr = "SW"
            } else if (y > 0.50 && y <= 0.75) {
                lsd = 12
                qtr = "NW"
            } else if (y > 0.75 && y <= 1.00) {
                lsd = 13
                qtr = "NW"
            }
        } else if (x > 0.25 && x <= 0.50) {
            if (y > 0 && y <= 0.25) {
                lsd = 3
                qtr = "SW"
            } else if (y > 0.25 && y <= 0.50) {
                lsd = 6
                qtr = "SW"
            } else if (y > 0.50 && y <= 0.75) {
                lsd = 11
                qtr = "NW"
            } else if (y > 0.75 && y <= 1.00) {
                lsd = 14
                qtr = "NW"
            }
        } else if (x > 0.50 && x <= 0.75) {
            if (y > 0 && y <= 0.25) {
                lsd = 2
                qtr = "SE"
            } else if (y > 0.25 && y <= 0.50) {
                lsd = 7
                qtr = "SE"
            } else if (y > 0.50 && y <= 0.75) {
                lsd = 10
                qtr = "NE"
            } else if (y > 0.75 && y <= 1.00) {
                lsd = 15
                qtr = "NE"
            }
        } else if (x > 0.75 && x <= 1.00) {
            if (y > 0 && y <= 0.25) {
                lsd = 1
                qtr = "SE"
            } else if (y > 0.25 && y <= 0.50) {
                lsd = 8
                qtr = "SE"
            } else if (y > 0.50 && y <= 0.75) {
                lsd = 9
                qtr = "NE"
            } else if (y > 0.75 && y <= 1.00) {
                lsd = 16
                qtr = "NE"
            }
        }

        return "($qtr) $lsd-$sec"
    }
}
