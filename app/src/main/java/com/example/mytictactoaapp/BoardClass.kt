package com.example.mytictactoaapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.pow

sealed class Board {
    abstract val cells: MutableList<MutableList<Cell>>

    data object BoardTable : Board() {
        override var cells: MutableList<MutableList<Cell>> =
            mutableListOf(/* Your data here */)
    }
}
enum class Cell(val symbol: String, val notLocked: Boolean) {
    User("X", false),
    Android("O", false),
    Empty("", true)
}

class BoardClass(private var player: Cell) {
    @Composable
    fun MyDialog(winner :String, onDismiss: () -> Unit) {
//    var showDialog = remember { mutableStateOf(false) }
        val (clicked, showDialog) = remember { mutableStateOf(false) }
        if (clicked) {
            AlertDialog(
                onDismissRequest = {
                    showDialog(false)
                    onDismiss()
                },
                title = {
                    Text("Winner")
                },
                text = {
                    Text("$winner ",
                        fontSize = 20.sp)
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showDialog(false)
                            onDismiss()
                        }
                    ) {
                        Text("OK")
                    }
                }
            )
        }

        Column(
//        modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { showDialog(true) }) {
                Text("Show Result")
            }
        }
    }
    @Composable
    fun TableWithButtons() {
        if(Board.BoardTable.cells.size>2) {
            if (!isDraw()) {
                if (Board.BoardTable.cells.flatten().count() > 4 && checkWinner()) {
                    getWinner()?.name?.let { MyDialog(it, onDismiss = {}) }
                }

            } else {
                if (checkWinner()) {// boolean form,
                    getWinner()?.name?.let { MyDialog(it, onDismiss = {}) }
                } else {
                    MyDialog("No winner", onDismiss = {})
                }
            }
        }
        fun performButtonClick(row: Int, column: Int) {
            // Trigger button click if enabled
            // Perform button click
            if (!isDraw()) {
                if (Board.BoardTable.cells.flatten().count() > 4 && checkWinner()) {
                    return
                } else
                    if (player == Cell.Android) {
                        val (computerRow, computerColumn) = findBestMove(player)
                        Board.BoardTable.cells[computerRow][computerColumn] = player
                        player = Cell.User
                    } else {
                        Board.BoardTable.cells[row][column] = player
                        player = Cell.Android
                    }

            } else {
                return
            }
        }
        Column(
//            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            repeat(Board.BoardTable.cells.size) { row ->
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    repeat(Board.BoardTable.cells.size) { column ->
                        Button(
                            onClick = {
                                performButtonClick(row, column)
                            },
                            modifier = Modifier
                                .padding(2.dp)
                                .height(48.dp),
                            shape = RectangleShape,
                            enabled = Board.BoardTable.cells[row][column].notLocked//mutableBoardTable[row][column].notLocked,//player1.locked
//                                colors = ButtonDefaults.buttonColors(
//                                    containerColor = mutableBoardTable[row][column].color//player1.color
//                                ),
                        ) {
                            Text(
                                text = Board.BoardTable.cells[row][column].symbol,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )

                        }
                    }
                }
            }
        }
    }

    private fun checkWinner(): Boolean {
        // Check rows
        for (row in Board.BoardTable.cells) {
            if (row.all { it == Cell.User } || row.all { it == Cell.Android }) {
                return true
            }
        }

        // Check columns
        for (colIndex in Board.BoardTable.cells.indices) {
            val column = Board.BoardTable.cells.map { it[colIndex] }
            if ((column.all { it == Cell.User } || column.all { it == Cell.Android }) && column.size == Board.BoardTable.cells.size) {
                return true
            }
        }

        // Check diagonals or diameter
        val diagonal1 = List(Board.BoardTable.cells.size) { Board.BoardTable.cells[it][it] }
        val diagonal2 =
            List(Board.BoardTable.cells.size) { Board.BoardTable.cells[it][Board.BoardTable.cells.size - it - 1] }
        return diagonal1.all { it == Cell.User } || diagonal1.all { it == Cell.Android } ||
                diagonal2.all { it == Cell.User } || diagonal2.all { it == Cell.Android }
    }

    private fun getWinner(): Cell? {
        // Check rows
        for (i in 0 until Board.BoardTable.cells.size) {
            if (Board.BoardTable.cells[i].all { it == Cell.User }) {
                return Cell.User
            }
            if (Board.BoardTable.cells[i].all { it == Cell.Android }) {
                return Cell.Android
            }
        }
        // Check columns
        for (colIndex in Board.BoardTable.cells.indices) {
            val column = Board.BoardTable.cells.map { it[colIndex] }
            if ((column.all { it == Cell.User }) && column.size == Board.BoardTable.cells.size) {
                return Cell.User
            } else if ((column.all { it == Cell.Android }) && column.size == Board.BoardTable.cells.size) {
                return Cell.Android
            }
        }
        // Check diagonals
        val diagonal1 = List(Board.BoardTable.cells.size) { Board.BoardTable.cells[it][it] }
        val diagonal2 =
            List(Board.BoardTable.cells.size) { Board.BoardTable.cells[it][Board.BoardTable.cells.size - it - 1] }
        if (diagonal1.all { it == Cell.User } || diagonal2.all { it == Cell.User })
            return Cell.User
        if (diagonal1.all { it == Cell.Android } || diagonal2.all { it == Cell.Android })
            return Cell.Android
        return null
    }

    private fun isDraw(): Boolean {// true is table full
        return Board.BoardTable.cells.all { row -> row.all { it != Cell.Empty } }
    }

    private fun makeMove(player: Cell, row: Int, col: Int) {

        Board.BoardTable.cells[row][col] = player
    }
