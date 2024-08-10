package com.nerazim.ignis.data

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel

//viewModel поля
class BoardViewModel: ViewModel() {
    //крайние координаты
    var startX = 0
    var startY = 0
    var endX = 5
    var endY = 5

    //удаление ряда
    private fun cutRow() {

    }

    //удаление столбца
    private fun cutColumn() {

    }

    //проверка, надо ли удалять ряд
    fun shouldRemoveRow() {

    }

    //проверка, надо ли удалять столбец
    fun shouldRemoveColumn() {

    }

    //реакция на нажатие на тайл
    fun onClick(newTile: TileType, coordinates: Pair<Int, Int>, direction: MoveDirection): Boolean {
        //извлекаем ряд или столбец, который будем двигать
        val list: MutableList<TileState> = when (direction) {
            MoveDirection.UP -> board.filter { it.coordinates.first == coordinates.first }.reversed().toMutableList()
            MoveDirection.DOWN -> board.filter { it.coordinates.first == coordinates.first }.toMutableList()
            MoveDirection.LEFT -> board.filter { it.coordinates.second == coordinates.second }.reversed().toMutableList()
            MoveDirection.RIGHT -> board.filter { it.coordinates.second == coordinates.second }.toMutableList()
            MoveDirection.UNDEFINED -> {
                return false
            }
        }
        //если пустых мест нет, проверяем, можно ли вытолкнуть крайний тайл
        //нельзя вытолкнуть землю, а воздух можно вытолкнуть только землей
        if (list.count { it.type == TileType.EMPTY } == 0 &&
            (list.last().type == TileType.EARTH || list.last().type == TileType.AIR && list[list.size - 2].type != TileType.EARTH)) {
            return false
        }
        else {
            //количество сдвигов
            var iterationNumber = list.indexOf(list.find { it.type == TileType.EMPTY})
            //ставим в максимум, если нет пустых тайлов
            if (iterationNumber == -1) {
                //TODO: размер листа всегда 6, нужно считать либо пустого, либо до края (который может меняться)
                iterationNumber = list.size - 1
            }
            //шаг, с которым мы будем двигаться по изначальному списку, чтобы обновлять тайлы
            val step = when (direction) {
                MoveDirection.UP -> startX - endX - 1
                MoveDirection.DOWN -> endX - startX + 1
                MoveDirection.LEFT -> -1
                MoveDirection.RIGHT -> 1
                else -> return false
            }
            //переменные для свапа
            var tempTile = newTile
            var temp: TileType

            //стартовый индекс считаем по координатам
            var currentIndex = (endX - startX + 1) * coordinates.second + coordinates.first
            //сдвиг массива
            for (i in 0 until iterationNumber) {
                temp = _board[currentIndex].type
                _board[currentIndex] = _board[currentIndex].copy(type = tempTile)
                tempTile = temp
                currentIndex += step
            }
            //ставим тайл на последнюю ячейку
            //TODO: добавить условие на выход с поля
            _board[currentIndex] = _board[currentIndex].copy(type = tempTile)
        }
        return true
    }

    private val _board = listOf(TileState(TileType.FIRE, Pair(0, 0)), TileState(TileType.FIRE, Pair(1, 0)),
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
        TileState(TileType.FIRE, Pair(4, 5)), TileState(TileType.FIRE, Pair(5, 5))).toMutableStateList()

    val board: List<TileState>
        get() = _board
}