package com.nyp.scaffoldingapp.ui.pages

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


/**
 * Page 4 — the game launcher.
 *
 * The button is deliberately NOT wired up. Connecting it to a LibGDX
 * Activity is Assignment 2, and it is taught in the Week 9 session:
 *
 *   - launching an Activity for a result
 *   - passing a value back, and what happens when there isn't one
 *   - the lifecycle trap that leaks on the second launch
 *
 * See the Week 9 slides. Do not guess at it from here.
 */
@Composable
fun Page4(modifier: Modifier = Modifier) {
   Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(24.dp), verticalArrangement = Arrangement.Top,
       horizontalAlignment = Alignment.CenterHorizontally)
   {
        Text("Test",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            )
       Spacer(modifier.size(8.dp))
       HorizontalDivider(modifier = Modifier, 4.dp)
       LazyColumn()
       {

           item{
               LazyRow(
                   contentPadding = PaddingValues(horizontal = 16.dp),
                   horizontalArrangement = Arrangement.spacedBy(12.dp)) {


               }
           }
       }
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