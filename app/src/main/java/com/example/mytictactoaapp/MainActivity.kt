package com.example.mytictactoaapp

import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mytictactoaapp.ui.theme.MyTicTacToaAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTicTacToaAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize()
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TicTacToeContent()
                }
            }
        }
    }
}

@Composable
fun TitleGame(name: String) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "Hello $name!",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}

@Composable
fun inputColumns(): Pair<Int, Cell> {

    val height = LocalConfiguration.current.screenHeightDp
    val width = LocalConfiguration.current.screenWidthDp
    val numberOfButtonAtMost = if (height < width) {
        height / 65
    } else {
        width / 65
    }
    var boardSize by rememberSaveable {
        mutableIntStateOf(3)
    }
    var startBy by rememberSaveable { mutableStateOf(Cell.User) }
    val (clicked, setClicked) = remember { mutableStateOf(false) }
    // Call TableWithButtons if the button is clicked
    if (clicked) {
        Board.BoardTable.cells.clear()
        BoardContent(Pair(boardSize, startBy))
        setClicked(false) // Reset the clicked state to false after resetting the game

    }
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        var textFieldValue by rememberSaveable { mutableStateOf("3") }
        Button(
            modifier = Modifier
                .wrapContentHeight() // This is similar to wrap_content for height
                .widthIn(max = 135.dp)
//                .wrapContentWidth()  // This is similar to wrap_content for width
//                .background(Color.Gray)
                .padding(10.dp),
            onClick = {
                startBy = if (startBy == Cell.User) Cell.Computer else Cell.User
            }
        ) {
            Text(startBy.name)
        }
        TextField(
            modifier = Modifier
                .padding(10.dp)
                .wrapContentHeight() // This is similar to wrap_content for height
                .widthIn(max = 115.dp) // Limit width to at most half the screen width
                .border(width = 2.dp, color = Color.Black)
//                .wrapContentSize()
//                .wrapContentWidth()  // This is similar to wrap_content for width
                .background(Color.Gray),
            value = textFieldValue,
            onValueChange = {
                textFieldValue = it
            },
            label = { Text("Board size") },
        )
        Button(
            modifier = Modifier
                .wrapContentHeight() // This is similar to wrap_content for height
                .wrapContentWidth()  // This is similar to wrap_content for width
//                .background(Color.Gray)
                .padding(10.dp),
            onClick = {
                boardSize =
                    if (textFieldValue.isNotEmpty() && textFieldValue.toInt() in 3..numberOfButtonAtMost) {
                        // Number is valid, handle it (e.g., navigate to the next screen)
                        textFieldValue.toInt()
                    } else {
                        // Number is not valid, show an error message or perform some other action
                        3
                    }
                setClicked(true)
            }
        ) {
            Text(text = "Start Game")
        }
    }
    return Pair(boardSize, startBy)
}

@Composable
fun CreateInitialButtonValues(boardSize: Int) {
    Board.BoardTable.cells = remember {
        mutableStateListOf<MutableList<Cell>>().apply {
            repeat(boardSize) {
                add(mutableStateListOf<Cell>().apply {
                    repeat(boardSize) { _ ->
                        add(Cell.Empty)
                    }
                })
            }
        }
    }
}

@Composable
fun BoardContent(playerInfo: Pair<Int, Cell>) {
    CreateInitialButtonValues(playerInfo.first)
    val ticTacToeBoard = BoardClass(playerInfo.second)
    ticTacToeBoard.TableWithButtons()

//    Board.BoardTable.cells = mutableListOf(
//        mutableListOf(Cell.Empty, Cell.Empty, Cell.Computer),
//        mutableListOf(Cell.Empty, Cell.Empty, Cell.Empty),
//        mutableListOf(Cell.User, Cell.Empty, Cell.Empty)
//    )
//
//
//    ticTacToeBoard.board = Board.BoardTable.cells
}

@Composable
fun TicTacToeContent() {
    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TitleGame("TicTacToe Game")
        Spacer(modifier = Modifier.height(32.dp))
        val boardInfo = inputColumns()// Set the size of the Tic Tac Toe board
        Spacer(modifier = Modifier.height(32.dp))
        BoardContent(boardInfo)
        Spacer(modifier = Modifier.height(32.dp))
        ResetButton(boardInfo)
    }
}

@Composable
fun ResetButton(boardInfo: Pair<Int, Cell>) {
    // Create a mutable state variable to track whether the button is clicked
    val (clicked, setClicked) = remember { mutableStateOf(false) }
    // Call TableWithButtons if the button is clicked
    if (clicked) {
        Board.BoardTable.cells.clear()
        BoardContent(boardInfo)
        setClicked(false) // Reset the clicked state to false after resetting the game
    }
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        Button(onClick = { // Update the state variable when the button is clicked
            setClicked(true)
        }
        ) {
            Text(text = "Reset Game")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyTicTacToaAppTheme {
        TicTacToeContent()
    }
}