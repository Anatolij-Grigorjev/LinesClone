package com.tiem625.lines

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.tiem625.lines.assets.Assets
import com.tiem625.lines.constants.GameScreens
import com.tiem625.lines.constants.MenuItems
import com.tiem625.lines.event.EventSystem
import com.tiem625.lines.event.GameEventTypes
import com.tiem625.lines.dialog.InputNameDialog
import com.tiem625.lines.leaderboards.LeaderboardRecord
import com.tiem625.lines.leaderboards.LeaderboardStage
import com.tiem625.lines.stages.MainMenu
import com.tiem625.lines.stages.SplashGridStage
import com.tiem625.lines.stages.TilesGridStage
import com.tiem625.lines.stages.ui.GridHUD


class LinesGame : ApplicationAdapter() {

    lateinit var splashGridStage: SplashGridStage
    lateinit var mainMenuStage: MainMenu
    lateinit var tilesGridStage: TilesGridStage
    lateinit var gridHUD: GridHUD
    lateinit var leaderboardStage: LeaderboardStage
    lateinit var viewport: Viewport

    val currentScreenStages = mutableListOf<Stage>()
    var currentScreen = GameScreens.MAIN_MENU
        set(value) {
            currentScreenStages.clear()
            when (value) {
                GameScreens.MAIN_MENU -> {
                    currentScreenStages.add(splashGridStage)
                    currentScreenStages.add(mainMenuStage)
                }
                GameScreens.GAME_GRID -> {
                    currentScreenStages.add(tilesGridStage)
                    currentScreenStages.add(gridHUD)
                }
                GameScreens.LEADERBOARDS -> {
                    currentScreenStages.add(leaderboardStage)
                }
            }

            field = value
        }


    fun setupEventHandlers() {
        //setup menu listener
        EventSystem.addHandler(GameEventTypes.MENU_OPTION_SELECTED) { gameEvent ->

            val option = gameEvent.data as MenuItems

            when (option) {

                MenuItems.START_GAME -> {

                    createGameGrid()
                }
                MenuItems.VIEW_LEADERBOARDS -> {

                    createLeaderboards()
                }
                MenuItems.EXIT_GAME -> {
                    gameOver()
                }
            }
        }
        //game over listener (called from filled grid), show menu
        EventSystem.addHandler(GameEventTypes.GAME_OVER) { event ->

            val earnedPoints = event.data as Int

            if (earnedPoints >= GameRuntime.currentLowestHigh) {
                InputNameDialog(tilesGridStage).show()
            } else {
                createLeaderboards()
            }
        }

        EventSystem.addHandler(GameEventTypes.DIALOG_DISMISS) { event ->
            if (currentScreen == GameScreens.GAME_GRID) {
                createLeaderboards()
            }
        }

        EventSystem.addHandler(GameEventTypes.GRID_ESCAPE) { event ->
            resetGamePoints()
            createMenuScreen()
        }

        //add event handler
        EventSystem.addHandler(GameEventTypes.LEADERBOARD_ENTRY) { event ->

            val entry = event.data as Pair<String, Int>

            val firstSmallerIdx = GameRuntime.records.indexOfFirst { it.score <= entry.second }

            //if some values actually are smaller than this
            if (firstSmallerIdx >= 0) {

                //shit elements lower by one position down
                // (going other direction just copies value across array)
                (GameRuntime.records.size - 1 downTo firstSmallerIdx + 1).forEach { idx ->
                    GameRuntime.records[idx] = GameRuntime.records[idx - 1]
                }
                GameRuntime.records[firstSmallerIdx] = LeaderboardRecord(
                        name = entry.first,
                        score = entry.second
                )
            }

            GameRuntime.updateLowestHigh()
        }
    }

    private fun resetGamePoints() {
        GameRuntime.currentPoints = 0
        GameRuntime.currentPointsMultiplier = 1f
    }

    override fun create() {
        GridGlobals.refreshGridPositions()
        setupEventHandlers()

        viewport = FitViewport(
                GridGlobals.WORLD_WIDTH + Math.abs(GridGlobals.WORLD_OFFSET.first),
                GridGlobals.WORLD_HEIGHT + Math.abs(GridGlobals.WORLD_OFFSET.second))

        Assets.load()
        //create menu for first time
        createMenuScreen(fresh = true)
    }

    fun createMenuScreen(fresh: Boolean = false) {

        splashGridStage = SplashGridStage(viewport)
        mainMenuStage = MainMenu(viewport,
                splashGridStage.splashMoveTime
                        + splashGridStage.midBallDelay * splashGridStage.splashBalls.size
        )
        Gdx.input.inputProcessor = mainMenuStage

        //fresh means menu created first time not to get exception
        //for checking a lateinit
        if (!fresh) {
            disposeCurrentScreen()
        } else {
            //prepare list for initial menu stages
            currentScreenStages.clear()
        }
        currentScreen = GameScreens.MAIN_MENU
    }

    fun createLeaderboards() {

        leaderboardStage = LeaderboardStage(viewport)
        Gdx.input.inputProcessor = leaderboardStage

        disposeCurrentScreen()

        currentScreen = GameScreens.LEADERBOARDS
    }


    fun createGameGrid() {

        gridHUD = GridHUD(viewport)
        tilesGridStage = TilesGridStage(
                viewport,
                GridGlobals.WORLD_WIDTH,
                GridGlobals.WORLD_HEIGHT,
                GridGlobals.GRID_ROWS,
                GridGlobals.GRID_COLS,
                GridGlobals.WORLD_OFFSET
        )

        //initialize grid with some stuff
        (0 until 1).forEach { tilesGridStage.addNewBalls() }

        Gdx.input.inputProcessor = tilesGridStage

        disposeCurrentScreen()

        currentScreen = GameScreens.GAME_GRID
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, false)
    }

    override fun render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        currentScreenStages.forEach { stage ->
            stage.act(Gdx.graphics.deltaTime)
            stage.draw()
        }
    }

    override fun dispose() {

        disposeCurrentScreen()

        Assets.manager.dispose()
        GridGlobals.dispose()
    }

    fun disposeCurrentScreen() {
        currentScreenStages.forEach { stage -> stage.dispose() }
    }

    fun gameOver() {
        println("Balls done, game over...")
        Thread.sleep(1000)
        Gdx.app.exit()
    }
}
