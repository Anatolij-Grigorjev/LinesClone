package com.tiem625.lines

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Disposable
import com.tiem625.lines.actors.Ball
import com.tiem625.lines.actors.TileBallGroup
import com.tiem625.lines.stages.TilesGridStage

object GridGlobals : Disposable {

    override fun dispose() {
        skinRegularFont.dispose()
    }

    const val HUD_HEIGHT = 100.0f

    const val WORLD_WIDTH = 900.0f
    const val WORLD_HEIGHT = 800.0f

    const val GRID_WIDTH = 800f
    const val GRID_HEIGHT = 800f

    val GRID_OFFSET = (WORLD_WIDTH - GRID_WIDTH to -HUD_HEIGHT)

    const val GRID_ROWS = 8
    const val GRID_COLS = 8

    val TILE_NORMAL_COLOR: Color = Color.WHITE
    val TILE_SELECTED_COLOR: Color = Color.BLUE

    //how many balls get added in a turn
    const val TURN_NUM_BALLS = 5
    // num balls to align in a single pattern
    const val POP_NUM_BALLS = 4
    const val TILE_BALL_GUTTER = 0.0f
    //num of moves you can do while the multiplier is frozen for it to remain frozen
    const val MAX_FROZEN_MULTIPLIER_MOVES = 3
    //how many points you get for popping POP_NUM_BALLS amount of balls
    const val POINTS_PER_CHAIN = 150
    //how many points you get for every extra ball in chain above POP_NUM_BALLS
    const val POINTS_PER_EXTRA_BALL = 150
    //multiplier addition when on popping streak
    const val STREAK_MULTIPLIER_ADJUST = 0.5f
    //amount of multiplier frozen when progress bar is filled
    const val FREEZE_MULTIPLIER_VALUE = 5f
    const val MAX_BAR_PROGRESS = 2500f
    //number of positions on the leaderboards
    const val LEADERBOARD_POSITIONS = 15

    const val LEADERBOARD_FILENAME = "leaderboards.json"

    const val OPTIONS_FILENAME = "options.json"

    val gameSkin = Skin(Gdx.files.internal("skins/plain-james/plain-james-ui.json"))

    val glyphLayout: GlyphLayout = GlyphLayout()

    val skinRegularFont = gameSkin.getFont("font")
    val skinTitleFont = gameSkin.getFont("title")

    fun pointsDimensions(label: Label): Pair<Float, Float> =
            glyphLayout.let {
                it.setText(GridGlobals.skinRegularFont, label.text)
                (it.width to it.height)
            }

    val BALL_COLORS = listOf<Color>(
            Color.RED,
            Color.PURPLE,
            Color.BLUE,
            Color.YELLOW
    )

    //all ball positions used during the game. when this list runs out, its over
    //disappearing groups add their positions back into this
    lateinit var ballPositions: MutableList<Pair<Int, Int>>

    fun refreshGridPositions() {
        ballPositions = (0 until GRID_ROWS).map { rIdx ->
            (0 until GRID_COLS).map { cIdx ->
                rIdx to cIdx
            }
        }.flatten().toMutableList().shuffled()
    }

    /**
     * Test if two tileballgroups both either have or dont have a ball
     */
    fun sameBallState(g1: TileBallGroup, g2: TileBallGroup): Boolean =
            (g1.ball != null && g2.ball != null) ||
                    (g1.ball == null && g2.ball == null)

    fun attachBall(theBall: Ball, tileTo: TileBallGroup) {
        //if this tile already has a ball we log and return
        tileTo.ball?.let {
            println("TILE ${tileTo.gridPos} already has BALL ${it.color}! doing nothing!")
            return
        }

        //move ball and update positions
        theBall.resetPosition()
        tileTo.ball = theBall
        tileTo.ball!!.gridPos = tileTo.gridPos

        ballPositions.remove(tileTo.gridPos)
        ballPositions.shuffled()
    }

    fun removeBall(fromTile: TileBallGroup, toStage: TilesGridStage) {
        //if the tile doesn't have a ball, we log and return
        if (fromTile.ball == null) {
            println("TILE ${fromTile.gridPos} HAS NO BALL")
            return
        }

        val ball = fromTile.ball!!

        ball.setPosition(
                fromTile.gridPos.second * fromTile.tile.width,
                fromTile.gridPos.first * fromTile.tile.height
        )
        fromTile.ball = null
        toStage.gridGroup.addActor(ball)

        ballPositions.add(fromTile.gridPos)
        ballPositions.shuffled()
    }
}