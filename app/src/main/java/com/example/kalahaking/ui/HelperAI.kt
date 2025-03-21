package com.example.kalahaking.ui

import android.app.Activity
import android.util.Log

class HelperAI(private val helper: HeuristicHelper) {

    private fun evaluateBoard(board: IntArray): Int {
        "evaluateBoardLeaf".printLog(board.toList())
        val player2KalahaScore = board[13]
        val player1KalahaScore = board[6]

        val kalahaWeight = 2.5
        val maxChainSearchDepth = 3
        val opponentKalahaWeight = 0.5

        var score = 0.0

        score += player2KalahaScore * kalahaWeight
        score -= player1KalahaScore * opponentKalahaWeight


        //Capture seeds evaluation
        val chainFreeTurnBonus =
            evaluateChainFreeTurnPotential(board, 2, 0, maxChainSearchDepth) //For AI-player
        val chainFreeTurnPenalty =
            evaluateOpponentChainFreeTurnPenalty(
                board,
                1,
                0,
                maxChainSearchDepth
            ) //For opponent player

        "chainFreeTurnBonus".printLog(chainFreeTurnBonus)
        "chainFreeTurnPenalty".printLog(chainFreeTurnPenalty)
        score += chainFreeTurnBonus  //This will be positive
        score += chainFreeTurnPenalty //This will be negative

//        val opponentCapturePenalty = getOpponentCaptureAndFreeTurnPenalty(board)
//        val aiCaptureFreeTurnScore = getAICaptureAndFreeTurnScore(board)
//        score -= opponentCapturePenalty //This will be negative
//        score += aiCaptureFreeTurnScore  //This will be positive
//        //Capture seeds evaluation
//        val opponentCapturePenalty = getOpponentCaptureAndFreeTurnPenalty(board)
//        val aiCaptureFreeTurnScore = getAICaptureAndFreeTurnScore(board)
//        score += opponentCapturePenalty //This will be negative
//        score += aiCaptureFreeTurnScore  //This will be positive

        //End-game evaluation
        val aiEndgameBonus = getAIEndgameBonus(board)
        val opponentEndgamePenalty = getOpponentEndgamePenalty(board)
        score += opponentEndgamePenalty //This will be negative
        score += aiEndgameBonus  //This will be positive

        "finalScore".printLog(score)
        return score.toInt()
    }

    private fun getOpponentEndgamePenalty(board: IntArray): Double {
        val endGameWeight = 1.5 // Weight for end-game bonus - tune this
        var endGamePenalty = 0.0
        val player1PitsEmpty = (7..12).all { board[it] == 0 }
        if (player1PitsEmpty) {
            for (i in 0..5) {
                endGamePenalty += board[i] * endGameWeight
            }
        }
        return endGamePenalty
    }

    private fun getAIEndgameBonus(board: IntArray): Double {
        val endGameWeight = 2.0 // Weight for end-game bonus - tune this
        var endGameBonus = 0.0
        val player1PitsEmpty = (0..5).all { board[it] == 0 }
        if (player1PitsEmpty) {
            for (i in 7..12) {
                endGameBonus += board[i] * endGameWeight
            }
        }
        return endGameBonus
    }

    private fun getAICaptureAndFreeTurnScore(board: IntArray): Double {
        val captureWeight = 3.5
        val extraTurnWeight = 3.0
        var score = 0.0
        // Get valid moves for the player we are evaluating
        val possibleMoves = getPossibleMovesForPlayer(board, 2)
        for (pitChoice in possibleMoves) {
            // Check if move resulted in capture
            val capturedSeedCount = wasCaptureMadeInMove(board, 2, pitChoice)
            if (capturedSeedCount > 0) {
                score += captureWeight * capturedSeedCount // Add capture bonus if move leads to capture
            }
            if (willGetFreeTurn(board, 2, pitChoice)) {
                score += extraTurnWeight
            }
        }
        return score
    }

