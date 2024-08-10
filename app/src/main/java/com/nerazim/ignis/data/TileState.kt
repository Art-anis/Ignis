package com.nerazim.ignis.data

//типы тайлов
enum class TileType {
    EMPTY,
    WATER,
    FIRE,
    EARTH,
    AIR
}

//направление движения тайлов
enum class MoveDirection {
    LEFT,
    RIGHT,
    UP,
    DOWN,
    UNDEFINED
}

//состояния тайла
data class TileState(var type: TileType, val coordinates: Pair<Int, Int>)