package com.example.authenticationexample

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication
import org.mockito.MockedConstruction

class HiltTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, name: String?, context: Context?): Application
    {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}