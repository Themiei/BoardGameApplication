package com.nyp.scaffoldingapp.ui.pages.collections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.nyp.scaffoldingapp.data.CollectionRepository
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import com.nyp.scaffoldingapp.BoardGameApp

class CollectionsViewModel private constructor(val repo : CollectionRepository) : ViewModel(){


    companion object {
        val Factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as BoardGameApp
                CollectionsViewModel(app.collectionRepository)
            }
        }
    }
}