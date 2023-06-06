package com.example.android.rickandmortyapp.repository

import com.example.android.rickandmortyapp.model.ApiResponse
import com.example.android.rickandmortyapp.model.CharacterModel
import com.example.android.rickandmortyapp.util.Resource
import javax.inject.Inject

class CharacterRepository @Inject constructor(
    private val apiService: RickAndMortyApiService
) {


    suspend fun getCharacters(page: Int): Resource<ApiResponse<CharacterModel>> {
        val response = try {
            apiService.getCharacters(page)
        }catch (e: Exception){
            return Resource.Error("An Unknown error occurred")
        }

        return Resource.Success(response)
    }

    suspend fun getCharacterById(id:Int): Resource<CharacterModel> {
        val response = try {
            apiService.getCharacterById(id)
        }catch (e: Exception){
            return Resource.Error("An Unknown error occurred")
        }

        return Resource.Success(response)
    }
}