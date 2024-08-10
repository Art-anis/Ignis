package com.nerazim.ignis

import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nerazim.ignis.data.BoardViewModel
import com.nerazim.ignis.data.MoveDirection
import com.nerazim.ignis.data.TileState
import com.nerazim.ignis.data.TileType
import com.nerazim.ignis.ui.theme.IgnisTheme

//базовая активность со скаффолдом
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IgnisTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Board(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

//поле
@Composable
fun Board(
    modifier: Modifier = Modifier,
    viewModel: BoardViewModel = viewModel(factory = AppViewModelProvider.Factory) //viewmodel с состоянием поля
) {
    val context = LocalContext.current //контекст для тостов

    //грид, который отображает само поле
    LazyVerticalGrid(
        columns = GridCells.Fixed(6), //всегда 6 столбцов
        modifier = modifier
    ) {
        items(viewModel.board) { tile ->
            //переменные для диалога выбора направления: флаг и два варианта
            val openDirectionDialog = remember {
                mutableStateOf(false)
            }
            val option1 = remember {
                mutableStateOf("")
            }
            val option2 = remember {
                mutableStateOf("")
            }

            //картинка для тайла, зависит от фишки, которая на нем
            val resource = when(tile.type) {
                TileType.EMPTY -> painterResource(id = R.drawable.board_space)
                TileType.WATER -> painterResource(id = R.drawable.water_tile)
                TileType.FIRE -> painterResource(id = R.drawable.fire_tile)
                TileType.EARTH -> painterResource(id = R.drawable.earth_tile)
                TileType.AIR -> painterResource(id = R.drawable.air_tile)
            }
            //картинка с тайлом
            Image(
                painter = resource,
                contentDescription = null,
                alpha = if (tile.type == TileType.EMPTY) 0.7f else 1f, //делаем пустой тайл более прозрачным
                modifier = Modifier
                    .border(border = BorderStroke(2.dp, Color.Black))
                    .clickable {
                        //определяем, крайний ли это тайл, сравнивая координаты с крайними
                        val coordinates = tile.coordinates
                        val left = coordinates.first == viewModel.startX
                        val right = coordinates.first == viewModel.endX
                        val up = coordinates.second == viewModel.startY
                        val down = coordinates.second == viewModel.endY
                        val edges = listOf(left, right, up, down)
                        //если это угол, то открываем диалог с соответсвующими вариантами
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
                        //если это не угол, но край, просто сдвигаем в нужном направлении
                        } else if (edges.count { it } == 1) {
                            val direction =
                                if (up) MoveDirection.DOWN else if (down) MoveDirection.UP
                                else if (left) MoveDirection.RIGHT else MoveDirection.LEFT
                            //двигаем, если можно
                            val status =
                                viewModel.onClick(TileType.EARTH, tile.coordinates, direction)
                            //если нельзя, выводим тост
                            if (!status) {
                                Toast
                                    .makeText(context, "Illegal move!", Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                    }
            )

            //диалог с выбором направления
            if (openDirectionDialog.value) {
                DirectionDialog(
                    option1 = option1.value,
                    option2 = option2.value,
                    onDismiss = { openDirectionDialog.value = false },
                    onSelection = {
                        openDirectionDialog.value = false
                        //после выбора пытаемся сдвинуть
                        val status = viewModel.onClick(TileType.EARTH, tile.coordinates, it.toMoveDirection())
                        //если нельзя, выводим тост
                        if (!status) {
                            Toast.makeText(context, "Illegal move!", Toast.LENGTH_LONG).show()
                        }
                    }
                )
            }
        }
    }
}

//диалог с выбором направления
@Composable
fun DirectionDialog(
    option1: String,
    option2: String,
    onDismiss: () -> Unit,
    onSelection: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss, //если откат, то просто закрываем диалог
        title = {
            Text("Choose the movement direction:")
        },
        confirmButton = { //первый вариант
            TextButton(onClick = {
                onSelection(option1)
            }) {
                Text(option1)
            }
        },
        dismissButton = { //второй вариант
            TextButton(onClick = {
                onSelection(option2)
            }) {
                Text(option2)
            }
        }
    )
}

//расширение, привод к enum направления
fun String.toMoveDirection(): MoveDirection {
    return when(this) {
        "Up" -> MoveDirection.UP
        "Down" -> MoveDirection.DOWN
        "Left" -> MoveDirection.LEFT
        "Right" -> MoveDirection.RIGHT
        else -> MoveDirection.UNDEFINED
    }
}

//Preview
@Preview(showBackground = true)
@Composable
fun BoardPreview() {
    IgnisTheme {
        Board()
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