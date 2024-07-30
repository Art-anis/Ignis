package com.nerazim.ignis.data


class BoardState {
    var startX = 0
    var startY = 0
    var endX = 5
    var endY = 5

    private fun cutRow() {

    }

    private fun cutColumn() {

    }

    fun shouldRemoveRow() {

    }

    fun shouldRemoveColumn() {

    }

    val board = mutableListOf(
        TileState(TileType.FIRE, Pair(0, 0)), TileState(TileType.FIRE, Pair(1, 0)),
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
        TileState(TileType.FIRE, Pair(4, 5)), TileState(TileType.FIRE, Pair(5, 5))
    )
}