package com.example.dlsandroidredesign.ui.gallery

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.annotation.ExperimentalCoilApi
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.dlsandroidredesign.ImageLocationInfoViewModel
import com.example.dlsandroidredesign.R
import com.example.dlsandroidredesign.ui.mainScreen.MainScreenViewModel

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalCoilApi::class, ExperimentalFoundationApi::class)
@Composable
fun GalleryScreen(mainScreenViewModel: MainScreenViewModel = hiltViewModel(), imageLocationInfoViewModel: ImageLocationInfoViewModel = hiltViewModel(), galleryViewModel: GalleryViewModel = hiltViewModel()) {
    var selectedImageUris = galleryViewModel.selectedImageUris.collectAsStateWithLifecycle().value

    val uriSet = imageLocationInfoViewModel.uriSet.collectAsStateWithLifecycle()

    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris -> galleryViewModel.getMergeList(uris) }
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(390.dp)
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.End)
                .padding(8.dp)
        ) {
            OutlinedButton(
                onClick = { imageLocationInfoViewModel.processUpload() },
                colors = ButtonDefaults.buttonColors(Color.Gray)
            ) {
                Text(text = "Done", color = Color.White)
            }
        }

        LazyVerticalGrid(columns = GridCells.Fixed(3), contentPadding = PaddingValues(8.dp)) {
            item {
                CameraPicker()
            }
            item {
                Image(
                    painter = painterResource(id = R.drawable.gallery),
                    contentDescription = null,
                    modifier = Modifier
                        .clickable {
                            multiplePhotoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                        .padding(4.dp)
                        .aspectRatio(1f),
                    contentScale = ContentScale.FillBounds

                )
            }

            // PictureFromDefaultGallery
            items(selectedImageUris) { uri ->
                var isSelected by remember { mutableStateOf(uriSet.value.contains(uri)) }
                val locationObject = imageLocationInfoViewModel.getLocationFromPicture(uri)
                Log.d("imagePicker", "locationObject$locationObject")
                imageLocationInfoViewModel.insertImagelocationinfo(uri, locationObject)
                Box(
                    modifier = Modifier.clickable {
                        imageLocationInfoViewModel.setUriSet(uri)
                        val check = uriSet.value.contains(uri)
                        isSelected = check
                    }
                ) {
                    AsyncImage(
                        model = uri,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(4.dp)
                            .aspectRatio(1f),
                        contentScale = ContentScale.FillBounds
                    )
                    if (isSelected) {
                        Image(
                            modifier = Modifier
                                .align(Alignment.Center),
                            painter = painterResource(id = R.drawable.ic_checkmark_single),
                            contentDescription = null
                        )
                    }
                }
            }

            // PictureFromTheApp
            items(mainScreenViewModel.allImages.value) { imageUri ->
                var isSelected by remember { mutableStateOf(uriSet.value.contains(imageUri)) }
                Box(
                    modifier = Modifier.clickable {
                        imageLocationInfoViewModel.setUriSet(imageUri)
                        val check = uriSet.value.contains(imageUri)
                        isSelected = check

                        Log.d("galleryClick", "uriSet:$uriSet")
                    }
                ) {
                    Log.d("galleryClick", "isSelected:$isSelected")
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(4.dp)
                            .aspectRatio(1f),
                        contentScale = ContentScale.FillBounds

                    )
                    Log.d("galleryClick", "Boolean:${imageLocationInfoViewModel.checkUriContain(imageUri)}")

                    if (isSelected) {
                        Image(
                            modifier = Modifier
                                .align(Alignment.Center),
                            painter = painterResource(id = R.drawable.ic_checkmark_single),
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CameraPicker() {
    val context = LocalContext.current as ComponentActivity
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Handle the result of the camera picker
        if (result.resultCode == Activity.RESULT_OK) {
            // Camera picker completed successfully
            // You can handle the result here
        } else {
            // Camera picker was canceled or failed
            // Handle the error or cancellation here
        }
    }
    Image(
        painter = painterResource(id = R.drawable.camera_picker),
        contentDescription = null,
        modifier = Modifier
            .padding(2.dp)
            .padding(4.dp)
            .aspectRatio(1f)
            .clickable {
                val cameraPickerIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                launcher.launch(cameraPickerIntent)
            },
        contentScale = ContentScale.FillBounds

    )
}
