package com.tiem625.lines

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.tiem625.lines.assets.Assets
import com.tiem625.lines.constants.GameScreens
import com.tiem625.lines.constants.MenuItems
import com.tiem625.lines.event.EventSystem
import com.tiem625.lines.event.GameEvent
import com.tiem625.lines.event.GameEventTypes
import com.tiem625.lines.leaderboards.InputNameDialog
import com.tiem625.lines.leaderboards.LeaderboardStage
import com.tiem625.lines.stages.MainMenu
import com.tiem625.lines.stages.SplashGridStage
import com.tiem625.lines.stages.TilesGridStage
import com.tiem625.lines.stages.ui.GridHUD


class LinesGame : ApplicationAdapter() {

    lateinit var tilesGridStage: TilesGridStage
    lateinit var splashGridStage: SplashGridStage
    lateinit var mainMenuStage: MainMenu
    lateinit var leaderboardStage: LeaderboardStage
    lateinit var gridHUD: GridHUD
    lateinit var viewport: Viewport

    var currentScreen = GameScreens.MAIN_MENU

    fun setupEventHandlers() {
        //setup menu listener
        EventSystem.addHandler(GameEventTypes.MENU_OPTION_SELECTED) { gameEvent ->

            val option = gameEvent.data as MenuItems

            when(option) {

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
            InputNameDialog(tilesGridStage).show()
        }

        EventSystem.addHandler(GameEventTypes.DIALOG_DISMISS) { event ->
            if (currentScreen == GameScreens.GAME_GRID) {
                createLeaderboards()
            }
        }
    }

    override fun create() {

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
        tilesGridStage.addListener(object : InputListener() {

            override fun keyUp(event: InputEvent?, keycode: Int): Boolean {
                when (keycode) {
                    Input.Keys.SPACE -> {
                        val haveBalls = tilesGridStage.addNewBalls()
                        if (!haveBalls) {
                            EventSystem.submitEvent(GameEventTypes.GAME_OVER, GameRuntime.currentPoints)
                        }
                    }
                    Input.Keys.D -> {
                        EventSystem.submitEvent(GameEventTypes.GAME_OVER)
                    }
                    Input.Keys.ESCAPE -> {
                        createMenuScreen()
                    }
                    else -> {
                        println("No handler for key $keycode")
                    }
                }

                return true
            }
        })

        disposeCurrentScreen()

        currentScreen = GameScreens.GAME_GRID
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, false)
    }

    override fun render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        when (currentScreen) {

            GameScreens.MAIN_MENU -> {

                splashGridStage.act(Gdx.graphics.deltaTime)
                mainMenuStage.act(Gdx.graphics.deltaTime)

                splashGridStage.draw()
                mainMenuStage.draw()
            }
            GameScreens.GAME_GRID -> {

                tilesGridStage.act(Gdx.graphics.deltaTime)
                gridHUD.act(Gdx.graphics.deltaTime)

                tilesGridStage.draw()
                gridHUD.draw()
            }
            GameScreens.LEADERBOARDS -> {

                leaderboardStage.act(Gdx.graphics.deltaTime)
                leaderboardStage.draw()
            }
        }
    }

    override fun dispose() {

        disposeCurrentScreen()

        Assets.manager.dispose()
        GridGlobals.dispose()
    }

    fun disposeCurrentScreen() {
        when (currentScreen) {

            GameScreens.MAIN_MENU -> {

                splashGridStage.dispose()
                mainMenuStage.dispose()
            }
            GameScreens.GAME_GRID -> {

                gridHUD.dispose()
                tilesGridStage.dispose()
            }
            GameScreens.LEADERBOARDS -> {

                leaderboardStage.dispose()
            }
        }
    }

    fun gameOver() {
        println("Balls done, game over...")
        Thread.sleep(1000)
        Gdx.app.exit()
    }
}
