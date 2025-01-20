package id.example.storyapp

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import id.example.storyapp.ui.ViewModelFactory
import id.example.storyapp.ui.login.LoginActivity


class StartActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start_activity)

        val createAccountButton: Button = findViewById(R.id.create_account_button)
        val alreadyHaveAccountButton: Button = findViewById(R.id.already_have_account_button)
        val creditCardImage: ImageView = findViewById(R.id.credit_card)

        animateCreditCard(creditCardImage)

        alreadyHaveAccountButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        createAccountButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        checkSession()
    }

    private fun checkSession() {
        viewModel.getSession().observe(this) { user ->
            if (user != null && user.isLogin && user.token.isNotEmpty()) {
                navigateToMainActivity()
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun animateCreditCard(view: ImageView) {
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0.5f, 1f)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0.5f, 1f)

        scaleX.duration = 1000
        scaleY.duration = 1000
        scaleX.interpolator = DecelerateInterpolator()
        scaleY.interpolator = DecelerateInterpolator()

        scaleX.start()
        scaleY.start()
    }
}