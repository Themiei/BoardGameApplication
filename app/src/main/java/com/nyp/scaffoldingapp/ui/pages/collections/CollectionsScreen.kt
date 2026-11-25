package com.nyp.scaffoldingapp.ui.pages.collections

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nyp.scaffoldingapp.ui.theme.ScaffoldingTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import com.nyp.scaffoldingapp.data.model.CollectionType
import com.nyp.scaffoldingapp.data.model.GameCollection


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

            thumbnailStrip(summary.previewThumbnails)

            Column(modifier = Modifier.weight(1f)){
                Text(text = summary.collection.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis)

                Text(text = summary.gameCount.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Box(modifier = Modifier){

            }
        }
    }

}

@Composable
fun thumbnailStrip(urls : List<String>){
    if(urls.isEmpty())
        Box(modifier = Modifier
            .padding(12.dp)
            .background(MaterialTheme.colorScheme.onPrimaryContainer))
    else{
        Row(modifier = Modifier, horizontalArrangement = Arrangement.spacedBy(4.dp)){
            Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.surfaceVariant))
            Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.surfaceVariant))
            Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.surfaceVariant))
        }
    }
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


@Preview
@Composable
private fun CollectionCardPreview() {
    ScaffoldingTheme(darkTheme = false, dynamicColor = false) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            previewSummaries.forEach { summary ->
                CollectionCard(
                    summary = summary,
                    onClick = {},
                    onRename = {},
                    onDelete = {}
                )
            }
        }
    }
}