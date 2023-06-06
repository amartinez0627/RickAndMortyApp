package com.example.android.rickandmortyapp.characterdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.android.rickandmortyapp.model.CharacterModel
import com.example.android.rickandmortyapp.util.Resource

@Composable
fun CharacterDetailScreen(
    dominantColor: Color,
    characterId: Int,
    navController: NavController,
    topPadding: Dp = 20.dp,
    characterImageSize: Dp = 200.dp,
    viewModel: CharacterDetailViewModel = hiltViewModel()
) {
    val characterInfo = produceState<Resource<CharacterModel>>(initialValue = Resource.Loading()){
        value = viewModel.getCharacterById(characterId)
    }.value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(dominantColor)
            .padding(bottom = 16.dp)
    ){
        CharacterDetailTopSection(
            navController = navController,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.2f)
                .align(Alignment.TopCenter)
        )
        CharacterDetailStateWrapper(
            characterInfo = characterInfo,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = topPadding + characterImageSize / 2f,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
                .shadow(10.dp, RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colors.surface)
                .padding(16.dp)
                .align(Alignment.BottomCenter),
            loadingModifier = Modifier
                .size(100.dp)
                .align(Alignment.Center)
                .padding(
                    top = topPadding + characterImageSize / 2f,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
        )
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxSize()
        ){
            if(characterInfo is Resource.Success){
                val result = characterInfo.data!!
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(result.image)
                        .crossfade(true)
                        .build(),
                    contentDescription = result.name,
                    modifier = Modifier
                        .size(characterImageSize)
                        .offset(y = topPadding)
                )
            }
        }
    }
}

@Composable
fun CharacterDetailTopSection(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.TopStart,
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.Black,
                        Color.Transparent
                    )
                )
            )
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(36.dp)
                .offset(16.dp, 16.dp)
                .clickable {
                    navController.popBackStack()
                }
        )
    }
}

@Composable
fun CharacterDetailStateWrapper(
    characterInfo: Resource<CharacterModel>,
    modifier: Modifier = Modifier,
    loadingModifier: Modifier = Modifier
){
    when(characterInfo){
        is Resource.Success ->{
            CharacterDetailSection(
                characterInfo = characterInfo.data!!,
                modifier = modifier
                    .offset(y = (-20).dp)
            )
        }
        is Resource.Error ->{
            Text(
                text = characterInfo.message ?: "",
                color = Color.Red,
                modifier = modifier

            )
        }
        is Resource.Loading ->{
            CircularProgressIndicator(
                color = MaterialTheme.colors.primary,
                modifier = loadingModifier
            )
        }
    }
}

@Composable
fun CharacterDetailSection(
    characterInfo: CharacterModel,
    modifier: Modifier = Modifier
){
    val scrollState = rememberScrollState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .offset(y = 100.dp)
            .verticalScroll(scrollState)
    ){
        Text(
            text = "#${characterInfo.id} ${characterInfo.name.replaceFirstChar { it.uppercase() }}",
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.onSurface
        )
        CharacterDetailRow(key = "Gender:", value = characterInfo.gender)
        CharacterDetailRow(key = "Species:", value = characterInfo.species)
        CharacterDetailRow(key = "Status:", value = characterInfo.status)
        CharacterDetailRow(key = "Location:", value = characterInfo.location.name)
        CharacterDetailRow(key = "Origin:", value = characterInfo.origin.name)
    }
}

@Composable
fun CharacterDetailRow(
    key: String,
    value: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.onSurface
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Text(text = key, color = color)
        Spacer(modifier = Modifier.width(5.dp))
        Text(text = value,  color = color)
    }
}