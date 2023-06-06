package com.example.android.rickandmortyapp.characterdetail

import androidx.lifecycle.ViewModel
import com.example.android.rickandmortyapp.model.CharacterModel
import com.example.android.rickandmortyapp.repository.CharacterRepository
import com.example.android.rickandmortyapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CharacterDetailViewModel @Inject constructor(
    private val repository: CharacterRepository
): ViewModel(){

    suspend fun getCharacterById(id: Int): Resource<CharacterModel> {
        return repository.getCharacterById(id)
    }
}