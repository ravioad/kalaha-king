package com.example.kalahaking.ui

import android.util.Log

class HeuristicHelper {
    fun evaluateBoardV2(board: IntArray): Double {
//        return (board[13] - board[6]).toDouble() // Player 2 Kalaha - Player 1 Kalaha

        var score = 0.0

        score += getStoresScore(board) //Stores score
        score += getPitsScores(board) //Pits score

        return score
    }

    private fun getStoresScore(board: IntArray): Double {
        val player1Store = board[6]  // Player 1's store (Kalaha)
        val player2Store = board[13] // Player 2's store (AI's Kalaha)
        val storeWeight = 3.0
        return (player2Store - player1Store) * storeWeight
    }

    private fun getPitsScores(board: IntArray): Double {
        var score = 0.0
        val pitWeight = 1

        for (i in 0..5) {
            score -= board[i] * pitWeight
        }
        for (i in 7..12) {
            score += board[i] * pitWeight
        }
        return score
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

        return 0.0
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
}