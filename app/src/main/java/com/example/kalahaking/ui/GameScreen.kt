package com.example.kalahaking.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.kalahaking.ui.theme.primaryLightMediumContrast
import com.example.kalahaking.ui.theme.secondaryLightMediumContrast
import com.example.kalahaking.ui.theme.tertiaryLightMediumContrast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun GameScreen(modifier: Modifier = Modifier, ai: HelperAI) {
//    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val boardState = remember {
        mutableStateListOf(
            0, 0, 0, 0, 0, 0, //Player 1, pits
            0, //Player 1, Kalaha (Store)
            0, 0, 0, 0, 0, 0, //Player 2, pits
            0, //Player 2, Kalaha (Store)
        )
    } // Observe changes in the board
    var startGame by remember { mutableStateOf(false) }// Observe changes in current player
    var gameOver by remember { mutableStateOf(false) }// Observe changes in current player
    var isFreeTurn by remember { mutableStateOf(false) }// Observe changes in current player
    var currentPlayerState by remember { mutableIntStateOf(1) }// Observe changes in current player
    var winnerPlayer by remember { mutableIntStateOf(0) }// Observe changes in current player
    var player1Message by remember { mutableStateOf("Welcome to Kalaha!") }// Observe changes in messages
    var player2Message by remember { mutableStateOf("Welcome to Kalaha!") }// Observe changes in messages

    fun showMessage(player: Int, message: String = "", isGameOver: Boolean = false) {
        if (isGameOver) {
            player1Message = when (player) {
                1 -> "You won!"
                2 -> "You lost!"
                else -> "It's a tie!"
            }
            player2Message = when (player) {
                1 -> "You lost!"
                2 -> "You won!"
                else -> "It's a tie!"
            }
            gameOver = true
            afterDelayInSeconds(3) {
                player1Message = ""
                player2Message = ""
            }
            return
        }
        if (player == 1) {
            player1Message =
                if (isFreeTurn) "You get another turn!" else message.ifBlank { "It's your turn!" }
            player2Message = ""
            return
        }
        player2Message =
            if (isFreeTurn) "You get another turn!" else message.ifBlank { "It's your turn!" }
        player1Message = ""
    }

    fun showAlert(player: Int, message: String) {
        showMessage(player, message)
        afterDelayInSeconds(3) {
            showMessage(currentPlayerState)
        }
    }

    fun resetStores() {
        boardState[6] = 0
        boardState[13] = 0
    }

    fun setPits() {
        for (i in 0..5) {
            boardState[i] = 4 // Player 1 pits (indices 0-5)
        }
        for (i in 7..12) {
            boardState[i] = 4 // Player 2 pits (indices 7-12)
        }
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

    fun calculateScore(): Pair<String, Int> {
        var player1Surplus = 0
        var player2Surplus = 0
        var player1Score = boardState[6] // Player 1 Kalaha is at index 6
        var player2Score = boardState[13] // Player 2 Kalaha is at index 13

        for (i in 0..5) { // Player 1 pits 0-5
            player1Surplus += boardState[i]
            player1Score += boardState[i]
        }
        for (i in 7..12) { // Player 2 pits 7-12
            player2Surplus += boardState[i]
            player2Score += boardState[i]
        }

        val winnerMessage = when {
            player1Score > player2Score -> "Player 1 wins!"
            player2Score > player1Score -> "Player 2 wins!"
            else -> "It's a tie!"
        }
        Log.e("calculateScoreSurplus", "p1: $player1Surplus, p2: $player2Surplus")
        return Pair(
            winnerMessage,
            when {
                player1Score > player2Score -> 1
                player2Score > player1Score -> 2
                else -> 0
            }
        )
    }

    fun isGameOver(): Boolean {
        val player1PitsEmpty = (0..5).all { boardState[it] == 0 }
        val player2PitsEmpty = (7..12).all { boardState[it] == 0 }
        return player1PitsEmpty || player2PitsEmpty
    }

    fun makeAIMove(onPlayerMove: (Int) -> Unit) {
        afterDelayInSeconds(3) {
            val bestMove = ai.getBestMoveWithMinimax(boardState)
            bestMove?.let { onPlayerMove(it) }
        }
    }

    fun onPitClick(player: Int, startPitIndex: Int) { // Modified onPitClick for Compose State
        if (!startGame) return
        if (player != currentPlayerState) {
            showAlert(player, "Not your turn!")
            return
        }

        if (boardState[startPitIndex] == 0) {
            showAlert(player, "This pit is empty. Choose another pit.")
            return
        }

        isFreeTurn = makeMove(player, startPitIndex)
        if (isGameOver()) {
            val winnerMessage = calculateScore()
            winnerPlayer = winnerMessage.second
            showMessage(winnerMessage.second, winnerMessage.first, isGameOver = true)
            return
        }
        if (isFreeTurn) {
            showMessage(currentPlayerState)
            if (currentPlayerState == 2) {
                makeAIMove(onPlayerMove = {
                    onPitClick(2, it)
                })
            }
        }
        if (!isFreeTurn) {
            currentPlayerState = 3 - currentPlayerState // Switch player
            showMessage(currentPlayerState)
            if (currentPlayerState == 2) { // AI's turn after player switch
                makeAIMove(onPlayerMove = {
                    onPitClick(2, it)
                })
            }
        }
    }


    suspend fun initializeGame() {
        if (gameOver) {
            winnerPlayer = 0
            gameOver = false
            isFreeTurn = false
            resetStores()
            player1Message = "Welcome to Kalaha!"
            player2Message = "Welcome to Kalaha!"
        }
        setPits()
        val randomPlayer = Random.nextInt(2) + 1 // Generates 1 or 2 randomly
        "initializeGamePlayer".printLog(randomPlayer)
        currentPlayerState = randomPlayer
        delay(3000)
        startGame = true
        showMessage(currentPlayerState)
        if(currentPlayerState == 2){ //If the player 2's is to make the first turn.
            makeAIMove(onPlayerMove = {
                onPitClick(2, it)
            })
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
            isSecondPlayer = true,
            winnerPlayer = winnerPlayer
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
                    .weight(0.35f)
                    .fillMaxWidth(),
                color = primaryLightMediumContrast,
                pits = boardState.slice(7..12).reversed(),
                isSecondPlayer = true,
                onPitClick = { pitIndex ->
                    onPitClick(2, pitIndex)
                }
            )
            Spacer(modifier = Modifier.weight(0.05f))
            DetailsCard(
                modifier = Modifier
                    .weight(0.2f)
                    .fillMaxWidth(),
                player1Message = player1Message,
                player2Message = player2Message,
                gameOver = gameOver,
                onRestartGame = {
                    coroutineScope.launch {
                        initializeGame()
                    }
                }
            )
            Spacer(modifier = Modifier.weight(0.05f))
            UserPits(
                modifier = Modifier
                    .weight(0.35f)
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
            winnerPlayer = winnerPlayer
        )

    }
}

