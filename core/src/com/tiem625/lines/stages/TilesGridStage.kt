package com.tiem625.lines.stages

import com.badlogic.gdx.Input
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.viewport.Viewport
import com.tiem625.lines.*
import com.tiem625.lines.actors.Ball
import com.tiem625.lines.actors.ReceivedPoints
import com.tiem625.lines.actors.Tile
import com.tiem625.lines.actors.TileBallGroup
import com.tiem625.lines.assets.AudioPlayer
import com.tiem625.lines.constants.GameScreens
import com.tiem625.lines.constants.SoundFx
import com.tiem625.lines.dialog.LinesGameDialog
import com.tiem625.lines.event.EventSystem
import com.tiem625.lines.event.GameEventTypes

open class TilesGridStage(
        viewport: Viewport,
        gridWidth: Float,
        gridHeight: Float,
        val numRows: Int,
        val numCols: Int,
        val offset: Pair<Float, Float> = (0.0f to 0.0f)
) : Stage(viewport) {

    val tileWidth: Float = gridWidth / numCols
    val tileHeight: Float = gridHeight / numRows

    val gridGroup = Group()

    var ballMoving = false

    val grid: Array<Array<TileBallGroup>> = (tileWidth to tileHeight).let { (tileWidth, tileHeight) ->

        Array(numRows) { rowIdx ->
            Array(numCols) { colIdx ->
                TileBallGroup(this, rowIdx to colIdx, Tile(tileWidth, tileHeight).apply {
                    zIndex = 0
                }).apply {
                    gridGroup.addActor(this)
                    x = tileWidth * colIdx
                    y = tileHeight * rowIdx
                    tile.group = this
                }
            }
        }
    }

    init {
        gridGroup.apply {
            x += offset.first
            if (offset.second > 0) {
                y += offset.second
            }
            this@TilesGridStage.addActor(this)
        }

        addListener(object : InputListener() {

            override fun keyUp(event: InputEvent?, keycode: Int): Boolean {
                //ignore input while any dialogs are showing
                if (LinesGameDialog.dialogIsShowing) {
                    return true
                }

                when (keycode) {
                    Input.Keys.SPACE -> {
                        val haveBalls = addNewBalls()
                        if (!haveBalls) {
                            GridGlobals.refreshGridPositions()
                            EventSystem.submitEvent(GameEventTypes.GAME_OVER, GameRuntime.currentPoints)
                        }
                    }
                    Input.Keys.ESCAPE -> {
                        EventSystem.submitEvent(GameEventTypes.STAGE_ESCAPE, GameScreens.GAME_GRID)
                    }
                    else -> {
                        println("No handler for key $keycode")
                    }
                }

                return true
            }
        })
    }

    val gridGraph = IndexedGridGraph(numRows, numCols, grid)
    val pathFinder = IndexedAStarPathFinder(gridGraph)

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

        //change in topology, redoing paths and scanning for poppers penguins
        if (newPositions.isNotEmpty()) {
            gridGraph.invalidateConnections()
            checkGridUpdates(
                    *newPositions.map { grid[it.first][it.second] }.toTypedArray(),
                    addNewBalls = false
            )
        }

        //return if we had balls
        return newPositions.size == GridGlobals.TURN_NUM_BALLS
    }

    private fun removePoppedBalls(markedSurroundGroups: List<TileBallGroup>) {
        println("Found remove sequence: ${markedSurroundGroups.joinToString { it.gridPos.toString() }}")
        val positions: MutableList<Pair<Int, Int>> = mutableListOf()
        AudioPlayer.playSfx(SoundFx.BALLS_POP)
        //create removable group
        Group().apply {
            //remove ball references and create actor remove actions
            //record ball positions
            val removeBallActions = markedSurroundGroups.mapNotNull { group ->
                group.ball?.let { ball ->
                    positions.add(ball.gridPos)
                    Actions.run {
                        removeActor(ball)
                        group.ball = null
                        ball.addAction(Actions.removeActor())
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
            gridGroup.addActor(this)
        }
    }

    /**
     * Check if the grid has a pop-ready ball alignment in any direction by length
     *
     * if it does, pop it
     *
     * if it does not, generate new balls batch
     */
    fun checkGridUpdates(vararg aroundBalls: TileBallGroup, addNewBalls: Boolean = true) {

        aroundBalls.forEach { balledGroup ->
            //only do things if group has ball
            balledGroup.ball?.let { ball ->

                //search area around ball
                val markedSurroundGroups = (markAroundBall(ball) + balledGroup)

                if (markedSurroundGroups.size >= GridGlobals.POP_NUM_BALLS) {
                    if (!GameRuntime.justPoppedBalls) {
                        GameRuntime.justPoppedBalls = true
                        GameRuntime.currentPointsMultiplier = 1.0f
                    } else {
                        GameRuntime.currentPointsMultiplier += GridGlobals.STREAK_MULTIPLIER_ADJUST
                    }
                    println("Creating received points at ${ball.gridPos}")
                    //create points float above this moved ball
                    gridGroup.addActor(ReceivedPoints(
                            tileWidth * balledGroup.gridPos.second
                                    to
                                    tileHeight * balledGroup.gridPos.first
                            ,
                            tileWidth to tileHeight,
                            //score provided without multiplier,
                            // that's applied globally later in object itself
                            GridGlobals.POINTS_PER_CHAIN +
                                    (markedSurroundGroups.size - GridGlobals.POP_NUM_BALLS) * GridGlobals.POINTS_PER_EXTRA_BALL,
                            ball.color)
                    )
                    removePoppedBalls(markedSurroundGroups)
                } else {
                    //not enough balls, resetting streak
                    GameRuntime.justPoppedBalls = false
                    GameRuntime.currentPointsMultiplier = 1.0f

                    if (addNewBalls) {
                        //if this was false, its game over man!
                        if (!addNewBalls()) {
                            //reset ball positions
                            GridGlobals.refreshGridPositions()
                            EventSystem.submitEvent(
                                    GameEventTypes.GAME_OVER,
                                    GameRuntime.currentPoints
                            )
                        }
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