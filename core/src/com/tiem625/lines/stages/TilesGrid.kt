package com.tiem625.lines.stages

import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.tiem625.lines.*
import com.tiem625.lines.actors.Ball
import com.tiem625.lines.actors.ReceivedPoints
import com.tiem625.lines.actors.Tile
import com.tiem625.lines.actors.TileBallGroup

class TilesGrid(val numRows: Int,
                val numCols: Int) : Stage(ExtendViewport(GridGlobals.WORLD_WIDTH, GridGlobals.WORLD_HEIGHT)) {

    val tileWidth: Float = viewport.worldWidth / numRows
    val tileHeight: Float = viewport.worldHeight / numCols

    var highlightOn = false

    val grid: Array<Array<TileBallGroup>> = (tileWidth to tileHeight).let { (tileWidth, tileHeight) ->

        Array(numRows) { rowIdx ->
            Array(numCols) { colIdx ->
                TileBallGroup(this, rowIdx to colIdx, Tile(tileWidth, tileHeight).apply {
                    zIndex = 0
                }).apply {
                    x = tileWidth * colIdx
                    y = tileHeight * rowIdx
                    tile.group = this
                    this@TilesGrid.addActor(this)
                }
            }
        }
    }

    val gridGraph = IndexedGridGraph(numRows, numCols, grid)
    val pathFinder = IndexedAStarPathFinder(gridGraph)


    fun toggleConnectionsHighlight() {
        val groups = gridGraph
                .connectionsMap
                .values
                .flatten()
                .map { con ->
                    listOf<TileBallGroup>(con.fromNode, con.toNode)
                }.flatten()
                .toSet()

        highlightOn = !highlightOn
        if (highlightOn) {
            highlightTileGroups(groups)
        } else {
            clearHighlight()
        }
    }

    fun toggleBallsHighlight() {
        val groups = grid.flatten().filter { it.ball != null }

        highlightOn = !highlightOn
        if (highlightOn) {
            highlightTileGroups(groups)
        } else {
            clearHighlight()
        }
    }

    private fun highlightTileGroups(tileGroups: Iterable<TileBallGroup>) {
        tileGroups.forEach {
            it.tile.color = GridGlobals.BALL_COLORS[2]
        }
    }

    private fun clearHighlight() {
        grid.flatten().forEach {
            it.tile.color = GridGlobals.TILE_NORMAL_COLOR
        }
    }

    private fun tileGroupAt(point: Pair<Int, Int>): TileBallGroup? =
            if (point.first in (0 until numRows)
                    && point.second in (0 until numCols))
                grid[point.first][point.second]
            else null

    /**
     * Try adding new balls at free shuffled positions on the board
     *
     * returns <code>true</code> if the amount of positions filled corresponds to the
     * expected turn amount (i.e. its not game over yet)
     *
     * <code>false</code> means number of free positions was lower than balls willing to fill them
     */
    fun addNewBalls(): Boolean {

        //take first N new ball positions
        val newPositions = GridGlobals.ballPositions.pop(GridGlobals.TURN_NUM_BALLS)
        //reshuffle smaller list
        GridGlobals.ballPositions.shuffled()

        newPositions.forEach { pos ->

            grid[pos.first][pos.second].ball = Ball(
                    width = tileWidth - GridGlobals.TILE_BALL_GUTTER,
                    height = tileHeight - GridGlobals.TILE_BALL_GUTTER,
                    color = GridGlobals.BALL_COLORS.random(),
                    gridPoxX = pos.first,
                    gridPosY = pos.second
            ).apply {
                zIndex = 999
            }
        }

        println("Empty grid positions: ${GridGlobals.ballPositions.size}")

        //change in topology, redoing paths
        if (newPositions.isNotEmpty()) {
            gridGraph.invalidateConnections()
        }

        //return if we had balls
        return newPositions.size == GridGlobals.TURN_NUM_BALLS
    }

    private fun removePoppedBalls(markedSurroundGroups: List<TileBallGroup>) {
        println("Found remove sequence: ${markedSurroundGroups.joinToString { it.gridPos.toString() }}")
        val positions: MutableList<Pair<Int, Int>> = mutableListOf()
        //create removable group
        Group().apply {
            //remove ball references and create actor remove actions
            //record ball positions
            val removeBallActions = markedSurroundGroups.mapNotNull { group ->
                group.ball?.let { ball ->
                    this.addActor(ball)
                    positions.add(ball.gridPos)
                    Actions.run {
                        println("Removing ball ${ball.gridPos}(${ball.color})...")
                        removeActor(ball)
                        group.ball = null
                    }
                }
            }
            //add remove actions in sequence + remove group itself
            addAction(Actions.sequence(
                    *removeBallActions.toTypedArray(),
                    Actions.run {
                        //add recorded positions back into potentials list, reshuffle
                        GridGlobals.ballPositions.addAll(positions)
                        GridGlobals.ballPositions.shuffled()
                        println("Empty grid positions: ${GridGlobals.ballPositions.size}")
                        gridGraph.invalidateConnections()
                    },
                    Actions.removeActor(this)
            ))
            this@TilesGrid.addActor(this)
        }
    }

    /**
     * Check if the grid has a pop-ready ball alignment in any direction by length
     *
     * if it does, pop it
     *
     * if it does not, generate new balls batch
     */
    fun checkGridUpdates(vararg aroundBalls: TileBallGroup) {

        aroundBalls.forEach { balledGroup ->
            //only do things if group has ball
            balledGroup.ball?.let { ball ->

                //search area around ball
                val markedSurroundGroups = (markAroundBall(ball) + balledGroup)

                if (markedSurroundGroups.size >= GridGlobals.POP_NUM_BALLS) {
                    //create points float above this moved ball
                    addActor(ReceivedPoints(
                            Pair(
                                    tileHeight * ball.gridPos.first,
                                    tileWidth * ball.gridPos.second
                            ),
                            150)
                    )
                    removePoppedBalls(markedSurroundGroups)
                } else {
                    //if this was false, its game over man!
                    if (!addNewBalls()) {
                        LinesGame.currentGame.gameOver()
                    }
                }
            }
        }
    }

    /**
     * Search grid tiles around supplied ball for tile-ball groups with balls of the same color.
     * Search will greedily mark and check tiles in the same direction until either the color chain is broken or
     * a wall is hit. At least N matching balls in a row will be required to induce a pop
     *
     * N is the constant GridGlobals.POP_NUM_BALLS
     */
    private fun markAroundBall(ball: Ball): List<TileBallGroup> {

        val collectedLists = listOf(
                //vertical
                (
                        collectAtOffset(ball.gridPos, 0 to 1, ball.color)
                                + collectAtOffset(ball.gridPos, 0 to -1, ball.color)
                        ),
                //horizontal
                (
                        collectAtOffset(ball.gridPos, -1 to 0, ball.color)
                                + collectAtOffset(ball.gridPos, 1 to 0, ball.color)
                        ),
                //slash
                (
                        collectAtOffset(ball.gridPos, -1 to -1, ball.color)
                                + collectAtOffset(ball.gridPos, 1 to 1, ball.color)
                        ),
                //reverse slash
                (
                        collectAtOffset(ball.gridPos, -1 to 1, ball.color)
                                + collectAtOffset(ball.gridPos, 1 to -1, ball.color)
                        )
        )
        //make sure lists are at least one solution large for pop (-1 due to origin ball not here)
        return collectedLists.filter { it.size >= GridGlobals.POP_NUM_BALLS - 1 }.flatten()
    }


    private fun collectAtOffset(origin: Pair<Int, Int>, offset: Pair<Int, Int>, color: Color): List<TileBallGroup> {

        var coef = 1
        var keepChecking = true
        val foundBalls = mutableListOf<TileBallGroup>()

        while (keepChecking) {
            keepChecking = false
            tileGroupAt(origin + (offset * coef))?.let { group ->
                group.ball?.let {
                    if (it.color == color) {
                        foundBalls.add(group)
                        keepChecking = true
                        coef++
                    }
                }
            }
        }

        return foundBalls
    }


}