package com.example.authenticationexample.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

val BackgroundColourKey = SemanticsPropertyKey<Color>("BackgroundColour")
var SemanticsPropertyReceiver.backgroundColour by BackgroundColourKey
@Composable
fun ItemView(index: Int,
             item: String,
             selected: Boolean,
             onClick: (Int) -> Unit){
    // Determine colour of item by row and if selected
    val itemBackgroundColour = when {
        selected -> Color.Black
        index % 2 == 0 -> Color.LightGray
        else -> Color.Gray
    }

    Text(
        text = item,
        color = if (selected) Color.White else Color.Black,
        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
        modifier = Modifier.clickable { onClick.invoke(index) }
                            .background(itemBackgroundColour)
                            .padding(10.dp)
                            .semantics(mergeDescendants = true) { backgroundColour = itemBackgroundColour }
    )
}
