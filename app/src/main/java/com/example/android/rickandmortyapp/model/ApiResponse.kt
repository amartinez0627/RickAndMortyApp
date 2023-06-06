package com.example.android.rickandmortyapp.model

data class ApiResponse<T>(
    val info: Info,
    val results: List<T>
)
