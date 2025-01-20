package id.example.storyapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.google.gson.Gson
import id.example.storyapp.data.UiState
import id.example.storyapp.local.preference.UserPreference
import id.example.storyapp.local.remote_mediator.StoryRemoteMediator
import id.example.storyapp.local.room.StoryRoomDatabase
import id.example.storyapp.model.FileUploadResponse
import id.example.storyapp.model.ListStoryItem
import id.example.storyapp.api.ApiService
import id.example.storyapp.utils.EspressoIdlingResource
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File


class StoryRepository(
    private val apiService: ApiService,
    private val storyRoomDatabase: StoryRoomDatabase,
    private val userPreference: UserPreference
) {
    @OptIn(ExperimentalPagingApi::class)
    fun getAllStories(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5,
                enablePlaceholders = false
            ),
            remoteMediator = StoryRemoteMediator(storyRoomDatabase, apiService, userPreference),
            pagingSourceFactory = { storyRoomDatabase.storyDao().getAllStory() }
        ).liveData
    }


    fun getStoryWithLocation(): LiveData<UiState<List<ListStoryItem>>> = liveData {
        emit(UiState.Loading)
        try {
            val pref = userPreference.getSession().first().token
            val token = "Bearer $pref"
            val response = apiService.getStoriesWithLocation(token)
            val stories = response.listStory
            emit(UiState.Success(stories))
        } catch (e: Exception) {
            emit(UiState.Error(e.message.toString()))
        }
    }

    fun getDetailStory(storyId: String) = liveData {
        emit(UiState.Loading)
        try {
            val pref = userPreference.getSession().first().token
            val token = "Bearer $pref"
            val response = apiService.getDetailStory(token, storyId)
            val story = response.story
            emit(UiState.Success(story))
        } catch (e: Exception) {
            emit(UiState.Error(e.message.toString()))
        }
    }

    fun uploadImage(imageFile: File, description: String, lat: String? = null, lon: String? = null) = liveData {
        emit(UiState.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val latBody = lat?.toRequestBody("text/plain".toMediaTypeOrNull())
        val lonBody = lon?.toRequestBody("text/plain".toMediaTypeOrNull())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile,
        )
        EspressoIdlingResource.increment()
        try {
            val pref = userPreference.getSession().first().token
            val token = "Bearer $pref"
            val successResponse = apiService.uploadImage( token ,multipartBody, requestBody, latBody, lonBody)
            emit(UiState.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, FileUploadResponse::class.java)
            emit(errorResponse.message?.let { UiState.Error(it) })
        } finally {
            EspressoIdlingResource.decrement()
        }

    }



    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            storyRoomDatabase: StoryRoomDatabase,
            apiService: ApiService,
            userPreference: UserPreference
        ): StoryRepository = instance ?: synchronized(this) {
            instance ?: StoryRepository(apiService, storyRoomDatabase, userPreference)
        }.also { instance = it }
    }
}