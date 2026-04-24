package com.example.authenticationexample.data.ticket

import androidx.compose.ui.graphics.Color

enum class Status(val color: Color) {
    OPEN(Color.Blue),
    IN_PROGRESS(Color.Yellow),
    RESOLVED(Color.Green),
    CLOSED(Color.Gray);

    fun displayName(): String {
        return this.name.replace("_", " ")
                        .lowercase()
                        .replaceFirstChar { it.uppercase() }
    }
}