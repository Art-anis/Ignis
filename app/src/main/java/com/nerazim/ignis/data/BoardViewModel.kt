package com.nerazim.ignis.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel

//viewModel поля
class BoardViewModel: ViewModel() {
    //крайние координаты
    var startX = 0
    var startY = 0
    var endX = 5
    var endY = 5

    var airTilesNumber = mutableIntStateOf(12)
    var earthTilesNumber = mutableIntStateOf(9)

    var selectedTile by mutableStateOf(TileType.EMPTY)

    private fun endTrigger(): Boolean {
        val pieces = _board.filter { it.type != TileType.DESTROYED }
        return _board.count { it.type == TileType.DESTROYED } == 36 ||
                pieces.count { it.type == TileType.WATER } == 0 ||
                pieces.count { it.type == TileType.FIRE } == 0
    }

    fun resetGame() {
        for (i in 0 until _board.size) {
            _board[i] = _board[i].copy(type = startingBoard[i].type)
        }
        airTilesNumber.intValue = 12
        earthTilesNumber.intValue = 9
    }

    //удаление ряда
    private fun cutRow(top: Boolean, bottom: Boolean) {
        if (top) {
            for (i in 6 * startY + startX .. 6 * startY + endX) {
                _board[i] = _board[i].copy(type = TileType.DESTROYED)
            }
            startY++
        }
        if (bottom) {
            for (i in 6 * endY + startX .. 6 * endY + endX) {
                _board[i] = _board[i].copy(type = TileType.DESTROYED)
            }
            endY--
        }
    }

    //удаление столбца
    private fun cutColumn(left: Boolean, right: Boolean) {
        if (left) {
            for (i in 6 * startY + startX ..6 * endY + startX step 6) {
                _board[i] = _board[i].copy(type = TileType.DESTROYED)
            }
            startX++
        }
        if (right) {
            for (i in 6 * startY + endX..6 * endY + endX step 6) {
                _board[i] = _board[i].copy(type = TileType.DESTROYED)
            }
            endX--
        }
    }

    //проверка, надо ли удалять ряд
    private fun shouldRemoveRow(): Pair<Boolean, Boolean> {
        val topRowTiles = _board.filter { it.coordinates.second == startY && it.type != TileType.DESTROYED }
        val bottomRowTiles = _board.filter { it.coordinates.second == endY && it.type != TileType.DESTROYED }
        return Pair(topRowTiles.count { it.type == topRowTiles[0].type } == endX - startX + 1 && topRowTiles.isNotEmpty(),
            bottomRowTiles.count { it.type == bottomRowTiles[0].type } == endX - startX + 1 && bottomRowTiles.isNotEmpty())
    }

    //проверка, надо ли удалять столбец
    private fun shouldRemoveColumn(): Pair<Boolean, Boolean> {
        val topColumnTiles = _board.filter { it.coordinates.first == startX && it.type != TileType.EMPTY && it.type != TileType.DESTROYED }
        val bottomColumnTiles = _board.filter { it.coordinates.first == endX && it.type != TileType.EMPTY && it.type != TileType.DESTROYED }
        return Pair(topColumnTiles.count { it.type == topColumnTiles[0].type } == endY - startY + 1 && topColumnTiles.isNotEmpty(),
            bottomColumnTiles.count { it.type == bottomColumnTiles[0].type } == endY - startY + 1 && bottomColumnTiles.isNotEmpty())
    }

