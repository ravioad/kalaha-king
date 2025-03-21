package com.example.kalahaking.ui

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

fun Modifier.customClickable(
    onClick: () -> Unit,
    isActive: Boolean = true,
    noIndication: Boolean = false,
    bounded: Boolean = false,
) = composed {
    clickable(
        onClick = onClick,
        indication = if (isActive && !noIndication) ripple(bounded = bounded) else null,
        interactionSource = remember { MutableInteractionSource() },
        enabled = isActive,
        onClickLabel = null,
        role = null,
    )
}

fun Context.shortToast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_LONG).show()
}

@Composable
fun SimpleAnimatedVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier,
    enter: EnterTransition = fadeIn(),
    exit: ExitTransition = fadeOut(),
    label: String = "AnimatedVisibility",
    content: @Composable() AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(visible, modifier, enter, exit, label, content)
}


fun afterDelayInSeconds(seconds: Int, block: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed(block, (seconds.toLong() * 1000))
}

fun String.printLog(text: Any?) {
    Log.e(this, if (text !is String) text.toString() else text)
}