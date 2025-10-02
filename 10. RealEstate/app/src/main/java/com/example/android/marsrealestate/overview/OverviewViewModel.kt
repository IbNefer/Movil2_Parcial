
package com.example.android.marsrealestate.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.marsrealestate.network.MarsApi
import com.example.android.marsrealestate.network.MarsApiFilter
import com.example.android.marsrealestate.network.MarsProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

enum class MarsApiStatus{ LOADING, ERROR, DONE}

class OverviewViewModel : ViewModel() {

    private val _status = MutableLiveData<MarsApiStatus>()

    val status: LiveData<MarsApiStatus>
        get() = _status

    private val _properties = MutableLiveData<List<MarsProperty>>()

    val properties: LiveData<List<MarsProperty>>
    get() = _properties

    private val _navigateToSelectedProperty = MutableLiveData<MarsProperty>()
    val navigateToSelectedProperty: LiveData<MarsProperty>
    get() = _navigateToSelectedProperty

    val coroutineJob = Job()
    val coroutineScope = CoroutineScope(Dispatchers.Main + coroutineJob)


    init {
        getMarsRealEstateProperties(MarsApiFilter.SHOW_ALL)
    }


    private fun getMarsRealEstateProperties(filter: MarsApiFilter) {
      coroutineScope.launch {
          val getPropertiesDeferred = MarsApi.retrofitService.getProperties(filter.value)
          _status.value = MarsApiStatus.LOADING
          try {
              if (getPropertiesDeferred.size > 0){
                  _status.value = MarsApiStatus.DONE
                  _properties.value = getPropertiesDeferred
              }
          }catch (t: Throwable){
              _status.value = MarsApiStatus.ERROR
              _properties.value = ArrayList()
          }
      }
    }

    fun displayPropertyDetails(marsProperty: MarsProperty){
        _navigateToSelectedProperty.value = marsProperty
    }

    fun displayPropertiesDetailComplete(){
        _navigateToSelectedProperty.value = null
    }

    fun updateFilter(filter: MarsApiFilter){
        getMarsRealEstateProperties(filter)
    }

    override fun onCleared() {
        super.onCleared()
        coroutineJob.cancel()
    }
}
