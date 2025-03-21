package com.example.kalahaking.ui

class HelperAI {
    private fun evaluateBoard(board: IntArray, player: Int): Int {
        val player2KalahaScore = board[13]
        val player1KalahaScore = board[6]

        val kalahaWeight = 3.0
        val captureWeight = 1.5
        val opponentKalahaWeight = 0.5

        var score = 0.0

        score += player2KalahaScore * kalahaWeight
        score -= player1KalahaScore * opponentKalahaWeight

        val possibleMoves = getPossibleMovesForPlayer(board, player)
        for (pitChoice in possibleMoves) {
            // Check if move resulted in capture
            if (wasCaptureMadeInMove(board, player, pitChoice)) {
                score += captureWeight  // Add capture bonus if move leads to capture
            }
        }
        return score.toInt()
    }

    private fun wasCaptureMadeInMove(board: IntArray, player: Int, pitChoice: Int): Boolean {
        val seedsToSow = board[pitChoice] // Get seeds to sow
        if (seedsToSow == 0) return false // No seeds, no capture
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
                    return true
                }
            }
        } else if (player == 2 && lastPitLandedIndex in playerPitsRange) { // Player 2's capture check
            if (board[lastPitLandedIndex] == 0) {
                val oppositePitIndex = 12 - lastPitLandedIndex
                if (board[oppositePitIndex] > 0) {
                    return true
                }
            }
        }
        return false
    }

    private fun getPossibleMovesForPlayer(board: IntArray, player: Int): List<Int> {
        val possiblePits = mutableListOf<Int>()
        val playerPitsRange = if (player == 1) (0..5) else (7..12)

        for (pitIndex in playerPitsRange) {
            if (board[pitIndex] > 0) {
                possiblePits.add(pitIndex)
            }
        }
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

    private fun minimax(
        board: IntArray,
        depth: Int,
        alpha: Int,
        beta: Int,
        maximizingPlayer: Boolean
    ): Int {
        if (depth == 0 || isGameOverForMinimax(board)) {
            return evaluateBoard(board, 2)
        }

        if (maximizingPlayer) {
            var bestScore = Int.MIN_VALUE
            var currentAlpha = alpha
            val possibleMoves = getPossibleMovesForPlayer(board, 2)

            for (pitChoice in possibleMoves) {
                val nextBoard = makeMoveForMinimax(board.toList(), 2, pitChoice)
                val score = minimax(nextBoard, depth - 1, currentAlpha, beta, false)
                bestScore = maxOf(bestScore, score)
                currentAlpha = maxOf(currentAlpha, bestScore)

                if (beta <= currentAlpha) {
                    break
                }
            }
            return bestScore

        } else {
            var bestScore = Int.MAX_VALUE
            var currentBeta = beta
            val possibleMoves = getPossibleMovesForPlayer(board, 1)

            for (pitChoice in possibleMoves) {
                val nextBoard = makeMoveForMinimax(board.toList(), 1, pitChoice)
                val score = minimax(nextBoard, depth - 1, alpha, currentBeta, true)
                bestScore = minOf(bestScore, score)
                currentBeta = minOf(currentBeta, bestScore)

                if (currentBeta <= alpha) {
                    break
                }
            }
            return bestScore
        }
    }

    fun getBestMoveWithMinimax(currentBoardState: List<Int>): Int? { // Pass boardState as argument
        var bestMove: Int? = null
        var bestScore = Int.MIN_VALUE
        var alpha = Int.MIN_VALUE
        val beta = Int.MAX_VALUE

        val possiblePits =
            getPossibleMovesForPlayer(currentBoardState.toIntArray(), 2) // Pass toIntArray()

        if (possiblePits.isEmpty()) {
            return null
        }

        for (pitChoice in possiblePits) {
            //AI (Player 2) makes first (hypothetical) move
            val nextBoardState = makeMoveForMinimax(currentBoardState, 2, pitChoice)
            val score = minimax(
                board = nextBoardState,
                depth = 4,
                alpha = alpha,
                beta = beta,
                maximizingPlayer = false
            ) //then, initiates chain of moves with (2nd hypothetical) move of the "Human" opponent.

            if (score > bestScore) {
                bestScore = score
                bestMove = pitChoice
            }
            alpha = maxOf(alpha, bestScore)
        }
        return bestMove
    }

}