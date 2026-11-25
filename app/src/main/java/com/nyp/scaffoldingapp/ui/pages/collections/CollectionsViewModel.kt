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
import com.nyp.scaffoldingapp.data.model.NameError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

    private val _dialog = MutableStateFlow(NameDialogState())
    val dialog : StateFlow<NameDialogState> = _dialog.asStateFlow()

    private val _pendingDelete = MutableStateFlow<GameCollection?>(null)
    val pendingDelete = _pendingDelete.asStateFlow()

    fun openCreateDialog(){
        _dialog.update{
            it.copy(isOpen = true, editingId = null, text = "", error = null)
        }
    }

    fun openRenameDialog(collection : GameCollection){
        _dialog.update{
            it.copy(isOpen = true, editingId = collection.id, text = collection.name, error = null)
        }
    }

    fun onNameChanged(text : String){
        _dialog.update {
            current -> current.copy(
            text = text, error = repo.validateCollectionName(text, current.editingId)
            ) }
    }

    fun confirmName(){
        val state = _dialog.value
        if(state.editingId == null){
            val repoCall = repo.createCollection(state.text)
            if(repoCall != null)
                _dialog.update{current ->current.copy(error = repoCall)}
            else{
                dismissDialog()
            }
        }
        else{
            val repoCall = repo.renameCollection(state.editingId, state.text)
            if(!repoCall)
                _dialog.update{current -> current.copy(error = repo.validateCollectionName(current.text, current.editingId))}
            else{
                dismissDialog()
            }
        }

    }

    fun dismissDialog(){
        _dialog.update{NameDialogState()}
    }

    fun requestDelete(collection : GameCollection){
        _pendingDelete.update{collection}
    }

    fun confirmDelete(){
        val deleteId = _pendingDelete.value?.id
        if(deleteId != null)
            repo.deleteCollection(deleteId)
        _pendingDelete.update{null}
    }

    fun cancelDelete(){
        _pendingDelete.update{null}
    }
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

data class NameDialogState(val isOpen : Boolean = false,
                           val editingId : Int? = null,
                           val text: String = "",
                           val error : NameError? = null){

}