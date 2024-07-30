package com.nerazim.ignis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nerazim.ignis.ui.theme.IgnisTheme

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

enum class TileType {
    EMPTY,
    WATER,
    FIRE,
    EARTH,
    AIR
}

val startingBoard: MutableList<TileType> = mutableListOf(
    TileType.FIRE, TileType.FIRE, TileType.EMPTY, TileType.EMPTY, TileType.WATER, TileType.WATER,
    TileType.FIRE, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.WATER,
    TileType.EMPTY, TileType.EMPTY, TileType.FIRE, TileType.WATER, TileType.EMPTY, TileType.EMPTY,
    TileType.EMPTY, TileType.EMPTY, TileType.WATER, TileType.FIRE, TileType.EMPTY, TileType.EMPTY,
    TileType.WATER, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.FIRE,
    TileType.WATER, TileType.WATER, TileType.EMPTY, TileType.EMPTY, TileType.FIRE, TileType.FIRE
)

@Composable
fun Board(modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(6),
        modifier = modifier
    ) {
        items(startingBoard) { tile ->
            val resource = when(tile) {
                TileType.EMPTY -> painterResource(id = R.drawable.board_space)
                TileType.WATER -> painterResource(id = R.drawable.water_tile)
                TileType.FIRE -> painterResource(id = R.drawable.fire_tile)
                TileType.EARTH -> painterResource(id = R.drawable.earth_tile)
                TileType.AIR -> painterResource(id = R.drawable.air_tile)
            }
            Image(
                painter = resource,
                contentDescription = null,
                alpha = if (tile == TileType.EMPTY) 0.7f else 1f,
                modifier = Modifier.border(border = BorderStroke(2.dp, Color.Black))
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BoardPreview() {
    IgnisTheme {
        Board()
    }
}