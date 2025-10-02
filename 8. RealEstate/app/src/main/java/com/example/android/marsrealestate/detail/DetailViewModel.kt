
package com.example.android.marsrealestate.detail

import android.app.Application
import com.example.android.marsrealestate.R
import androidx.lifecycle.*
import com.example.android.marsrealestate.network.MarsProperty


class DetailViewModel(marsProperty: MarsProperty, app: Application) : AndroidViewModel(app) {

    private val _selectedProperty = MutableLiveData<MarsProperty>()
    val selectedProperty: LiveData<MarsProperty> = _selectedProperty

    init {
        _selectedProperty.value = marsProperty
    }

    val displayPropertyPrice: LiveData<String> = _selectedProperty.map { property ->
        property?.let {
            app.getString(
                if (it.isRental) R.string.display_price_monthly_rental else R.string.display_price,
                it.price
            )
        } ?: ""
    }

    val displayPropertyType: LiveData<String> = _selectedProperty.map { property ->
        property?.let {
            app.getString(
                R.string.display_type,
                app.getString(if (it.isRental) R.string.type_rent else R.string.type_sale)
            )
        } ?: ""
    }
}
