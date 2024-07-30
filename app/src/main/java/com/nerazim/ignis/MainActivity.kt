package com.nerazim.ignis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nerazim.ignis.data.BoardState
import com.nerazim.ignis.data.MoveDirection
import com.nerazim.ignis.data.TileState
import com.nerazim.ignis.data.TileType
import com.nerazim.ignis.ui.theme.IgnisTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IgnisTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val boardState by remember {
                        mutableStateOf(BoardState())
                    }

                    Board(
                        modifier = Modifier.padding(innerPadding),
                        boardState = boardState
                    )
                }
            }
        }
    }
}

@Composable
fun Board(
    modifier: Modifier = Modifier,
    boardState: BoardState
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(6),
        modifier = modifier
    ) {
        items(boardState.board) { tile ->
            val openDirectionDialog = remember {
                mutableStateOf(false)
            }
            val option1 = remember {
                mutableStateOf("")
            }
            val option2 = remember {
                mutableStateOf("")
            }

            BoardTile(
                tile = tile,
                onTileClick = {
                    val coordinates = tile.coordinates
                    val left = coordinates.first == boardState.startX
                    val right = coordinates.first == boardState.endX
                    val up = coordinates.second == boardState.startY
                    val down = coordinates.second == boardState.endY
                    val edges = listOf(left, right, up, down)
                    if (edges.count { it } == 2) {
                        if (up && left) {
                            option1.value = "Right"
                            option2.value = "Down"
                            openDirectionDialog.value = true
                        }
                        if (up && right) {
                            option1.value = "Left"
                            option2.value = "Down"
                            openDirectionDialog.value = true
                        }
                        if (down && left) {
                            option1.value = "Right"
                            option2.value = "Up"
                            openDirectionDialog.value = true
                        }
                        if (down && right) {
                            option1.value = "Left"
                            option2.value = "Up"
                            openDirectionDialog.value = true
                        }
                    }
                    else {
                        val direction = if (up) MoveDirection.DOWN else if (down) MoveDirection.UP
                            else if (left) MoveDirection.RIGHT else MoveDirection.LEFT
                        tile.onClick(TileType.EARTH, direction)
                    }
                }
            )

            if (openDirectionDialog.value) {
                DirectionDialog(
                    option1 = option1.value,
                    option2 = option2.value,
                    onDismiss = { openDirectionDialog.value = false },
                    onSelection = {
                        openDirectionDialog.value = false
                        tile.onClick(TileType.EARTH, it.toMoveDirection())
                    }
                )
            }
        }
    }


}

@Composable
fun BoardTile(tile: TileState, onTileClick: () -> Unit) {
    val tileState by remember {
        mutableStateOf(tile)
    }
    val resource = when(tileState.type) {
        TileType.EMPTY -> painterResource(id = R.drawable.board_space)
        TileType.WATER -> painterResource(id = R.drawable.water_tile)
        TileType.FIRE -> painterResource(id = R.drawable.fire_tile)
        TileType.EARTH -> painterResource(id = R.drawable.earth_tile)
        TileType.AIR -> painterResource(id = R.drawable.air_tile)
    }
    Image(
        painter = resource,
        contentDescription = null,
        alpha = if (tileState.type == TileType.EMPTY) 0.7f else 1f,
        modifier = Modifier
            .border(border = BorderStroke(2.dp, Color.Black))
            .clickable {
                onTileClick()
            }
    )
}

@Composable
fun DirectionDialog(
    option1: String,
    option2: String,
    onDismiss: () -> Unit,
    onSelection: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Choose the movement direction:")
        },
        confirmButton = {
            TextButton(onClick = {
                onSelection(option1)
            }) {
                Text(option1)
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onSelection(option2)
            }) {
                Text(option2)
            }
        }
    )
}

fun String.toMoveDirection(): MoveDirection {
    return when(this) {
        "Up" -> MoveDirection.UP
        "Down" -> MoveDirection.DOWN
        "Left" -> MoveDirection.LEFT
        "Right" -> MoveDirection.RIGHT
        else -> MoveDirection.UNDEFINED
    }
}

@Preview(showBackground = true)
@Composable
fun BoardPreview() {
    IgnisTheme {
        val boardState by remember {
            mutableStateOf(BoardState())
        }
        Board(boardState = boardState)
    }
}

@Preview(showBackground = true)
@Composable
fun DirectionDialogPreview() {
    IgnisTheme {
        DirectionDialog(option1 = "Up", option2 = "Down", onDismiss = { /*TODO*/ }) {

        }
    }
}