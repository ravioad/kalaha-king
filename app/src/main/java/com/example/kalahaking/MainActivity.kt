package com.example.kalahaking

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.kalahaking.ui.GameScreen
import com.example.kalahaking.ui.KalahaAI
import com.example.kalahaking.ui.theme.KalahaKingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        fullScreen(true)
        setContent {
            KalahaKingTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    GameScreen(
                        modifier = Modifier.padding(innerPadding),
                        ai = KalahaAI()
                    )
                }
            }
        }
    }
}

fun Activity.fullScreen(turnOn: Boolean = false) {
    val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
    if (turnOn) {
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        return
    }
    windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
}