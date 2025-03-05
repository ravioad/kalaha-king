package com.example.kalahaking.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.kalahaking.ui.theme.KalahaKingTheme
import com.example.kalahaking.ui.theme.primaryLight
import com.example.kalahaking.ui.theme.primaryLightMediumContrast
import com.example.kalahaking.ui.theme.secondaryLight
import com.example.kalahaking.ui.theme.secondaryLightMediumContrast

@Composable
fun GameScreen(modifier: Modifier = Modifier) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
    ) {
        val startGuideline = createGuidelineFromStart(0.18f)
        val endGuideline = createGuidelineFromEnd(0.18f)
        val (user1Kalaha, user2Kalaha, pitsLayout) = createRefs()
        UserKalaha(
            modifier = Modifier.constrainAs(user2Kalaha) {
                start.linkTo(parent.start, margin = 36.dp)
                end.linkTo(startGuideline)
                top.linkTo(parent.top, margin = 30.dp)
                bottom.linkTo(parent.bottom, margin = 30.dp)
                height = Dimension.fillToConstraints
                width = Dimension.fillToConstraints
            },
            color = primaryLightMediumContrast,
            totalScore = 55,
            isSecondPlayer = true
        )
        Column(modifier = Modifier
            .constrainAs(pitsLayout) {
                start.linkTo(startGuideline, margin = 20.dp)
                end.linkTo(endGuideline, margin = 20.dp)
                top.linkTo(parent.top, margin = 30.dp)
                bottom.linkTo(parent.bottom, margin = 30.dp)
                height = Dimension.fillToConstraints
                width = Dimension.fillToConstraints
            }
        ) {
            UserPits(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                color = primaryLightMediumContrast,
                pits = listOf(1, 2, 3, 4, 5, 6).reversed(),
                isSecondPlayer = true
            )
            Spacer(modifier = Modifier.height(20.dp))
            UserPits(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                color = secondaryLightMediumContrast,
                pits = listOf(1, 2, 3, 4, 5, 6)
            )
        }
        UserKalaha(
            modifier = Modifier.constrainAs(user1Kalaha) {
                start.linkTo(endGuideline)
                end.linkTo(parent.end, margin = 36.dp)
                top.linkTo(parent.top, margin = 30.dp)
                bottom.linkTo(parent.bottom, margin = 30.dp)
                height = Dimension.fillToConstraints
                width = Dimension.fillToConstraints
            },
            color = secondaryLightMediumContrast,
            totalScore = 34
        )

    }
}

@Composable
fun UserPits(
    modifier: Modifier = Modifier, color: Color, pits: List<Int>,
    isSecondPlayer: Boolean = false
) {
    Row(modifier = modifier) {
        pits.forEachIndexed { index, i ->
            Pit(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .rotate(if (isSecondPlayer) 180f else 0f),
                i,
                color
            )
            if (index != pits.lastIndex) {
                Spacer(modifier = Modifier.width(20.dp))
            }
        }
    }
}

@Composable
fun Pit(modifier: Modifier = Modifier, num: Int, color: Color) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color), contentAlignment = Alignment.Center
    ) {
        Text(text = num.toString(), fontSize = 20.sp, color = Color.White)
    }
}

@Composable
fun UserKalaha(
    modifier: Modifier = Modifier,
    color: Color,
    totalScore: Int,
    isSecondPlayer: Boolean = false
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color)
            .rotate(if(isSecondPlayer) 180f else 0f),
        contentAlignment = Alignment.Center
    ) {
        Text(text = totalScore.toString(), fontSize = 30.sp, color = Color.White)
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KalahaKingTheme {
        GameScreen()
    }
}
