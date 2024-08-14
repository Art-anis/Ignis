package com.nerazim.ignis

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nerazim.ignis.data.BoardViewModel
import com.nerazim.ignis.data.MoveDirection
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
                    //столбец, в котором все лежит
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        //viewModel
                        val viewModel: BoardViewModel = viewModel(factory = AppViewModelProvider.Factory)
                        //поле
                        Board(
                            modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxWidth(),
                            viewModel = viewModel
                        )
                        //кнопка ресета игры
                        Button(onClick = viewModel::resetGame) {
                            Text("Reset")
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        //выбор тайла
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            //земля
                            TileChoiceItem(
                                type = TileType.EARTH,
                                tilesLeft = viewModel.earthTilesNumber.intValue,
                                modifier = Modifier.fillMaxWidth(0.5f),
                                onClick = { viewModel.selectedTile = TileType.EARTH },
                                flag = viewModel.selectedTile == TileType.EARTH
                            )
                            //воздух
                            TileChoiceItem(
                                type = TileType.AIR,
                                tilesLeft = viewModel.airTilesNumber.intValue,
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { viewModel.selectedTile = TileType.AIR },
                                flag = viewModel.selectedTile == TileType.AIR
                            )
                        }
                    }
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
                TileType.DESTROYED -> painterResource(id = R.drawable.board_space_destroyed)
            }
            //картинка с тайлом
            Image(
                painter = resource,
                contentDescription = null,
                alpha = if (tile.type == TileType.EMPTY || tile.type == TileType.DESTROYED) 0.7f else 1f, //делаем пустой тайл более прозрачным
                modifier = Modifier
                    .border(border = BorderStroke(2.dp, Color.Black))
                    .clickable {
                        //клик работает, если тайл не уничтожен и мы выбрали, что выложить
                        if (tile.type != TileType.DESTROYED && viewModel.selectedTile != TileType.EMPTY) {
                            //координаты для определения, крайний ли это тайл
                            val coordinates = tile.coordinates
                            val left = coordinates.first == viewModel.startX
                            val right = coordinates.first == viewModel.endX
                            val up = coordinates.second == viewModel.startY
                            val down = coordinates.second == viewModel.endY
                            val edges = listOf(left, right, up, down)
                            //если это угол, то открываем диалог с соответствующими вариантами
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
                                //определяем направление сдвига
                                val direction =
                                    if (up) MoveDirection.DOWN else if (down) MoveDirection.UP
                                    else if (left) MoveDirection.RIGHT else MoveDirection.LEFT

                                //финальная проверка на тип тайла
                                //если земля закончилась, мы обязаны перевернуть воздух и играть его как землю
                                val type = if (viewModel.selectedTile == TileType.AIR
                                    && viewModel.earthTilesNumber.intValue == 0) {
                                    Toast
                                        .makeText(context, "Air changing to Earth", Toast.LENGTH_SHORT)
                                        .show()
                                    TileType.EARTH
                                }
                                else viewModel.selectedTile
                                //двигаем, если можно
                                val status =
                                    viewModel.onClick(
                                        type,
                                        tile.coordinates,
                                        direction
                                    )
                                //выводим тост согласно статусу клика
                                when (status) {
                                    -1 -> Toast
                                        .makeText(context, "Illegal move!", Toast.LENGTH_SHORT)
                                        .show()

                                    1 -> Toast
                                        .makeText(context, "It's a tie!", Toast.LENGTH_LONG)
                                        .show()

                                    2 -> Toast
                                        .makeText(context, "Fire player won!", Toast.LENGTH_LONG)
                                        .show()

                                    3 -> Toast
                                        .makeText(context, "Water player won!", Toast.LENGTH_LONG)
                                        .show()
                                }
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
                        //финальная проверка на тип тайла
                        //если земля закончилась, мы обязаны перевернуть воздух и играть его как землю
                        val type = if (viewModel.selectedTile == TileType.AIR
                            && viewModel.earthTilesNumber.intValue == 0) {
                            Toast
                                .makeText(context, "Air changing to Earth", Toast.LENGTH_SHORT)
                                .show()
                            TileType.EARTH
                        }
                        else viewModel.selectedTile
                        //после выбора пытаемся сдвинуть
                        val status = viewModel.onClick(type, tile.coordinates, it.toMoveDirection())
                        //выводим тост согласно статусу клика
                        when (status) {
                            -1 -> Toast.makeText(context, "Illegal move!", Toast.LENGTH_SHORT).show()
                            1 -> Toast.makeText(context, "It's a tie!", Toast.LENGTH_LONG).show()
                            2 -> Toast.makeText(context, "Fire player won!", Toast.LENGTH_LONG).show()
                            3 -> Toast.makeText(context, "Water player won!", Toast.LENGTH_LONG).show()
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

@Composable
fun TileChoiceItem(
    type: TileType,
    tilesLeft: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    flag: Boolean
) {
    val resource = if (type == TileType.EARTH) painterResource(id = R.drawable.earth_tile)
        else painterResource(id = R.drawable.air_tile)
    Column(
        modifier = Modifier
            .background(color = if (flag) Color.LightGray else Color.White)
            .clickable {
                if (tilesLeft != 0) {
                    onClick()
                }
        }
    ) {
        Image(
            painter = resource,
            contentDescription = null,
            modifier = modifier.fillMaxWidth()
        )
        Text(
            text = "x$tilesLeft",
            modifier = modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
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

@Preview(showBackground = true)
@Composable
fun TileChoicePreview() {
    IgnisTheme {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            TileChoiceItem(
                type = TileType.EARTH,
                tilesLeft = 9,
                modifier = Modifier.fillMaxWidth(0.5f),
                onClick = {},
                false
            )
            TileChoiceItem(
                type = TileType.AIR,
                tilesLeft = 12,
                modifier = Modifier.fillMaxWidth(),
                onClick = {},
                false
            )
        }
    }
}