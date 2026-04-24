package com.example.authenticationexample.presentation.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun CustomButton(text: String,
                 clickButton: () -> Unit,
                 modifier: Modifier = Modifier,
                 enabled: Boolean = true,
                 containerColour: Color = MaterialTheme.colorScheme.primary){
    Button(
        onClick = clickButton,
        modifier = modifier.semantics {
            contentDescription = "$text button"
        },
        enabled = enabled,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = containerColour)
    ) {
        Text(text = text)
    }
}
