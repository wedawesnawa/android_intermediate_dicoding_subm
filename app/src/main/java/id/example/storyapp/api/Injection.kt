package id.example.storyapp.api

import android.content.Context
import id.example.storyapp.local.preference.UserPreference
import id.example.storyapp.local.preference.dataStore
import id.example.storyapp.local.room.StoryRoomDatabase
import id.example.storyapp.repository.StoryRepository
import id.example.storyapp.repository.UserRepository

object Injection {
    fun provideStoryRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig().getApiService()
        val storyRoomDatabase = StoryRoomDatabase.getInstance(context)
        return StoryRepository.getInstance(storyRoomDatabase ,apiService, pref)
    }
    fun provideUserRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig().getApiService()
        return UserRepository.getInstance(pref, apiService)
    }
}