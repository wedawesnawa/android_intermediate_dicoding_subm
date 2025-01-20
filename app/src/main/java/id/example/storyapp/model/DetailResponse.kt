package id.example.storyapp.model

import com.google.gson.annotations.SerializedName

data class DetailResponse(

	@field:SerializedName("story")
	val story: Story? = null
)

