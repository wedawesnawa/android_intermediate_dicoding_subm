package id.example.storyapp.ui.detail

import android.graphics.Color
import android.os.Bundle
import android.transition.Fade
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import id.example.storyapp.R
import id.example.storyapp.data.UiState
import id.example.storyapp.model.Story
import id.example.storyapp.databinding.ActivityDetailBinding
import id.example.storyapp.utils.formatDate
import id.example.storyapp.ui.ViewModelFactory

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val viewModel: DetailViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupWindowAnimations()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyId = intent.getStringExtra(EXTRA_STORY_ID)
        if (storyId.isNullOrEmpty()) {
            showErrorAndFinish()
            return
        }

        setupToolbar()
        observeStoryDetail(storyId)
    }

    private fun setupWindowAnimations() {
        val fade = Fade()
        fade.duration = 500
        window.enterTransition = fade
        window.exitTransition = fade
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = getString(R.string.tambah_cerita)
            setDisplayHomeAsUpEnabled(true)
        }
        binding.toolbar.navigationIcon?.setTint(Color.WHITE)
        ViewCompat.setElevation(binding.toolbar, 8f) // Animated elevation for toolbar
    }

    private fun observeStoryDetail(storyId: String) {
        viewModel.detailStory(storyId).observe(this) { state ->
            when (state) {
                is UiState.Loading -> toggleLoading(true)
                is UiState.Success -> {
                    toggleLoading(false)
                    displayStoryDetails(state.data!!)
                }
                is UiState.Error -> showSnackbar(state.error)
            }
        }
    }

    private fun toggleLoading(isLoading: Boolean) {
        with(binding) {
            val visibility = if (isLoading) View.GONE else View.VISIBLE
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            if (!isLoading) animateContentIn() // Animate content once loading finishes
        }
    }

    private fun displayStoryDetails(story: Story) {
        with(binding) {
            tvDetailName.text = story.name
            tvDetailDescription.text = story.description
            dateTextView.text = formatDate(story.createdAt ?: "null")
            Glide.with(this@DetailActivity)
                .load(story.photoUrl)
                .into(ivDetailPhoto)
        }
    }

    private fun animateContentIn() {
        // Animate ImageView
        binding.ivDetailPhoto.apply {
            alpha = 0f
            translationY = 50f
            animate().alpha(1f).translationY(0f).setDuration(500).start()
        }

        binding.tvDetailName.apply {
            alpha = 0f
            translationX = -50f
            animate().alpha(1f).translationX(0f).setDuration(500).start()
        }

        binding.dateTextView.apply {
            alpha = 0f
            translationX = 50f
            animate().alpha(1f).translationX(0f).setDuration(500).start()
        }

        binding.tvDetailDescription.apply {
            alpha = 0f
            translationY = 30f
            animate().alpha(1f).translationY(0f).setDuration(500).start()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finishAfterTransition()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showErrorAndFinish() {
        Toast.makeText(this, getString(R.string.story_id_not_found), Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    companion object {
        const val EXTRA_STORY_ID = "story_id"
    }
}
