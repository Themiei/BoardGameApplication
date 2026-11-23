package com.nyp.scaffoldingapp.ui.pages

import android.media.Image
import android.text.style.BackgroundColorSpan
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nyp.scaffoldingapp.ui.theme.ScaffoldingTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import java.util.Date


data class GameCollection(val id : Int, val name : String, val status : CollectionStatus)

data class GameRef(val id : Int, val name : String, val thumbnail : Image, val thumbNailURL : String)

data class Membership(val collectionID : Int, val gameID : Int, val dateAdded : Long)
enum class CollectionStatus{
    Owned,
    Wishlist,
    Custom
}
@Composable
fun Page4(modifier: Modifier = Modifier) {
   Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(24.dp), verticalArrangement = Arrangement.Top,
       horizontalAlignment = Alignment.CenterHorizontally)
   {
        
   }
}

@Preview
@Composable
fun PreviewCollectionsPage()
{
    ScaffoldingTheme(false)
    {
        Page4();
    }
}