
package com.example.android.trackmysleepquality.sleepquality

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.trackmysleepquality.database.SleepDatabaseDao

@Suppress("UNCHECKED_CAST")
class SleepQualityViewModelFactory (
        private val sleepNightKey: Long,
        private val dataSource: SleepDatabaseDao): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SleepQualidadViewModel::class.java)) {
            return SleepQualidadViewModel(sleepNightKey,dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
