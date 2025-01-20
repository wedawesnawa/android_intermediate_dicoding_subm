@file:Suppress("SameReturnValue")

package id.example.storyapp.ui.main

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import id.example.storyapp.DataDummy
import id.example.storyapp.MainDispatcherRule
import id.example.storyapp.MainViewModel
import id.example.storyapp.local.preference.UserPreference
import id.example.storyapp.model.ListStoryItem
import id.example.storyapp.api.ApiService
import id.example.storyapp.repository.StoryRepository
import id.example.storyapp.repository.UserRepository
import id.example.storyapp.getOrAwaitValue
import id.example.storyapp.ui.MainAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest{
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Mock
    private lateinit var apiService: ApiService

    @Mock
    private lateinit var userPreference: UserPreference

    private lateinit var userRepository: UserRepository
    private lateinit var mockedLog: MockedStatic<Log>

    @Before
    fun setUp() {
        userRepository = UserRepository.getInstance(userPreference, apiService)

        mockedLog = Mockito.mockStatic(Log::class.java)
        mockedLog.`when`<Boolean> { Log.isLoggable(Mockito.anyString(), Mockito.anyInt()) }
            .thenReturn(false)
    }

    @Test
    fun `when get stories should not null and return data`() = runTest {
        val dummyStories = DataDummy.generateDummyStoryResponse()
        val data: PagingData<ListStoryItem> = StoryPagingSource.snapshot(dummyStories)
        val expectedStory = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStory.value = data
        Mockito.`when`(storyRepository.getAllStories()).thenReturn(expectedStory)

        val storyListViewModel = MainViewModel(storyRepository, userRepository)
        val actualStories: PagingData<ListStoryItem> = storyListViewModel.stories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = MainAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStories)

        assertNotNull(differ.snapshot())
        assertEquals(dummyStories.size, differ.snapshot().size)
        assertEquals(dummyStories[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest {
        val data: PagingData<ListStoryItem> = PagingData.from(emptyList())
        val expectedStory = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStory.value = data
        Mockito.`when`(storyRepository.getAllStories()).thenReturn(expectedStory)

        val storyListViewModel = MainViewModel(storyRepository, userRepository)
        val actualStory: PagingData<ListStoryItem> = storyListViewModel.stories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = MainAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        assertEquals(0, differ.snapshot().size)
    }

    @After
    fun tearDown() {
        mockedLog.close()
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}