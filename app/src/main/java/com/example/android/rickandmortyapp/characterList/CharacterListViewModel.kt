package com.example.android.rickandmortyapp.characterList

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.example.android.rickandmortyapp.model.CharacterListEntry
import com.example.android.rickandmortyapp.repository.CharacterRepository
import com.example.android.rickandmortyapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharacterListViewModel @Inject constructor(
    private val repository: CharacterRepository
) : ViewModel() {

    private var currPage = 1

    var characterList = mutableStateOf<List<CharacterListEntry>>(listOf())
    var loadError = mutableStateOf("")
    var isLoading = mutableStateOf(false)
    var endReached = mutableStateOf(false)

    private var cachedCharacterList = listOf<CharacterListEntry>()
    private var isSearchStarting = true
    var isSearching = mutableStateOf(false)

    init {
        loadCharacters()
    }

    fun loadCharacters() {
        viewModelScope.launch {
            isLoading.value = true
            val result = repository.getCharacters(currPage)
            when(result){
                is Resource.Success -> {
                    endReached.value = currPage > (result.data?.info?.pages ?: 0)
                    val characterEntries = result.data!!.results.map { entry ->
                        CharacterListEntry(
                            entry.name,
                            entry.image,
                            entry.id
                        )
                    }


                    currPage++

                    loadError.value = ""
                    isLoading.value = false
                    characterList.value += characterEntries
                }
                is Resource.Error -> {
                    loadError.value = result.message!!
                    isLoading.value = false
                }
                is Resource.Loading ->{

                }
            }
        }
    }

    fun searchCharacterList(searchQuery: String){
        val listToSearch = if (isSearchStarting){
            characterList.value
        } else{
            cachedCharacterList
        }

        viewModelScope.launch(Dispatchers.Default) {
            if(searchQuery.isEmpty()){
                characterList.value = cachedCharacterList
                isSearching.value = false
                isSearchStarting = true
                return@launch
            }
            val result = listToSearch.filter {
                it.characterName.contains(searchQuery.trim(), ignoreCase = true) ||
                        it.id.toString() == searchQuery.trim()
            }

            if (isSearchStarting){
                cachedCharacterList = characterList.value
                isSearchStarting = false
            }

            characterList.value = result
            isSearching.value = true
        }
    }

    fun calcDominantColor(drawable: Drawable, onFinish: (Color) -> Unit){
        val bmp = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)

        Palette.from(bmp).generate{ palette ->
            palette?.dominantSwatch?.rgb?.let { colorValue ->
                onFinish(Color(colorValue))
            }
        }
    }

}