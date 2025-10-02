package com.example.android.gdgfinder.search

import android.location.Location
import com.example.android.gdgfinder.network.GdgApiService
import com.example.android.gdgfinder.network.GdgChapter
import com.example.android.gdgfinder.network.GdgResponse
import com.example.android.gdgfinder.network.LatLong
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

class GdgChapterRepository(gdgApiService: GdgApiService) {


    private val request = gdgApiService.getChapters()


    private var inProgressSort: Deferred<SortedData>? = null

    var isFullyInitialized = false
        private set



    suspend fun getChaptersForFilter(filter: String?): List<GdgChapter> {
        val data = sortedData()
        return when(filter) {
            null -> data.chapters
            else -> data.chaptersByRegion.getOrElse(filter) { emptyList() }
        }
    }


    suspend fun getFilters(): List<String> = sortedData().filters

    private suspend fun sortedData(): SortedData = withContext(Dispatchers.Main) {

        inProgressSort?.await() ?: doSortData()
    }

    private suspend fun doSortData(location: Location? = null): SortedData {
        // since we'll need to launch a new coroutine for the sorting use coroutineScope.
        // coroutineScope will automatically wait for anything started via async {} or await{} in it's block to
        // complete.
        val result = coroutineScope {
            // launch a new coroutine to do the sort (so other requests can wait for this sort to complete)
            val deferred = async { SortedData.from(request.await(), location) }
            // cache the Deferred so any future requests can wait for this sort
            inProgressSort = deferred
            // and return the result of this sort
            deferred.await()
        }
        return result
    }


    suspend fun onLocationChanged(location: Location) {
        // We need to ensure we're on Dispatchers.Main so that this is not running on multiple Dispatchers and we
        // modify the member inProgressSort.

        // Since this was called from viewModelScope, that will always be a simple if check (not expensive), but
        // by specifying the dispatcher we can protect against incorrect usage.
        withContext(Dispatchers.Main) {
            isFullyInitialized = true

            // cancel any in progress sorts, their result is not valid anymore.
            inProgressSort?.cancel()

            doSortData(location)
        }
    }

    private class SortedData private constructor(
        val chapters: List<GdgChapter>,
        val filters: List<String>,
        val chaptersByRegion: Map<String, List<GdgChapter>>
    ) {

        companion object {

            suspend fun from(response: GdgResponse, location: Location?): SortedData {
                return withContext(Dispatchers.Default) {
                    val chapters: List<GdgChapter> = response.chapters.sortByDistanceFrom(location)
                    val filters: List<String> = chapters.map { it.region } .distinctBy { it }
                    val chaptersByRegion: Map<String, List<GdgChapter>> = chapters.groupBy { it.region }
                    SortedData(chapters, filters, chaptersByRegion)
                }

            }



            private fun List<GdgChapter>.sortByDistanceFrom(currentLocation: Location?): List<GdgChapter> {
                currentLocation ?: return this

                return sortedBy { distanceBetween(it.geo, currentLocation)}
            }


            private fun distanceBetween(start: LatLong, currentLocation: Location): Float {
                val results = FloatArray(3)
                Location.distanceBetween(start.lat, start.long, currentLocation.latitude, currentLocation.longitude, results)
                return results[0]
            }
        }
    }
}