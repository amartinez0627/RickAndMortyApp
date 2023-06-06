package com.example.android.rickandmortyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.android.rickandmortyapp.characterList.CharacterListScreen
import com.example.android.rickandmortyapp.characterdetail.CharacterDetailScreen
import com.example.android.rickandmortyapp.ui.theme.RickAndMortyAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RickAndMortyAppTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "character_list_screen"
                ){
                    composable("character_list_screen"){
                        CharacterListScreen(navController = navController)
                    }
                    composable(
                        "character_detail_screen/{dominantColor}/{characterId}",
                        arguments = listOf(
                            navArgument("dominantColor"){
                                type = NavType.IntType
                            },
                            navArgument("characterId"){
                                type = NavType.IntType
                            }
                        )
                    ){
                        val dominantColor = remember{
                            val color = it.arguments?.getInt("dominantColor")
                            color?.let {
                                Color(it)
                            } ?: Color.White
                        }
                        val characterId = remember{
                            it.arguments?.getInt("characterId") ?: 0
                        }
                        CharacterDetailScreen(
                            dominantColor = dominantColor,
                            characterId = characterId ,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}
