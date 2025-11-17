package com.example.brewco.viewmodel
package com.example.brewco.viewmodel

import androidx.lifecycle.ViewModel

/**
 * Placeholder ViewModel for BookedScreen; will be expanded to full logic later.
 */
class OrderViewModel : ViewModel()
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Skeleton ViewModel for BookedScreen. Will be filled in next phases.
 */
class OrderViewModel : ViewModel() {
    private val _dummy = MutableStateFlow(true)
    val dummy: StateFlow<Boolean> = _dummy
}