    private fun willGetFreeTurn(board: IntArray, player: Int, startPitIndex: Int): Boolean {
        val kalahaIndex: Int
        val opponentKalahaIndex: Int

        if (player == 1) {
            kalahaIndex = 6
            opponentKalahaIndex = 13
        } else {
            kalahaIndex = 13
            opponentKalahaIndex = 6
        }

        val seedsToSow = board[startPitIndex]
        if (seedsToSow == 0) return false

        var lastPitLandedIndex = startPitIndex
        for (seed in 1..seedsToSow) {
            lastPitLandedIndex = (lastPitLandedIndex + 1) % 14 // Move to the next pit (wrap around)
            if (lastPitLandedIndex == opponentKalahaIndex) { // Skip opponent's Kalaha
                lastPitLandedIndex =
                    (lastPitLandedIndex + 1) % 14 // Move to next to skip opponent's kalaha
            }
        }

        // Check if last pit landed in player's own Kalaha
        return lastPitLandedIndex == kalahaIndex
    }

    private fun getOpponentCaptureAndFreeTurnPenalty(board: IntArray): Double {
        val opponentCaptureWeight = 3.0
        val extraTurnWeight = 4.0
        var penalty = 0.0
        val opponentPossibleMoves = getPossibleMovesForPlayer(board, 1)
        for (pitChoice in opponentPossibleMoves) {
            // Check if move resulted in capture
            val capturedSeedCount = wasCaptureMadeInMove(board, 1, pitChoice)
            if (capturedSeedCount > 0) {
                penalty -= opponentCaptureWeight * capturedSeedCount
            }
            if (willGetFreeTurn(board, 1, pitChoice)) {
                penalty -= extraTurnWeight
            }
        }
        return penalty
    }

    private fun wasCaptureMadeInMove(board: IntArray, player: Int, pitChoice: Int): Int {
        val seedsToSow = board[pitChoice] // Get seeds to sow
        if (seedsToSow == 0) return 0 // No seeds, no capture
        val opponentKalahaIndex: Int
        val playerPitsRange: IntRange
        if (player == 1) {
            opponentKalahaIndex = 13
            playerPitsRange = 0..5
        } else {
            opponentKalahaIndex = 6
            playerPitsRange = 7..12
        }

        var lastPitLandedIndex = pitChoice
        for (seed in 1..seedsToSow) { // Simulate sowing *just to find landing pit index*
            lastPitLandedIndex = (lastPitLandedIndex + 1) % 14 // Move to next pit (wrap around)
            if (lastPitLandedIndex == opponentKalahaIndex) { // Skip opponent's Kalaha
                lastPitLandedIndex = (lastPitLandedIndex + 1) % 14 // Move to next again to skip
            }
        }

        // Check Capture Conditions:
        if (player == 1 && lastPitLandedIndex in playerPitsRange) { // Last pit on player's side
            if (board[lastPitLandedIndex] == 0) { // Check if landing pit is EMPTY *before* the move
                val oppositePitIndex = 12 - lastPitLandedIndex // Calculate opposite pit index
                if (board[oppositePitIndex] > 0) { // Check if opposite pit has seeds *before* move
                    return board[oppositePitIndex] + 1 //Returns total number of captured seeds
                }
            }
        } else if (player == 2 && lastPitLandedIndex in playerPitsRange) { // Player 2's capture check
            if (board[lastPitLandedIndex] == 0) {
                val oppositePitIndex = 12 - lastPitLandedIndex
                if (board[oppositePitIndex] > 0) {
                    return board[oppositePitIndex] + 1 //Returns total number of captured seeds
                }
            }
        }
        return 0
    }

    private fun getPossibleMovesForPlayer(board: IntArray, player: Int): List<Int> {
        val possiblePits = mutableListOf<Int>()
        val playerPitsRange = if (player == 1) (0..5) else (7..12)

        for (pitIndex in playerPitsRange) {
            if (board[pitIndex] > 0) {
                possiblePits.add(pitIndex)
            }
        }
        Log.e("possiblePits", possiblePits.toString())
        return possiblePits
    }

