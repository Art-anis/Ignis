package com.nerazim.ignis

import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.nerazim.ignis.data.BoardViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            BoardViewModel()
        }
    }
}