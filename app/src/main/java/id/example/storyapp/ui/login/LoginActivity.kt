package id.example.storyapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import id.example.storyapp.R
import id.example.storyapp.databinding.ActivityLoginBinding
import id.example.storyapp.data.UiState
import id.example.storyapp.model.UserModel
import id.example.storyapp.ui.ViewModelFactory
import id.example.storyapp.MainActivity
import id.example.storyapp.StartActivity
import id.example.storyapp.ui.register.RegisterActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBinding()

        binding.backButton.setOnClickListener {
            val intent = Intent(this, StartActivity::class.java)
            startActivity(intent)
        }
    }


    private fun setupBinding() {
        with(binding) {
            edLoginEmail.setParentLayout(emailInput)
            edLoginPassword.setParentLayout(passwordInput)
            btnRegister.setOnClickListener {
                moveToRegister()
            }
            btnLogin.setOnClickListener {
                setupLogin()
            }
        }
    }

    private fun setupLogin() {
        val email = binding.edLoginEmail.text.toString()
        val password = binding.edLoginPassword.text.toString()
        viewModel.login(email, password).observeForever {
            when (it) {
                is UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    showSnackbar(getString(R.string.login_succes))
                    val user = it.data.loginResult
                    viewModel.saveSession(UserModel(user?.name ?: "", user?.token ?: "", true))
                    moveToMain()
                }
                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    showSnackbar(it.error)
                }
            }
        }
    }

    private fun moveToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun moveToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}