    private fun makeMoveForMinimax(
        currentBoard: List<Int>,
        player: Int,
        startPitIndex: Int
    ): IntArray {
        val nextBoard = currentBoard.toIntArray().copyOf()
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

        var seedsToSow = nextBoard[startPitIndex]
        nextBoard[startPitIndex] = 0

        var currentPitIndex = startPitIndex
        while (seedsToSow > 0) {
            currentPitIndex = (currentPitIndex + 1) % 14

            if (currentPitIndex == opponentKalahaIndex) {
                continue
            }

            nextBoard[currentPitIndex] += 1
            seedsToSow -= 1
        }

        val lastPitLanded = currentPitIndex

        //(IF the last seed ends up in an empty pit, player takes all the seeds from the opposite pit (+1 from the last pit)

        // Capture Rule
        if (player == 1 && lastPitLanded in playerPitsRange && nextBoard[lastPitLanded] == 1) {
            val oppositePitIndex = 12 - lastPitLanded // Calculate opposite pit index for Player 1
            if (nextBoard[oppositePitIndex] > 0) {
                val capturedSeeds = nextBoard[lastPitLanded] + nextBoard[oppositePitIndex]
                nextBoard[kalahaIndex] += capturedSeeds
                nextBoard[lastPitLanded] = 0
                nextBoard[oppositePitIndex] = 0
            }
        } else if (player == 2 && lastPitLanded in playerPitsRange && nextBoard[lastPitLanded] == 1) {
            val oppositePitIndex = 12 - lastPitLanded // Calculate opposite pit index for Player 2
            if (nextBoard[oppositePitIndex] > 0) {
                val capturedSeeds = nextBoard[lastPitLanded] + nextBoard[oppositePitIndex]
                nextBoard[kalahaIndex] += capturedSeeds
                nextBoard[lastPitLanded] = 0
                nextBoard[oppositePitIndex] = 0
            }
        }

        return nextBoard
    }

    private fun isGameOverForMinimax(board: IntArray): Boolean {
        val player1PitsEmpty = (0..5).all { board[it] == 0 }
        val player2PitsEmpty = (7..12).all { board[it] == 0 }
        return player1PitsEmpty || player2PitsEmpty
    }

    fun printLog(text: Any?) {
        Log.e(this::class.java.simpleName, if (text !is String) text.toString() else text)
    }

