package id.example.storyapp.utils

import androidx.test.espresso.idling.CountingIdlingResource

object EspressoIdlingResource {
    private const val RESOURCE_NAME = "GLOBAL_RESOURCE"
    private val idlingResource = CountingIdlingResource(RESOURCE_NAME)

    fun increment() {
        idlingResource.increment()
    }

    fun decrement() {
        if (!idlingResource.isIdleNow) {
            idlingResource.decrement()
        }
    }
}
