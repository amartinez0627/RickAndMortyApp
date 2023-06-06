package com.example.android.rickandmortyapp.repository

import com.example.android.rickandmortyapp.model.ApiResponse
import com.example.android.rickandmortyapp.model.CharacterModel
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RickAndMortyApiService {
    @GET("character")
    suspend fun getCharacters(@Query("page") page: Int): ApiResponse<CharacterModel>

    @GET("character/{id}")
    suspend fun getCharacterById(@Path("id") id:Int): CharacterModel
}