    private fun minimax(
        board: IntArray,
        depth: Int,
        alpha: Double,
        beta: Double,
        maximizingPlayer: Boolean
    ): Double {
//        printLog("minimax - Depth: $depth, MaxPlayer: $maximizingPlayer, Alpha: $alpha, Beta: $beta")
        if (depth == 0 || isGameOverForMinimax(board)) {
            val score = helper.evaluateBoardV2(board)
            printLog("minimax - Leaf Node Depth: $depth, Score: $score")
            return score
        }

        if (maximizingPlayer) {
            var bestScore = Double.MIN_VALUE
            var currentAlpha = alpha
            val possibleMoves = getPossibleMovesForPlayer(board, 2)
//            printLog("minimax - AI MOVES : ${possibleMoves.toList().map { board[it] }}")

            for (pitChoice in possibleMoves) {
                printLog("minimax - Depth:$depth AI MOVE : [$pitChoice]=${board[pitChoice]} from possible: [${possibleMoves.toList().map { "{$it}-${board[it]}" }.joinToString(", ")}] ")
                val nextBoard = makeMoveForMinimax(board.toList(), 2, pitChoice)
                val freeTurn = willGetFreeTurn(board, 2, pitChoice)
                val score = if (freeTurn) {
                    printLog("minimax - AI Free turning At: [$pitChoice]=${board[pitChoice]}")
                    minimax(
                        nextBoard,
                        depth,
                        alpha,
                        beta,
                        true
                    ) // **RECURSIVE CALL: Same player's turn again (AI - maximizingPlayer remains true)**
                } else {
                    minimax(
                        nextBoard,
                        depth - 1,
                        alpha,
                        beta,
                        false
                    ) // **RECURSIVE CALL: Switch to minimizing player (opponent)**
                }
//                val score = minimax(nextBoard, depth - 1, currentAlpha, beta, false)
                bestScore = maxOf(bestScore, score)
                currentAlpha = maxOf(currentAlpha, bestScore)
                if (beta <= currentAlpha) {
//                    printLog("minimax - Beta Cutoff! Depth: $depth")
                    break
                }
            }
            return bestScore

        } else {
            var bestScore = Double.MAX_VALUE
            var currentBeta = beta
            val possibleMoves = getPossibleMovesForPlayer(board, 1)
//            printLog("minimax - HUMAN MOVES : ${possibleMoves.toList().map { board[it] }}")

            for (pitChoice in possibleMoves) {
                printLog("minimax - Depth:$depth HUMAN MOVE : [$pitChoice]=${board[pitChoice]} from possible: [${possibleMoves.toList().map { "{$it}-${board[it]}" }.joinToString(", ")}] ")
                val nextBoard = makeMoveForMinimax(board.toList(), 1, pitChoice)
                val freeTurn = willGetFreeTurn(board, 1, pitChoice)
                val score = if (freeTurn) {
                    printLog("minimax - HUMAN Free turning At: [$pitChoice]=${board[pitChoice]}")
                    minimax(
                        board = nextBoard,
                        depth = depth,
                        alpha = alpha,
                        beta = beta,
                        maximizingPlayer = false
                    ) // **RECURSIVE CALL: Same player's turn again (Opponent - maximizingPlayer remains false)**
                } else {
                    minimax(
                        board = nextBoard,
                        depth = depth - 1,
                        alpha = alpha,
                        beta = beta,
                        maximizingPlayer = true
                    ) // **RECURSIVE CALL: Switch to maximizing player (AI)**
                }
//                val score = minimax(nextBoard, depth - 1, alpha, currentBeta, true)
                bestScore = minOf(bestScore, score)
                currentBeta = minOf(currentBeta, bestScore)

                if (currentBeta <= alpha) {
//                    printLog("minimax - Alpha Cutoff! Depth: $depth")
                    break
                }
            }

            return bestScore
        }
    }

    fun getBestMoveWithMinimax(currentBoardState: List<Int>): Int? { // Pass boardState as argument
        var bestMove: Int? = null
        var bestScore = Double.MIN_VALUE
        var alpha = Double.MIN_VALUE
        val beta = Double.MAX_VALUE

        val possiblePits =
            getPossibleMovesForPlayer(currentBoardState.toIntArray(), 2) // Pass toIntArray()

        if (possiblePits.isEmpty()) {
            return null
        }

        for (pitChoice in possiblePits) {
            printLog("minimax - Trying pitChoice: ${currentBoardState[pitChoice]}") // Added print
            val nextBoardState = makeMoveForMinimax(
                currentBoardState,
                2,
                pitChoice
            ) //AI (Player 2) makes first (hypothetical) move
            val score = minimax(
                nextBoardState,
                depth = 4,
                alpha,
                beta,
                false
            ) //then, initiates chain of moves with (2nd hypothetical) move of the "Human" opponent.
            printLog("minimax - pitChoice: ${currentBoardState[pitChoice]}, score: $score")
            if (score > bestScore) {
                bestScore = score
                bestMove = pitChoice
            }
            alpha = maxOf(alpha, bestScore)
            printLog("minimax - Best Move: $bestMove")
        }
        return bestMove
    }


