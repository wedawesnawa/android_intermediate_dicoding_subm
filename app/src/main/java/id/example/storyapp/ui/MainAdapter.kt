package id.example.storyapp.ui

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.example.storyapp.model.ListStoryItem
import id.example.storyapp.databinding.ItemCardStoryBinding
import id.example.storyapp.ui.detail.DetailActivity

class MainAdapter(private val context: Context) :
    PagingDataAdapter<ListStoryItem, MainAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemCardStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    inner class StoryViewHolder(private val binding: ItemCardStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(story: ListStoryItem) {
            binding.apply {
                tvItemName.text = story.name
                tvItemDescription.text = story.description
                Glide.with(context)
                    .load(story.photoUrl)
                    .centerCrop()
                    .into(ivItemPhoto)
                setupClickListener(story)
            }
        }

        private fun setupClickListener(story: ListStoryItem) {
            itemView.setOnClickListener {
                navigateToDetailActivity(story.id)
            }
        }

        private fun navigateToDetailActivity(storyId: String) {
            val intent = Intent(context, DetailActivity::class.java).apply {
                putExtra(EXTRA_STORY_ID, storyId)
            }
            Log.d("TokenTesting", "Data story id: $storyId")
            context.startActivity(intent)
        }
    }

    companion object {
        const val EXTRA_STORY_ID = "story_id"

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
