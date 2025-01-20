package id.example.storyapp.ui.addStory

import android.location.Location
import android.net.Uri
import androidx.lifecycle.ViewModel
import id.example.storyapp.repository.StoryRepository
import java.io.File

class AddStoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    var currentImageUri: Uri? = null
     var currentLocation: Location? = null

    fun uploadImage(file: File, description: String, lat: String? = null,
                    lon: String? = null) =
        storyRepository.uploadImage(file, description, lat, lon)


}