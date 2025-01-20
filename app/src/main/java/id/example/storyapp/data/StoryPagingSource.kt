package id.example.storyapp.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import id.example.storyapp.local.preference.UserPreference
import id.example.storyapp.model.ListStoryItem
import id.example.storyapp.api.ApiService
import kotlinx.coroutines.flow.first

@Suppress("unused")
class StoryPagingSource(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) : PagingSource<Int, ListStoryItem>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
        const val LOCATION = 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        val position = params.key ?: INITIAL_PAGE_INDEX
        val locationMode = params.key ?: LOCATION

        return try {
            val token = fetchToken()
            val response = fetchStories(token, position, params.loadSize, locationMode)

            if (response.isSuccessful) {
                processSuccessfulResponse(response.body()!!.listStory, position)
            } else {
                LoadResult.Error(Exception("Error: ${response.message()}"))
            }
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    private suspend fun fetchToken(): String {
        return userPreference.getSession().first().token
    }

    private suspend fun fetchStories(
        token: String,
        position: Int,
        loadSize: Int,
        locationMode: Int
    ) = apiService.getStories(token, position, loadSize, locationMode)

    private fun processSuccessfulResponse(
        responseData: List<ListStoryItem>,
        position: Int
    ): LoadResult.Page<Int, ListStoryItem> {
        return LoadResult.Page(
            data = responseData,
            prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
            nextKey = if (responseData.isEmpty()) null else position + 1
        )
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
