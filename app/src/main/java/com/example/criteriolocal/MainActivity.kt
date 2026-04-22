package com.example.criteriolocal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.criteriolocal.ui.app.CriterioLocalApp
import com.example.criteriolocal.ui.theme.CriterioLocalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appContainer = (application as CriterioLocalApplication).container

        setContent {
            CriterioLocalTheme {
                CriterioLocalApp(appContainer = appContainer)
            }
        }
    }
}
