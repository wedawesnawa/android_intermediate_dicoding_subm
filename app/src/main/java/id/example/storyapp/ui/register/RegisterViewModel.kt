package id.example.storyapp.ui.register

import androidx.lifecycle.ViewModel
import id.example.storyapp.repository.UserRepository

class RegisterViewModel(private val repository: UserRepository) : ViewModel() {

    fun registerUser(name: String, email: String, password: String) = repository.userRegister(name, email, password)
}
