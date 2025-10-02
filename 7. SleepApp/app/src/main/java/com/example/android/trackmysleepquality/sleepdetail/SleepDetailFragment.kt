import android.app.Application
import androidx.lifecycle.*
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.utils.formatNights
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SleepTrackerViewModel(
    private val database: SleepDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    private val tonight = MutableLiveData<SleepNight?>()
    val nights: LiveData<List<SleepNight>> = database.getAllNights()

    // Modern map extension from lifecycle-livedata-ktx
    val nightString: LiveData<String> = nights.map { list ->
        formatNights(list ?: emptyList(), application.resources)
    }

    val startButtonVisible: LiveData<Boolean> = tonight.map { it == null }
    val stopButtonVisible: LiveData<Boolean> = tonight.map { it != null }
    val clearButtonVisible: LiveData<Boolean> = nights.map { it.isNotEmpty() }

    private val _navigateToSleepQuality = MutableLiveData<SleepNight?>()
    val navigateToSleepQuality: LiveData<SleepNight?> get() = _navigateToSleepQuality

    private val _showSnackBarEvent = MutableLiveData<Boolean>()
    val showSnackBarEvent: LiveData<Boolean> get() = _showSnackBarEvent

    private val _navigateToSleepDataQuality = MutableLiveData<Long?>()
    val navigateToSleepDataQuality: LiveData<Long?> get() = _navigateToSleepDataQuality

    init {
        initializeTonight()
    }

    private fun initializeTonight() {
        viewModelScope.launch(Dispatchers.IO) {
            tonight.postValue(getTonightFromDatabase())
        }
    }

    private suspend fun getTonightFromDatabase(): SleepNight? {
        val night = database.getTonight()
        return if (night?.endTimeMilli != night?.startTimeMilli) null else night
    }

    fun onStartTracking() {
        viewModelScope.launch(Dispatchers.IO) {
            val newNight = SleepNight()
            database.insert(newNight)
            tonight.postValue(getTonightFromDatabase())
        }
    }

    fun onStopTracking() {
        viewModelScope.launch(Dispatchers.IO) {
            val oldNight = tonight.value ?: return@launch
            oldNight.endTimeMilli = System.currentTimeMillis()
            database.update(oldNight)
            _navigateToSleepQuality.postValue(oldNight)
        }
    }

    fun onClear() {
        viewModelScope.launch(Dispatchers.IO) {
            database.clear()
            tonight.postValue(null)
            _showSnackBarEvent.postValue(true)
        }
    }

    fun doneNavigating() {
        _navigateToSleepQuality.value = null
    }

    fun doneShowingSnackBar() {
        _showSnackBarEvent.value = false
    }

    fun onSleepNightClicked(nightId: Long) {
        _navigateToSleepDataQuality.value = nightId
    }

    fun onSleepDataQualityNavigated() {
        _navigateToSleepDataQuality.value = null
    }
}
