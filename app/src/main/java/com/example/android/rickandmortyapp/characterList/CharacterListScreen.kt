package com.example.android.rickandmortyapp.characterList

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.android.rickandmortyapp.R
import com.example.android.rickandmortyapp.model.CharacterListEntry
import com.example.android.rickandmortyapp.ui.theme.RobotoCondensed


@Composable
fun CharacterListScreen(
    navController: NavController,
    viewModel: CharacterListViewModel = hiltViewModel()
) {
    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            Spacer(modifier = Modifier.height(20.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = painterResource(id = R.drawable.ic_rick_and_morty),
                    contentDescription = "Rick and morty Logo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                )
            }
            SearchBar(
                hint = "Search...",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ){
                viewModel.searchCharacterList(it)
            }
            Spacer(modifier = Modifier.height(16.dp))
            CharacterListComp(navController = navController)
        }
    }
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    hint: String = "",
    onSearch: (String) -> Unit = {}
){
    var text by remember {
        mutableStateOf("")
    }
    var isHintDisplayed by remember {
        mutableStateOf( hint.isNotEmpty() )
    }

    Box(modifier){
        BasicTextField(
            value = text,
            onValueChange = {
                text = it
                onSearch(it)
            },
            maxLines = 1,
            singleLine = true,
            textStyle = TextStyle(color = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(5.dp, CircleShape)
                .background(Color.White, CircleShape)
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .onFocusChanged {
                    isHintDisplayed = !it.isFocused && text.isEmpty()
                }
        )
        if (isHintDisplayed){
            Text(
                text = hint,
                color = Color.LightGray,
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            )
        }
    }
}

@Composable
fun CharacterListComp(
    navController: NavController,
    viewModel: CharacterListViewModel = hiltViewModel()
){
    val characterList by remember { viewModel.characterList }
    val endReached by remember { viewModel.endReached }
    val loadError by remember { viewModel.loadError }
    val isLoading by remember { viewModel.isLoading }
    val isSearching by remember { viewModel.isSearching }

    LazyColumn(
        contentPadding = PaddingValues(16.dp)
    ){
        val itemCount = if (characterList.size % 2 == 0) {
            characterList.size / 2
        }else{
            characterList.size / 2 + 1
        }

        items(itemCount){
            if (it >= itemCount -1 && !endReached && !isLoading && !isSearching){
                viewModel.loadCharacters()
            }
            CharacterRow(
                rowIndex = it,
                entries = characterList,
                navController = navController
            )
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ){
        if(isLoading){
            CircularProgressIndicator(color = MaterialTheme.colors.primary)
        }
        if (loadError.isNotEmpty()){
            RetrySection(error = loadError) {
                viewModel.loadCharacters()
            }
        }
    }
}

@Composable
fun CharacterRow(
    rowIndex: Int,
    entries: List<CharacterListEntry>,
    navController: NavController
){
    Column {
        Row {
            CharacterEntry(
                entry = entries[rowIndex * 2],
                navController = navController,
                modifier = Modifier
                    .weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            if(entries.size >= rowIndex * 2 + 2){
                CharacterEntry(
                    entry = entries[rowIndex * 2 + 1],
                    navController = navController,
                    modifier = Modifier
                        .weight(1f)
                )
            }
            else{
                Spacer(modifier = Modifier.weight(1f))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun CharacterEntry(
    entry: CharacterListEntry,
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: CharacterListViewModel = hiltViewModel()
){
    val defaultDominantColor = MaterialTheme.colors.surface
    var dominantColor by remember {
        mutableStateOf(defaultDominantColor)
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .shadow(5.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .aspectRatio(1f)
            .background(
                Brush.verticalGradient(
                    listOf(
                        dominantColor,
                        defaultDominantColor
                    )
                )
            )
            .clickable {
                navController.navigate(
                    "character_detail_screen/${dominantColor.toArgb()}/${entry.id}"
                )
            }
    ){
        Column {
            SubcomposeAsyncImage(
                model  = ImageRequest.Builder(LocalContext.current)
                    .data(entry.imageUrl)
                    .crossfade(true)
                    .build(),
                loading = {
                    CircularProgressIndicator(
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier.scale(.5f)
                    )
                },
                onSuccess = {
                    viewModel.calcDominantColor(it.result.drawable) { color ->
                        dominantColor = color
                    }
                },
                contentDescription = entry.characterName,
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally),
                filterQuality = FilterQuality.None
            )
            Text(
                text = entry.characterName,
                fontFamily = RobotoCondensed,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun RetrySection(
    error: String,
    onRetry: () -> Unit
){
    Column {
        Text(
            text = error,
            color = Color.Red,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(18.dp))
        Button(
            onClick = {onRetry()},
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Retry")
        }
    }
}
