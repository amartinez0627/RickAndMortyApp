package com.example.android.rickandmortyapp.di

import com.example.android.rickandmortyapp.repository.CharacterRepository
import com.example.android.rickandmortyapp.repository.RickAndMortyApiService
import com.example.android.rickandmortyapp.util.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideCharacterRepository(
        apiService: RickAndMortyApiService
    ) = CharacterRepository(apiService)

    @Provides
    @Singleton
    fun provideCharacterApi(): RickAndMortyApiService {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(RickAndMortyApiService::class.java)
    }
}