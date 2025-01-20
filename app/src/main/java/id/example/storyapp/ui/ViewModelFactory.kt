package id.example.storyapp.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import id.example.storyapp.repository.StoryRepository
import id.example.storyapp.repository.UserRepository
import id.example.storyapp.api.Injection.provideStoryRepository
import id.example.storyapp.api.Injection.provideUserRepository
import id.example.storyapp.ui.addStory.AddStoryViewModel
import id.example.storyapp.ui.detail.DetailViewModel
import id.example.storyapp.ui.login.LoginViewModel
import id.example.storyapp.MainViewModel
import id.example.storyapp.ui.maps.MapsViewModel
import id.example.storyapp.ui.register.RegisterViewModel

class ViewModelFactory private constructor(
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(storyRepository, userRepository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                AddStoryViewModel(storyRepository) as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(storyRepository) as T
            }
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(storyRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory {
            return INSTANCE ?: synchronized(this) {
                return@synchronized INSTANCE ?: ViewModelFactory(
                    userRepository = provideUserRepository(context),
                    storyRepository = provideStoryRepository(context)
                ).also { INSTANCE = it }
            }
        }
    }
}