@Composable
fun DetailsCard(
    modifier: Modifier = Modifier,
    player1Message: String,
    player2Message: String,
    gameOver: Boolean,
    onRestartGame: () -> Unit
) {
    ConstraintLayout(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(tertiaryLightMediumContrast)
            .customClickable(onClick = onRestartGame, isActive = gameOver, bounded = true),
    ) {
        val (message1Ref, message2Ref, gameOverRef) = createRefs()
        SimpleAnimatedVisibility(visible = player1Message.isNotBlank(), modifier = Modifier
            .constrainAs(message1Ref) {
                start.linkTo(parent.start, margin = 16.dp)
                bottom.linkTo(parent.bottom, margin = 14.dp)
            }) {

            Text(
                modifier = Modifier.constrainAs(message1Ref) {
                    start.linkTo(parent.start, margin = 16.dp)
                    bottom.linkTo(parent.bottom, margin = 14.dp)
                },
                text = player1Message,
                fontSize = 18.sp,
                color = Color.White
            )
        }
        SimpleAnimatedVisibility(
            modifier = Modifier.constrainAs(gameOverRef) {
                start.linkTo(parent.start, margin = 16.dp)
                end.linkTo(parent.end, margin = 16.dp)
                bottom.linkTo(parent.bottom, margin = 14.dp)
                top.linkTo(parent.top, margin = 14.dp)
            },
            visible = gameOver
        ) {
            Text(
                modifier = Modifier
                    .constrainAs(gameOverRef) {
                        start.linkTo(parent.start, margin = 16.dp)
                        end.linkTo(parent.end, margin = 16.dp)
                        bottom.linkTo(parent.bottom, margin = 14.dp)
                        top.linkTo(parent.top, margin = 14.dp)
                    },
                text = "Tap to restart!",
                fontSize = 22.sp,
                color = Color.White
            )
        }
        SimpleAnimatedVisibility(visible = player2Message.isNotBlank(), modifier = Modifier
            .constrainAs(message2Ref) {
                end.linkTo(parent.end, margin = 16.dp)
                top.linkTo(parent.top, margin = 14.dp)
            }) {
            Text(
                modifier = Modifier
                    .constrainAs(message2Ref) {
                        end.linkTo(parent.end, margin = 16.dp)
                        top.linkTo(parent.top, margin = 14.dp)
                    }
                    .rotate(180f),
                text = player2Message,
                fontSize = 18.sp,
                color = Color.White
            )
        }

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
    winnerPlayer: Int,
    isSecondPlayer: Boolean = false
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color)
            .rotate(if (isSecondPlayer) 180f else 0f),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = totalScore.toString(),
            fontSize = 30.sp,
            color = Color.White
        )
        SimpleAnimatedVisibility(
            modifier = Modifier
                .align(if (isSecondPlayer) Alignment.BottomCenter else Alignment.BottomCenter)
                .padding(8.dp),
            visible = winnerPlayer != 0
        ) {
            Text(
                modifier = Modifier
                    .align(if (isSecondPlayer) Alignment.BottomCenter else Alignment.BottomCenter)
                    .padding(8.dp),
                text = when {
                    winnerPlayer == 2 && isSecondPlayer -> "Winner!"
                    winnerPlayer == 1 && !isSecondPlayer -> "Winner!"
                    else -> "Loser!"
                },
                fontSize = 24.sp,
                color = Color.White
            )
        }
    }
}