//
//    private fun findBestPosition(player: Cell): Pair<Int, Int> {
//        var bestMove = Pair(-1, -1)
//        var bestScore = -1
//        for (i in 0 until Board.BoardTable.cells.size) {
//            for (j in 0 until Board.BoardTable.cells.size) {
//                if (Board.BoardTable.cells[i][j] == Cell.Empty) {
//                    makeMove(player, i, j)
//                    val score = findBestScore(player, Pair(i, j))
//                    makeMove(Cell.Empty, i, j) // Undo the move
//                    if (score > bestScore) {
//                        bestScore = score
//                        bestMove = Pair(i, j)
//                    }
//                }
//            }
//        }
//        return bestMove
//    }

    private fun findBestScore(player: Cell, position: Pair<Int, Int>): Int {
        val anotherPlayer = if (player == Cell.User) Cell.Android else Cell.User
        var score = 0
        var countPlayer: Int
        var countAnotherPlayer: Int
        var countEmpty: Int
        var depthHelper: Int
        var i = position.first
        countPlayer = Board.BoardTable.cells[i].count { it == player }
        countAnotherPlayer = Board.BoardTable.cells[i].count { it == anotherPlayer }
        countEmpty = Board.BoardTable.cells[i].count { it == Cell.Empty }
        if (Board.BoardTable.cells.size == countPlayer + countEmpty) {
            depthHelper = 10.toDouble().pow(countPlayer).toInt()
            score += depthHelper
        } else if (countPlayer == 1 && Board.BoardTable.cells.size == countAnotherPlayer + countPlayer) {
            depthHelper = 10.toDouble().pow(countAnotherPlayer).toInt() + 1
            score += depthHelper
        }else if (countPlayer == 1 && Board.BoardTable.cells.size == countAnotherPlayer + countPlayer + countEmpty) {
            depthHelper = 10.toDouble().pow(countAnotherPlayer).toInt() + 1
            score += depthHelper
        }
        i = position.second
        countPlayer = Board.BoardTable.cells.count { it[i] == player }
        countAnotherPlayer = Board.BoardTable.cells.count { it[i] == anotherPlayer }
        countEmpty = Board.BoardTable.cells.count { it[i] == Cell.Empty }
        if (Board.BoardTable.cells.size == countPlayer + countEmpty) {

            depthHelper = 10.toDouble().pow(countPlayer).toInt()
            score += depthHelper

        } else if (countPlayer == 1 && Board.BoardTable.cells.size == countAnotherPlayer + countPlayer) {
            depthHelper = 10.toDouble().pow(countAnotherPlayer).toInt() + 1
            score += depthHelper

        }else if (countPlayer == 1 && Board.BoardTable.cells.size == countAnotherPlayer + countPlayer + countEmpty) {
            depthHelper = 10.toDouble().pow(countAnotherPlayer).toInt() + 1
            score += depthHelper
        }
        //diagonals
        if (position.second == position.first) {
            val diagonal1 = List(Board.BoardTable.cells.size) { Board.BoardTable.cells[it][it] }

            countPlayer = diagonal1.count { it == player }
            countAnotherPlayer = diagonal1.count { it == anotherPlayer }
            countEmpty = diagonal1.count { it == Cell.Empty }
            if (Board.BoardTable.cells.size == countPlayer + countEmpty) {

                depthHelper = 10.toDouble().pow(countPlayer).toInt()
                score += depthHelper

            } else if (countPlayer == 1 && Board.BoardTable.cells.size == countAnotherPlayer + countPlayer) {
                depthHelper = 10.toDouble().pow(countAnotherPlayer).toInt() + 1
                score += depthHelper

            }else if (countPlayer == 1 && Board.BoardTable.cells.size == countAnotherPlayer + countPlayer + countEmpty) {
                depthHelper = 10.toDouble().pow(countAnotherPlayer).toInt() + 1
                score += depthHelper
            }
        }
        if (position.first == Board.BoardTable.cells.size - position.second - 1) {
            val diagonal2 =
                List(Board.BoardTable.cells.size) { Board.BoardTable.cells[it][Board.BoardTable.cells.size - it - 1] }
            countPlayer = diagonal2.count { it == player }

            countAnotherPlayer = diagonal2.count { it == anotherPlayer }
            countEmpty = diagonal2.count { it == Cell.Empty }
            if (Board.BoardTable.cells.size == countPlayer + countEmpty) {

                depthHelper = 10.toDouble().pow(countPlayer).toInt()
                score += depthHelper

            } else if (countPlayer == 1 && Board.BoardTable.cells.size == countAnotherPlayer + countPlayer) {

                depthHelper = 10.toDouble().pow(countAnotherPlayer).toInt() + 1
                score += depthHelper

            }else if (countPlayer == 1 && Board.BoardTable.cells.size == countAnotherPlayer + countPlayer + countEmpty) {
                depthHelper = 10.toDouble().pow(countAnotherPlayer).toInt() + 1
                score += depthHelper
            }
        }

        return score
    }

    private fun findBestMove(player: Cell): Pair<Int, Int> {
        var bestMove = Pair(-1, -1)
        var bestScore = Int.MIN_VALUE//findBestScore(player)
        for (i in 0 until Board.BoardTable.cells.size) {
            for (j in 0 until Board.BoardTable.cells.size) {
                if (Board.BoardTable.cells[i][j] == Cell.Empty) {
                    makeMove(player, i, j)
                    val score = findBestScore(player, Pair(i, j))
//                    minimax(
//                        Cell.User
//                    )
                    makeMove(Cell.Empty, i, j) // Undo the move
                    if (score > bestScore) {
                        bestScore = score
                        bestMove = Pair(i, j)
                    }
                }
            }
        }
        return bestMove
    }

//    private fun minimax(
//        maximizingPlayer: Cell
//    ): Int {
//        if (checkWinner()) {
//            return if (getWinner() == maximizingPlayer) {
//                Int.MAX_VALUE / (Board.BoardTable.cells.size.toDouble().pow(2).toInt())
//            } else {
//                Int.MIN_VALUE / (Board.BoardTable.cells.size.toDouble().pow(2).toInt())
//            }
//        }
//        if (isDraw()) {
//            return 0
//        }
//        val position: Pair<Int, Int> = findBestPosition(maximizingPlayer)
//        makeMove(maximizingPlayer, position.first, position.second)
//        val score = findBestScore(player, position)
//        minimax(
//            maximizingPlayer = if (maximizingPlayer == Cell.User) {
//                Cell.Android
//            } else {
//                Cell.User
//            }
//        )
//        makeMove(Cell.Empty, position.first, position.second)
//        return score
//    }
}