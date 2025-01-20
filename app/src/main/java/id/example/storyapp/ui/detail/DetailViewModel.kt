package id.example.storyapp.ui.detail

import androidx.lifecycle.ViewModel
import id.example.storyapp.repository.StoryRepository

class DetailViewModel(private val storyRepository: StoryRepository): ViewModel() {
    fun detailStory(id: String) = storyRepository.getDetailStory(id)
}