    //реакция на нажатие на тайл
    fun onClick(newTile: TileType, coordinates: Pair<Int, Int>, direction: MoveDirection): Int {
        //извлекаем ряд или столбец, который будем двигать
        val list: MutableList<TileState> = when (direction) {
            MoveDirection.UP -> board.filter { it.coordinates.first == coordinates.first && it.type != TileType.DESTROYED }.reversed().toMutableList()
            MoveDirection.DOWN -> board.filter { it.coordinates.first == coordinates.first && it.type != TileType.DESTROYED}.toMutableList()
            MoveDirection.LEFT -> board.filter { it.coordinates.second == coordinates.second && it.type != TileType.DESTROYED}.reversed().toMutableList()
            MoveDirection.RIGHT -> board.filter { it.coordinates.second == coordinates.second && it.type != TileType.DESTROYED}.toMutableList()
            MoveDirection.UNDEFINED -> {
                return -1
            }
        }
        //если пустых мест нет, проверяем, можно ли вытолкнуть крайний тайл
        //нельзя вытолкнуть землю, а воздух можно вытолкнуть только землей
        if (list.count { it.type == TileType.EMPTY } == 0 &&
            (list.last().type == TileType.EARTH || list.last().type == TileType.AIR && newTile != TileType.EARTH)) {
            return -1
        }
        else {
            //количество сдвигов
            var iterationNumber = list.indexOf(list.find { it.type == TileType.EMPTY})
            //ставим в максимум, если нет пустых тайлов
            if (iterationNumber == -1) {
                iterationNumber = list.size - 1
            }
            //шаг, с которым мы будем двигаться по изначальному списку, чтобы обновлять тайлы
            val step = when (direction) {
                //шаг по списку зависит от направления
                MoveDirection.UP -> -6
                MoveDirection.DOWN -> 6
                MoveDirection.LEFT -> -1
                MoveDirection.RIGHT -> 1
                else -> return -1
            }
            //переменные для свапа
            var tempTile = newTile
            var temp: TileType

            //стартовый индекс считаем по координатам
            var currentIndex = 6 * coordinates.second + coordinates.first
            //сдвиг массива
            for (i in 0 until iterationNumber) {
                temp = _board[currentIndex].type
                _board[currentIndex] = _board[currentIndex].copy(type = tempTile)
                tempTile = temp
                currentIndex += step
            }
            //ставим тайл на последнюю ячейку
            if (tempTile != TileType.EMPTY) {
                temp = _board[currentIndex].type
                _board[currentIndex] = _board[currentIndex].copy(type = tempTile)
                tempTile = temp
            }

            var cutRowFlag = shouldRemoveRow()
            var cutColumnFlag = shouldRemoveColumn()
            while (cutColumnFlag.first || cutColumnFlag.second || cutRowFlag.first || cutRowFlag.second) {
                cutRow(top = cutRowFlag.first, bottom = cutRowFlag.second)
                cutColumn(left = cutColumnFlag.first, right = cutColumnFlag.second)
                cutRowFlag = shouldRemoveRow()
                cutColumnFlag = shouldRemoveColumn()
            }
            if (newTile == TileType.EARTH && earthTilesNumber.intValue != 0) {
                earthTilesNumber.intValue--
            }
            else {
                airTilesNumber.intValue--
            }

            if (tempTile != TileType.EMPTY) {
                earthTilesNumber.intValue++
            }

            if (endTrigger()) {
                return if (_board.count { it.type == TileType.DESTROYED } == 36) 1
                else if (_board.count { it.type == TileType.WATER } == 0) 2
                else 3
            }
            selectedTile = TileType.EMPTY
        }
        return 0
    }

    private val startingBoard = listOf(TileState(TileType.FIRE, Pair(0, 0)), TileState(TileType.FIRE, Pair(1, 0)),
        TileState(TileType.EMPTY, Pair(2, 0)), TileState(TileType.EMPTY, Pair(3, 0)),
        TileState(TileType.WATER, Pair(4, 0)), TileState(TileType.WATER, Pair(5, 0)),
        TileState(TileType.FIRE, Pair(0, 1)), TileState(TileType.EMPTY, Pair(1, 1)),
        TileState(TileType.EMPTY, Pair(2, 1)), TileState(TileType.EMPTY, Pair(3, 1)),
        TileState(TileType.EMPTY, Pair(4, 1)), TileState(TileType.WATER, Pair(5, 1)),
        TileState(TileType.EMPTY, Pair(0, 2)), TileState(TileType.EMPTY, Pair(1, 2)),
        TileState(TileType.FIRE, Pair(2, 2)), TileState(TileType.WATER, Pair(3, 2)),
        TileState(TileType.EMPTY, Pair(4, 2)), TileState(TileType.EMPTY, Pair(5, 2)),
        TileState(TileType.EMPTY, Pair(0, 3)), TileState(TileType.EMPTY, Pair(1, 3)),
        TileState(TileType.WATER, Pair(2, 3)), TileState(TileType.FIRE, Pair(3, 3)),
        TileState(TileType.EMPTY, Pair(4, 3)), TileState(TileType.EMPTY, Pair(5, 3)),
        TileState(TileType.WATER, Pair(0, 4)), TileState(TileType.EMPTY, Pair(1, 4)),
        TileState(TileType.EMPTY, Pair(2, 4)), TileState(TileType.EMPTY, Pair(3, 4)),
        TileState(TileType.EMPTY, Pair(4, 4)), TileState(TileType.FIRE, Pair(5, 4)),
        TileState(TileType.WATER, Pair(0, 5)), TileState(TileType.WATER, Pair(1, 5)),
        TileState(TileType.EMPTY, Pair(2, 5)), TileState(TileType.EMPTY, Pair(3, 5)),
        TileState(TileType.FIRE, Pair(4, 5)), TileState(TileType.FIRE, Pair(5, 5)))

    private val _board = startingBoard.toMutableList().toMutableStateList()

    val board: List<TileState>
        get() = _board
}