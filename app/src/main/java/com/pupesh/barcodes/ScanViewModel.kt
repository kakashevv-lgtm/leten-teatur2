package com.pupesh.barcodes

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ScanViewModel : ViewModel() {
    private val seen = linkedSetOf<String>()
    private val _items = MutableStateFlow<List<String>>(emptyList())
    val items = _items.asStateFlow()

    fun onBarcode(code: String): Boolean {
        val dup = !seen.add(code)
        if (!dup) _items.value = seen.toList()
        return dup
    }

    fun clear() {
        seen.clear()
        _items.value = emptyList()
    }
}
