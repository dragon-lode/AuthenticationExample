package com.example.authenticationexample.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomTextField(modifier: Modifier = Modifier,
                    hintText: String,
                    text: String,
                    isPasswordField: Boolean = false,
                    onValueChange: (String) -> Unit,
                    errorMessage: String,
                    errorPresent: Boolean,
                    minimumNumberOfLines: Int = 1,
                    maximumNumberOfLines: Int = 1
){
    val scrollState = rememberScrollState()

    Surface(modifier = Modifier.padding(10.dp)) {
        OutlinedTextField(
            value = text,
            onValueChange = onValueChange,
            isError = errorPresent,
            singleLine = false,
            minLines = minimumNumberOfLines,
            maxLines = maximumNumberOfLines,
            label = { Text(hintText) },
            visualTransformation =  if (isPasswordField) PasswordVisualTransformation('*')
                                    else VisualTransformation.None,
            modifier = modifier.fillMaxWidth(0.8f) //otherwise it expands width wise
                            .verticalScroll(rememberScrollState())
        )
        Text(
            modifier = Modifier.padding(10.dp),
            text =  if (errorPresent) errorMessage else "",
            fontSize = 14.sp,
            color = Color.Red,
        )
    }
}
