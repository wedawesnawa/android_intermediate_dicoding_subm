package id.example.storyapp.ui.maps

import androidx.lifecycle.ViewModel
import id.example.storyapp.repository.StoryRepository

class MapsViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun getStoryWithLocation() = storyRepository.getStoryWithLocation()
}