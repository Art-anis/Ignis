package com.nerazim.ignis.data

enum class TileType {
    EMPTY,
    WATER,
    FIRE,
    EARTH,
    AIR
}

enum class MoveDirection {
    LEFT,
    RIGHT,
    UP,
    DOWN,
    UNDEFINED
}

class TileState(var type: TileType, val coordinates: Pair<Int, Int>) {
    fun onClick(newTile: TileType, direction: MoveDirection) {
    }
}