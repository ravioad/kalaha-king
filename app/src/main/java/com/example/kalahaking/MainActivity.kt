package com.example.kalahaking

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.kalahaking.ui.GameScreen
import com.example.kalahaking.ui.HelperAI
import com.example.kalahaking.ui.HeuristicHelper
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
                        ai = HelperAI(HeuristicHelper())
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