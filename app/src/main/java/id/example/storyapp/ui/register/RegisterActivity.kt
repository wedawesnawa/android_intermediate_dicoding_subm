package id.example.storyapp.ui.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import id.example.storyapp.R
import id.example.storyapp.StartActivity
import id.example.storyapp.data.UiState
import id.example.storyapp.databinding.ActivityRegisterBinding
import id.example.storyapp.ui.ViewModelFactory
import id.example.storyapp.ui.login.LoginActivity


class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBinding()

        binding.backButton.setOnClickListener {
            val intent = Intent(this, StartActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupBinding() {
        with(binding) {
            edRegisterEmail.setParentLayout(emailInput)
            edRegisterPassword.setParentLayout(passwordInput)
            btnLogin.setOnClickListener {
                moveToLogin()
            }
            btnRegister.setOnClickListener {
                setupRegister()
            }
        }
    }

    private fun setupRegister() {
        val name = binding.edRegisterName.text.toString()
        val email = binding.edRegisterEmail.text.toString()
        val password = binding.edRegisterPassword.text.toString()

        viewModel.registerUser(name, email, password).observeForever {
            when (it) {
                is UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    showSnackbar(getString(R.string.register_succes))
                    moveToLogin()
                }
                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    showSnackbar(it.error)
                }
            }
        }
    }

    private fun moveToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}