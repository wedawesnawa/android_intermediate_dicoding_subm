package id.example.storyapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import id.example.storyapp.model.ListStoryItem
import id.example.storyapp.model.UserModel
import id.example.storyapp.repository.StoryRepository
import id.example.storyapp.repository.UserRepository
import kotlinx.coroutines.launch

class MainViewModel(storyRepository: StoryRepository, private val userRepository: UserRepository) : ViewModel() {

    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession()
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }

    val stories: LiveData<PagingData<ListStoryItem>> =
        storyRepository.getAllStories().cachedIn(viewModelScope)

}