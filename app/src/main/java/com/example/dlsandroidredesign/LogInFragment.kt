@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.example.dlsandroidredesign

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Preview
@Composable
fun LogInFragement(){
    val context= LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val retrofitInstance = RetoInstance().getInstance()
    val apiService = retrofitInstance.create(ApiInterfaceService::class.java)
    val preferenceDataStore = PreferencesDataStore(context)

    var userName = preferenceDataStore.getUsername.collectAsState(initial = "").value
    var passWord= preferenceDataStore.getPassword.collectAsState(initial = "").value




    suspend fun validate(username: String, password: String){
//        val name = call?.execute()?.body()?.getName()
        val response = apiService.validate(userName = username, pass = password)
        val body = response.body()
        if(response.isSuccessful && response.body()!!._success==1) {
            preferenceDataStore.setLoginSharedInfoList(body!!)
            preferenceDataStore.setIsLoginSuccessful(body.success)
            preferenceDataStore.setWaypointGroup(body.waypointgroups!!)
            preferenceDataStore.setWaypointgroupCheck(body.waypointgroups!![0].groupid!!)
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()

        }
        else
        {
            preferenceDataStore.setLogInStatus(false)
            Toast.makeText(context, "Fail", Toast.LENGTH_SHORT).show()

        }
    }



    Column(modifier = Modifier
        .background(Color.LightGray)
        .fillMaxSize()) {
        Box(modifier = Modifier
            .background(Color.White)
            .padding(20.dp)
            .fillMaxWidth()){
            Text(text = "DLSPhoto must verify your AbaData Credentials", fontSize = 20.sp)
        }
        Column(
            Modifier
                .padding(10.dp, 10.dp, 10.dp, 10.dp)
                .background(Color.LightGray)) {
            Text(text = " USERNAME")
            Spacer(modifier = Modifier.height(height = 3.dp))
            BasicTextField(value = "$userName", onValueChange = {coroutineScope.launch { preferenceDataStore.setUsername(it) }},modifier= Modifier
                .background(Color.White, shape = RoundedCornerShape(5.dp))
                .fillMaxWidth()
                .size(30.dp, 50.dp))

            Spacer(modifier = Modifier.height(height = 8.dp))
            Text(text = " PASSWORD")
            var showPassword by remember {
                mutableStateOf(false)
            }
            Spacer(modifier = Modifier.height(height = 3.dp))
            BasicTextField(
                value = "$passWord",
                onValueChange = {coroutineScope.launch { preferenceDataStore.setPassword(it)}},
                modifier= Modifier
                    .background(Color.White, shape = RoundedCornerShape(5.dp))
                    .fillMaxWidth()
                    .size(30.dp, 50.dp),
                visualTransformation = PasswordVisualTransformation(),
            )
            Spacer(modifier = Modifier.height(height = 14.dp))
            Row( modifier = if (userName!=="" && passWord!=="")
            {
                Modifier
                    .clickable {
                        runBlocking{
                            validate(userName, passWord)
                        }
                    }
                    .fillMaxWidth()
                    .size(30.dp, 35.dp)
                    .background(Color.White, shape = RoundedCornerShape(5.dp))
                    .padding(horizontal = 10.dp)
                                                               }
                else{
                Modifier
                    .fillMaxWidth()
                    .size(30.dp, 35.dp)
                    .background(Color.White, shape = RoundedCornerShape(5.dp))
                    .padding(horizontal = 10.dp)
                    }
                , horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically)

            {
                Image(painter = painterResource(id = R.drawable.ic_checkmark_single), contentDescription =null , modifier = Modifier.size(15.dp,15.dp),colorFilter = ColorFilter.tint(
                    if (userName!=="" && passWord!==""){Color.Blue}else{Color.Black}))
                Spacer(modifier = Modifier.width(width = 8.dp))
                Text(text = "Verify", color = (if (userName!=="" && passWord!==""){Color.Blue}else{Color.Black}) )

            }
            }

    }}






