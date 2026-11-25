package com.nyp.scaffoldingapp.ui.pages.collections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.nyp.scaffoldingapp.data.CollectionRepository
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import com.nyp.scaffoldingapp.BoardGameApp
import com.nyp.scaffoldingapp.data.model.GameCollection
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import androidx.lifecycle.viewModelScope
import com.nyp.scaffoldingapp.data.gamesIn
import kotlin.collections.emptyList

class CollectionsViewModel private constructor(private val repo : CollectionRepository) : ViewModel(){

    val collections : StateFlow<List<CollectionSummary>> = repo.data.map{snapshot ->
        snapshot.collection.map{
            col ->
            val gamesIn = snapshot.gamesIn(col.id)
            val thumbnailList : List<String> = gamesIn.take(3).flatMap{ listOf(it.thumbNailURL) }
            CollectionSummary(col, gamesIn.count(), thumbnailList)
        }.sortedBy{it.collection.type}}
        .stateIn(viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList())

    companion object {
        val Factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as BoardGameApp
                CollectionsViewModel(app.collectionRepository)
            }
        }
    }
}

data class CollectionSummary(val collection : GameCollection,
                             val gameCount : Int,
                             val previewThumbnails : List<String>) {

}