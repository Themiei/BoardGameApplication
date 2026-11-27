package com.nyp.scaffoldingapp.ui.pages.collections

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nyp.scaffoldingapp.data.model.CollectionType
import com.nyp.scaffoldingapp.data.model.GameCollection
import com.nyp.scaffoldingapp.data.model.NameError
import com.nyp.scaffoldingapp.ui.theme.ScaffoldingTheme


@Composable
fun CollectionCard(summary: CollectionSummary,
                   onClick: () -> Unit,
                   onRename: () -> Unit,
                   onDelete: () -> Unit,
                   modifier: Modifier = Modifier){

    Card(onClick = onClick, modifier = modifier.fillMaxWidth()){
        Row(modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)){

            ThumbnailStrip(summary.previewThumbnails)

            Column(modifier = Modifier.weight(1f)){
                Text(text = summary.collection.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis)

                Text(text = numberDisplay(summary.gameCount),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            if (summary.collection.type == CollectionType.Custom) {
                CollectionMenu(
                    collectionName = summary.collection.name,
                    onRename = onRename,
                    onDelete = onDelete
                )
            }

        }
    }

}

@Composable
fun CollectionMenu(
    collectionName : String,
    onRename: () -> Unit,
    onDelete : () -> Unit
){
    var expanded by remember {
        mutableStateOf(false)
    }

    Box(modifier = Modifier){
        IconButton(onClick = {expanded = true}){
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Options for $collectionName"

            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {expanded = false}
        ){
            DropdownMenuItem(
                text = {Text("Rename")},
                leadingIcon = {Icon(Icons.Default.Edit, contentDescription = null)},
                onClick = {
                    expanded = false
                    onRename()
                }
            )
            DropdownMenuItem(
                text = {Text("Delete")},
                leadingIcon = {Icon(Icons.Default.Delete, contentDescription = null)},
                onClick = {
                    expanded = false
                    onDelete()
                }
            )
        }
    }
}
@Composable
fun ThumbnailStrip(urls : List<String>){
    if(urls.isEmpty())
        Box(Modifier.size(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.onSurfaceVariant))
    else{
        Row(modifier = Modifier,
            horizontalArrangement = Arrangement.spacedBy(4.dp)){

            urls.forEach()
            {
                Box(modifier = Modifier.size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant))
            }

        }
    }
}

private fun numberDisplay(number : Int) : String{
    return if(number <= 0) {
        "No Games"
    }else if(number > 1){
        "$number Games"
    }else{
        "$number Game"
    }

}

@Composable
private fun NameDialog(state :NameDialogState,
                       onTextChange : (String) -> Unit,
                       onConfirm : () -> Unit,
                       onDismiss : () -> Unit){

    val renaming = state.editingId != null
    val confirm = state.text.isNotBlank() && state.error == null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { if(renaming) Text("Renaming Collection") else Text("Creating Collection") },
        text = {
            OutlinedTextField(
                value = state.text,
                onValueChange = onTextChange,
                label = {Text("Name")},
                singleLine = true,
                isError = state.error != null,
                supportingText = {
                    if(state.error != null){
                        Text(nameErrorMessage(state.error))
                    }
                }
            )

        },

        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = confirm
            ){
                if(renaming)
                    Text("Rename")
                else
                    Text("Create")
            }
        },

        dismissButton = {
            TextButton(onClick = onDismiss){
                Text("Cancel")
            }
        }
    )
}

private fun nameErrorMessage(error : NameError) : String = when (error){
    NameError.BLANK -> "Name cannot be empty"
    NameError.TAKEN -> "Name has already been taken"
}
private val previewSummaries = listOf(

    CollectionSummary(
        collection = GameCollection(1, "Owned", CollectionType.Owned),
        gameCount = 12,
        previewThumbnails = listOf("thumb_a", "thumb_b", "thumb_c")
    ),

    CollectionSummary(
        collection = GameCollection(0, "Wishlist", CollectionType.Wishlist),
        gameCount = 2,
        previewThumbnails = listOf("thumb_d", "thumb_e")
    ),
    // Custom, exactly one game: checks "1 game" (singular)
    CollectionSummary(
        collection = GameCollection(2, "Party Games", CollectionType.Custom),
        gameCount = 1,
        previewThumbnails = listOf("thumb_f")
    ),
    // Custom, empty: placeholder icon and "No games yet"
    CollectionSummary(
        collection = GameCollection(3, "Co-op", CollectionType.Custom),
        gameCount = 0,
        previewThumbnails = emptyList()
    ),
    // Custom, very long name: checks the "…" ellipsis
    CollectionSummary(
        collection = GameCollection(4, "Games to play with the whole family at Christmas", CollectionType.Custom),
        gameCount = 27,
        previewThumbnails = listOf("thumb_g", "thumb_h", "thumb_i")
    )
)

@Composable
fun CollectionsList(
    summaries: List<CollectionSummary>,
    onOpen: (GameCollection) -> Unit,
    onRename: (GameCollection) -> Unit,
    onDelete: (GameCollection) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(summaries, key = { it.collection.id }) { summary ->
            CollectionCard(
                summary = summary,
                onClick = { onOpen(summary.collection) },
                onRename = { onRename(summary.collection) },
                onDelete = { onDelete(summary.collection) },
                modifier = Modifier.animateItem()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CollectionsListPreview() {
    ScaffoldingTheme(darkTheme = false, dynamicColor = false) {
        CollectionsList(
            summaries = previewSummaries,
            onOpen = {},
            onRename = {},
            onDelete = {}
        )
    }
}
@Composable
fun CollectionsScreen(modifier: Modifier = Modifier) {
    val viewModel: CollectionsViewModel = viewModel(factory = CollectionsViewModel.Factory)
    val collections by viewModel.collections.collectAsState()
    val dialog by viewModel.dialog.collectAsState()

    if (dialog.isOpen) {
        NameDialog(
            state = dialog,
            onTextChange = viewModel::onNameChanged,
            onConfirm = viewModel::confirmName,
            onDismiss = viewModel::dismissDialog
        )
    }

    CollectionsList(
        summaries = collections,
        onOpen = { },
        onRename = viewModel::openRenameDialog,
        onDelete = viewModel::requestDelete,
        modifier = modifier
    )
}