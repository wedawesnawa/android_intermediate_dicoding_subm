package id.example.storyapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.animation.DecelerateInterpolator
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.paging.LoadState
import id.example.storyapp.databinding.ActivityMainBinding
import id.example.storyapp.ui.LoadingStateAdapter
import id.example.storyapp.ui.MainAdapter
import id.example.storyapp.ui.ViewModelFactory
import id.example.storyapp.ui.addStory.AddStoryActivity
import id.example.storyapp.ui.login.LoginActivity
import id.example.storyapp.ui.maps.MapsActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var adapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkSession()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
        runEntryAnimations()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.cerita)
    }

    private fun checkSession() {
        viewModel.getSession().observe(this) { user ->
            when (user.isLogin && user.token.isNotEmpty()) {
                false -> {
                    startActivity(Intent(this@MainActivity, StartActivity::class.java))
                    finish()
                }
                true -> {
                    setupAdapter()
                    binding.fab.setOnClickListener {
                        moveToAddStory()
                    }
                }
            }
        }
    }

    private fun setupAdapter() {
        adapter = MainAdapter(this)
        viewModel.stories.observe(this) { pagingData ->
            adapter.submitData(lifecycle, pagingData)
        }
        binding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter { adapter.retry() }
        )
        adapter.addLoadStateListener { loadState ->
            binding.progressBar.isVisible = loadState.source.refresh is LoadState.Loading
            binding.tvNoData.isVisible = loadState.source.refresh is LoadState.NotLoading &&
                    adapter.itemCount == 0
        }
    }

    private fun moveToAddStory() {
        val intent = Intent(this, AddStoryActivity::class.java)
        startActivity(intent)
    }

    private fun moveToMaps() {
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.story_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                viewModel.logout()
                true
            }

            R.id.maps -> {
                moveToMaps()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun runEntryAnimations() {
        binding.toolbar.alpha = 0f
        binding.toolbar.translationY = -50f
        binding.toolbar.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(600)
            .setInterpolator(DecelerateInterpolator())
            .start()

        binding.rvStory.translationY = 50f
        binding.rvStory.alpha = 0f
        binding.rvStory.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(800)
            .setInterpolator(DecelerateInterpolator())
            .setStartDelay(200)
            .start()

        binding.fab.scaleX = 0f
        binding.fab.scaleY = 0f
        binding.fab.rotation = 90f
        binding.fab.animate()
            .scaleX(1f)
            .scaleY(1f)
            .rotation(0f)
            .setDuration(600)
            .setInterpolator(DecelerateInterpolator())
            .setStartDelay(400)
            .start()
    }
}