    private fun evaluateChainFreeTurnPotential(
        board: IntArray,
        player: Int,
        currentChainLength: Int,
        maxChainDepth: Int // Limit recursion depth to avoid infinite loops
    ): Double {
        if (currentChainLength >= maxChainDepth) {
            return 0.0 // Stop recursion at max depth
        }

        var chainBonus = 0.0
        val chainCaptureWeight = 3.0    // Weight for captures within a free turn chain - tune this!
        val extraTurnChainWeight = 3.5  // Weight for each free turn in a chain - tune this!

        val possibleMoves = getPossibleMovesForPlayer(board, player)
        for (pitChoice in possibleMoves) {
            val nextBoard = makeMoveForMinimax(board.toList(), player, pitChoice)
            val capturedSeedCount = wasCaptureMadeInMove(board, player, pitChoice)
            val captureBonus = if (capturedSeedCount > 0) { // Check for capture with free turn move
                capturedSeedCount * chainCaptureWeight // Bonus for capture within chain
            } else {
                0.0 // No capture with this move
            }
            if (willGetFreeTurn(board, player, pitChoice)) { // Check for free turn
                val deeperChainBonus = evaluateChainFreeTurnPotential( // Recursive call for chain
                    board = nextBoard,
                    player = player,
                    currentChainLength = currentChainLength + 1,
                    maxChainDepth = maxChainDepth
                )
                chainBonus = maxOf(
                    chainBonus,
                    captureBonus + extraTurnChainWeight + deeperChainBonus
                ) // Combine bonuses, maximize
            } else {
                chainBonus = maxOf(chainBonus, captureBonus)
            }
        }
        return chainBonus // Return the best chain bonus found
    }

    private fun evaluateOpponentChainFreeTurnPenalty(
        board: IntArray,
        player: Int, // Still need player context (AI player number, even though evaluating opponent)
        currentChainLength: Int,
        maxChainDepth: Int
    ): Double {
        if (currentChainLength >= maxChainDepth) {
            return 0.0 // Base case: stop recursion at max depth (no further penalty)
        }

        val opponentChainCaptureWeight = 2.0
        val opponentExtraTurnChainWeight = 1.5
        var chainPenalty = 0.0
        val opponentPlayer = 1 // We are evaluating opponent's (Player 1's) potential chains

        val opponentPossibleMoves =
            getPossibleMovesForPlayer(board, opponentPlayer) // Get opponent's moves

        "opponentPossibleMoves".printLog(opponentPossibleMoves)
        for (pitChoice in opponentPossibleMoves) {
            val nextBoard = makeMoveForMinimax(board.toList(), opponentPlayer, pitChoice)

            val capturedSeedCount = wasCaptureMadeInMove(board, player, pitChoice)
            val capturePenalty = if (capturedSeedCount > 0) {
                capturedSeedCount * opponentChainCaptureWeight
            } else {
                0.0
            }

            if (willGetFreeTurn(
                    board,
                    opponentPlayer,
                    pitChoice
                )
            ) { // Check if OPPONENT gets free turn
                "opponentPossibleMovesBoard".printLog(board.toList())
                "opponentPossibleMovesFRee".printLog("$opponentPlayer >>> $pitChoice")
                val deeperChainPenalty =
                    evaluateOpponentChainFreeTurnPenalty( // Recursive call for opponent chain
                        nextBoard,
                        player, // Still pass AI player number for context
                        currentChainLength + 1,
                        maxChainDepth
                    )
                chainPenalty = minOf(
                    chainPenalty,
                    capturePenalty + opponentExtraTurnChainWeight + deeperChainPenalty
                ) // Combine penalties, MINIMIZE penalty for AI (opponent maximizing their benefit)

            } else {
                // No free turn for opponent - Still consider capture penalty even for non-free-turn opponent moves
                chainPenalty = minOf(
                    chainPenalty,
                    capturePenalty
                ) // Still consider capture penalty for non-free-turn opponent move
            }
        }
        return chainPenalty // Return the MINIMAL chain penalty found (most negative or zero)
    }
}