package com.example.dlsandroidredesign.domain

sealed class Outcome<out R> {
    data class Success<out T>(val data: T) : Outcome<T>()
    data class Error(val error: Exception) : Outcome<Nothing>()
}
