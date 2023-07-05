package com.example.dlsandroidredesign.ui.login

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dlsandroidredesign.R

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LogInScreen(viewModel: LogInViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val errorMessage = viewModel.errorMessage.collectAsState().value
    Log.d("LoginScreen", "create")
    LogInScreen(
        onLogin = { username: String, password: String ->
            viewModel.validate(username, password)
            if (viewModel.success.value) {
                Toast.makeText(context, "Login Successfully", Toast.LENGTH_SHORT).show()
            }
        },
        errorMessage = errorMessage

    )
    Log.d("LoginScreen", "create")
}

@Composable
private fun LogInScreen(
    onLogin: (username: String, password: String) -> Unit,
    errorMessage: String?
) {
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .background(Color.LightGray)
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .background(Color.White)
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            Text(text = "DLSPhoto must verify your AbaData Credentials", fontSize = 20.sp)
        }
        Column(
            Modifier
                .padding(10.dp, 10.dp, 10.dp, 10.dp)
                .background(Color.LightGray)
            , horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column() {
                Text(text = "USERNAME")
                Spacer(modifier = Modifier.height(height = 3.dp))
                BasicTextField(
                    modifier = Modifier
                        .background(Color.White, shape = RoundedCornerShape(5.dp))
                        .fillMaxWidth()
                        .size(30.dp, 50.dp),
                    value = username,
                    onValueChange = { username = it }
                )
            }

            Spacer(modifier = Modifier.height(height = 8.dp))
            Column() {
                Text(text = " PASSWORD")

                Spacer(modifier = Modifier.height(height = 3.dp))
                BasicTextField(
                    modifier = Modifier
                        .background(Color.White, shape = RoundedCornerShape(5.dp))
                        .fillMaxWidth()
                        .size(30.dp, 50.dp),
                    value = password,
                    onValueChange = { password = it },
                    visualTransformation = PasswordVisualTransformation()
                )
            }

            Spacer(modifier = Modifier.height(height = 14.dp))
            val isAllFieldsReady = username.isNotBlank() && password.isNotBlank()
            Row(
                modifier = if (isAllFieldsReady) {
                    Modifier
                        .clickable { onLogin(username, password) }
                        .fillMaxWidth()
                        .size(30.dp, 35.dp)
                        .background(Color.White, shape = RoundedCornerShape(5.dp))
                        .padding(horizontal = 10.dp)
                } else {
                    Modifier
                        .fillMaxWidth()
                        .size(30.dp, 35.dp)
                        .background(Color.White, shape = RoundedCornerShape(5.dp))
                        .padding(horizontal = 10.dp)
                },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_checkmark_single),
                    contentDescription = null,
                    modifier = Modifier.size(15.dp, 15.dp),
                    colorFilter = ColorFilter.tint(
                        if (isAllFieldsReady) {
                            Color.Blue
                        } else {
                            Color.Black
                        }
                    )
                )
                Spacer(modifier = Modifier.width(width = 8.dp))
                Text(
                    text = "Verify",
                    color = (
                        if (isAllFieldsReady) {
                            Color.Blue
                        } else {
                            Color.Black
                        }
                        )
                )
            }
            Text(text = "$errorMessage")
        }
    }
}
