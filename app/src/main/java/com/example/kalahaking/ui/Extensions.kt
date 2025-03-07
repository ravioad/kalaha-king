package com.example.kalahaking.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ripple
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
