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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.kalahaking.ui.theme.KalahaKingTheme
import com.example.kalahaking.ui.theme.primaryLightMediumContrast
import com.example.kalahaking.ui.theme.secondaryLightMediumContrast

@Composable
fun GameScreen(modifier: Modifier = Modifier) {

    val context = LocalContext.current

    val boardState = remember {
        mutableStateListOf(
            0, 0, 0, 0, 0, 0, //Player 1, pits
            0, //Player 1, Kalaha (Store)
            0, 0, 0, 0, 0, 0, //Player 2, pits
            0, //Player 2, Kalaha (Store)
        )
    } // Observe changes in the board
    var currentPlayerState by remember { mutableIntStateOf(1) }// Observe changes in current player
    var messageState by remember { mutableStateOf("Welcome to Kalaha!") }// Observe changes in messages

    fun initializeGame() {
        for (i in 0..5) {
            boardState[i] = 4 // Player 1 pits (indices 0-5)
        }
        for (i in 7..12) {
            boardState[i] = 4 // Player 2 pits (indices 7-12)
        }
        currentPlayerState = 2
        messageState = "Player 1's Turn"
    }

    fun makeMove(player: Int, startPitIndex: Int): Boolean {
        val kalahaIndex: Int
        val opponentKalahaIndex: Int
        val playerPitsRange: IntRange
        if (player == 1) {
            kalahaIndex = 6
            opponentKalahaIndex = 13
            playerPitsRange = 0..5
        } else {
            kalahaIndex = 13
            opponentKalahaIndex = 6
            playerPitsRange = 7..12
        }

        var seedsToSow = boardState[startPitIndex]
        boardState[startPitIndex] = 0

        var currentPitIndex = startPitIndex
        while (seedsToSow > 0) {
            currentPitIndex = (currentPitIndex + 1) % 14

            if (currentPitIndex == opponentKalahaIndex) {
                continue
            }

            boardState[currentPitIndex] += 1
            seedsToSow -= 1
        }

        val lastPitLanded = currentPitIndex

        //(IF the last seed ends up in an empty pit, player takes all the seeds from the opposite pit (+1 from the last pit)

        // Capture Rule
        if (player == 1 && lastPitLanded in playerPitsRange && boardState[lastPitLanded] == 1) {
            val oppositePitIndex = 12 - lastPitLanded // Calculate opposite pit index for Player 1
            if (boardState[oppositePitIndex] > 0) {
                val capturedSeeds = boardState[lastPitLanded] + boardState[oppositePitIndex]
                boardState[kalahaIndex] += capturedSeeds
                boardState[lastPitLanded] = 0
                boardState[oppositePitIndex] = 0
            }
        } else if (player == 2 && lastPitLanded in playerPitsRange && boardState[lastPitLanded] == 1) {
            val oppositePitIndex = 12 - lastPitLanded // Calculate opposite pit index for Player 2
            if (boardState[oppositePitIndex] > 0) {
                val capturedSeeds = boardState[lastPitLanded] + boardState[oppositePitIndex]
                boardState[kalahaIndex] += capturedSeeds
                boardState[lastPitLanded] = 0
                boardState[oppositePitIndex] = 0
            }
        }

        return lastPitLanded == kalahaIndex // Free turn if last seed in Kalaha
    }

    fun calculateScore(): String {
        var player1Score = boardState[6] // Player 1 Kalaha is at index 6
        var player2Score = boardState[13] // Player 2 Kalaha is at index 13

        for (i in 0..5) { // Player 1 pits 0-5
            player1Score += boardState[i]
            boardState[i] = 0
        }
        for (i in 7..12) { // Player 2 pits 7-12
            player2Score += boardState[i]
            boardState[i] = 0
        }

        val winnerMessage = when {
            player1Score > player2Score -> "Player 1 wins!"
            player2Score > player1Score -> "Player 2 wins!"
            else -> "It's a tie!"
        }
        return winnerMessage
    }


    fun isGameOver(): Boolean {
        val player1PitsEmpty = (0..5).all { boardState[it] == 0 }
        val player2PitsEmpty = (7..12).all { boardState[it] == 0 }
        return player1PitsEmpty || player2PitsEmpty
    }

    fun onPitClick(player: Int, startPitIndex: Int) { // Modified onPitClick for Compose State
        if (player != currentPlayerState) {
            messageState = "Not your turn!"
            context.shortToast(messageState)
            return
        }

        if (boardState[startPitIndex] == 0) {
            messageState = "This pit is empty. Choose another pit."
            context.shortToast(messageState)
            return
        }

        messageState = "" // Clear message

        val freeTurn = makeMove(player, startPitIndex)
        if (isGameOver()) {
            val winnerMessage = calculateScore()
            messageState = "Game Over! $winnerMessage" // Show game over message with winner
            context.shortToast(messageState)
            return
        }

        if (!freeTurn) {
            currentPlayerState = 3 - currentPlayerState // Switch player
//            if (currentPlayerState == 2) { // AI's turn after player switch
//                aiPlayerMove() // AI move function
//            }
        }
    }

    LaunchedEffect(Unit) {
        initializeGame()
    }

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
            totalScore = boardState[13],
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
                pits = boardState.slice(7..12).reversed(),
                isSecondPlayer = true,
                onPitClick = { pitIndex ->
                    onPitClick(2, pitIndex)
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
            UserPits(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                color = secondaryLightMediumContrast,
                pits = boardState.slice(0..5),
                onPitClick = { pitIndex ->
                    onPitClick(1, pitIndex)
                }
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
            totalScore = boardState[6],
        )

    }
}

@Composable
fun UserPits(
    modifier: Modifier = Modifier,
    color: Color,
    pits: List<Int>,
    isSecondPlayer: Boolean = false,
    onPitClick: (Int) -> Unit
) {
    Row(modifier = modifier) {
        pits.forEachIndexed { index, i ->
            Pit(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .rotate(if (isSecondPlayer) 180f else 0f),
                num = i,
                color = color,
                onClick = {
                    val pitIndex = if (isSecondPlayer) 12 - index else index
                    onPitClick(pitIndex)
                }
            )
            if (index != pits.lastIndex) {
                Spacer(modifier = Modifier.width(20.dp))
            }
        }
    }
}

@Composable
fun Pit(modifier: Modifier = Modifier, num: Int, color: Color, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color)
            .customClickable(onClick = onClick, bounded = true), contentAlignment = Alignment.Center
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
            .rotate(if (isSecondPlayer) 180f else 0f),
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
