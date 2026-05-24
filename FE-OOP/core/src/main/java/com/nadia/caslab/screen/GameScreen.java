package com.nadia.caslab.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.nadia.caslab.command.Command;
import com.nadia.caslab.command.InputHandler;
import com.nadia.caslab.entity.*;
import com.nadia.caslab.factory.CoinFactory;
import com.nadia.caslab.game.CampusDashGame;
import com.nadia.caslab.game.GameConstants;
import com.nadia.caslab.manager.SoundManager;
import com.nadia.caslab.network.ApiClient;
import com.nadia.caslab.observer.GameEventManager;
import com.nadia.caslab.state.DoorContext;
import com.nadia.caslab.strategy.*;
import com.nadia.caslab.ui.HUD;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// Screen utama gameplay Campus Dash.
public class GameScreen implements Screen {

    // Core
    private final CampusDashGame game;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private ShapeRenderer shapeRenderer;

    // Design Pattern Objects
    private GameEventManager eventManager;    // Observer Pattern
    private DoorContext doorContext;          // State Pattern
    private CoinFactory coinFactory;          // Factory Method
    private InputHandler inputHandler;        // Command Pattern

    // Entities
    private Player player;
    private TileMap tileMap;
    private List<Coin> coins;
    private List<Enemy> enemies;

    // UI
    private HUD hud;                          // Observer (listener)
    private BitmapFont font;

    // Game State
    private int coinsCollected = 0;
    private float timeRemaining = GameConstants.GAME_TIME_SEC;

    // Win + Lose overlay
    private boolean gameOver    = false;

    public GameScreen(CampusDashGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        // Camera & Viewport
        camera   = new OrthographicCamera();
        viewport = new FitViewport(GameConstants.VIEWPORT_WIDTH, GameConstants.VIEWPORT_HEIGHT, camera);
        camera.setToOrtho(false, GameConstants.VIEWPORT_WIDTH, GameConstants.VIEWPORT_HEIGHT);
        shapeRenderer = new ShapeRenderer();
        font          = new BitmapFont();
        font.getData().setScale(2f);

        // Observer Pattern Setup
        eventManager = new GameEventManager();
        hud          = new HUD();
        eventManager.addObserver(hud);

        // State Pattern. Door
        doorContext = new DoorContext(GameConstants.COINS_TO_WIN);
        doorContext.setOnWinCallback(this::handleWin);

        // Map
        tileMap = new TileMap(doorContext);

        // Factory Method, Spawn Koin
        coinFactory = new CoinFactory(tileMap.getMapData());
        coins = coinFactory.spawnBatch(10);

        // Player
        player       = new Player(2, 2);
        inputHandler = new InputHandler(player);

        // Strategy Pattern, Enemy Setup
        enemies = new ArrayList<>();

        enemies.add(new Enemy(
            10 * GameConstants.TILE_SIZE, 3 * GameConstants.TILE_SIZE,
            Enemy.EnemyType.ASLAB,
            new RandomMovementStrategy()
        ));

        enemies.add(new Enemy(
            5 * GameConstants.TILE_SIZE, 8 * GameConstants.TILE_SIZE,
            Enemy.EnemyType.ASLAB,
            new RandomMovementStrategy()
        ));

        enemies.add(new Enemy(
            18 * GameConstants.TILE_SIZE, 10 * GameConstants.TILE_SIZE,
            Enemy.EnemyType.ASLAB,
            new RandomMovementStrategy()
        ));

        enemies.add(new Enemy(
            8 * GameConstants.TILE_SIZE, 6 * GameConstants.TILE_SIZE,
            Enemy.EnemyType.ASLAB,
            new RandomMovementStrategy()
        ));

        enemies.add(new Enemy(
            21 * GameConstants.TILE_SIZE, 5 * GameConstants.TILE_SIZE,
            Enemy.EnemyType.DOSEN,
            new PatrolMovementStrategy(PatrolMovementStrategy.PatrolAxis.VERTICAL, 2, 13)
        ));

        // Audio
        SoundManager.getInstance().playBgMusic();

        // Backend Session
        ApiClient.startSession(game.playerStudentId, new ApiClient.ApiCallback() {
            @Override public void onSuccess(String body) {
                Gdx.app.log("API", "Sesi game dimulai: " + body);
            }
            @Override public void onFailure(String err) {
                Gdx.app.log("API", "Gagal mulai sesi (offline mode): " + err);
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.18f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (gameOver) return; // stop semua render setelah game over

        camera.update();
        update(delta);

        if (!gameOver) renderGame(); // hanya render jika masih belum game over
    }

    // Update Logix
    private void update(float delta) {
        timeRemaining -= delta;
        if (timeRemaining <= 0) {
            timeRemaining = 0;
            handleLose("Waktu Habis! Deadline terlewat!");
            return;
        }

        // Input
        if (!player.isMoving()) {
            Command cmd = inputHandler.handleInput(
                InputHandler.isUpPressed(),
                InputHandler.isDownPressed(),
                InputHandler.isLeftPressed(),
                InputHandler.isRightPressed()
            );
            if (cmd != null) {
                Player.Direction dir = null;
                int targetX = player.getTileX();
                int targetY = player.getTileY();

                if (InputHandler.isUpPressed())         { targetY++; dir = Player.Direction.UP; }
                else if (InputHandler.isDownPressed())  { targetY--; dir = Player.Direction.DOWN; }
                else if (InputHandler.isLeftPressed())  { targetX--; dir = Player.Direction.LEFT; }
                else if (InputHandler.isRightPressed()) { targetX++; dir = Player.Direction.RIGHT; }

                if (dir != null) {
                    player.setFacingDirection(dir);
                    player.requestMove(targetX, targetY, tileMap.getMapData(), doorContext);
                    // cmd.execute() dihapus — sudah ditangani requestMove
                }
            }
        }

        // Update player
        player.update(delta);

        // Update semua enemy
        for (Enemy enemy : enemies) {
            enemy.update(delta, player, tileMap.getMapData());
        }

        // Collision: Player vs Coins
        for (Coin coin : coins) {
            coin.update(delta);
            if (coin.isCollidingWith(player.getTileX(), player.getTileY())) {
                coinsCollected++;
                eventManager.notifyCoinCollected(coinsCollected);
                SoundManager.getInstance().playCoinPickup();
                doorContext.checkUnlock(coinsCollected);
                if (doorContext.isOpen() && coinsCollected == GameConstants.COINS_TO_WIN) {
                    eventManager.notifyDoorUnlocked();
                    SoundManager.getInstance().playDoorOpen();
                }
                // Object pooling, reuse coin, pindahkan ke posisi baru
                coinFactory.respawnCoin(coin, player, enemies);
            }
        }

        // Collision: Player vs Enemy
        for (Enemy enemy : enemies) {
            if (enemy.isCollidingWithPlayer(player) && player.canBeHit()) {
                timeRemaining = Math.max(0, timeRemaining - GameConstants.TIME_PENALTY);
                player.hitByEnemy();
                enemy.triggerHitCooldown();
                eventManager.notifyEnemyHit((int) timeRemaining);
                SoundManager.getInstance().playEnemyHit();
                if (timeRemaining <= 0) {
                    handleLose("Tertangkap! Waktu habis!");
                    return;
                }
            }
        }

        // Collision: Player vs Door
        if (!gameOver &&
            player.getTileX() == tileMap.getDoorTileX() &&
            player.getTileY() == tileMap.getDoorTileY()) {
            doorContext.interact();
            return; // stop update setelah interact
        }

        hud.update(delta, (int) timeRemaining);
    }

    // RENDER
    private void renderGame() {
        shapeRenderer.setProjectionMatrix(camera.combined);

        // Filled shapes: tiles, coins, player, enemies
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Render map
        tileMap.render(shapeRenderer);

        // Render koin
        for (Coin coin : coins) {
            if (!coin.isCollected()) coin.render(shapeRenderer);
        }

        // Render enemies
        for (Enemy enemy : enemies) {
            enemy.render(shapeRenderer);
        }

        // Render player
        player.render(shapeRenderer);

        shapeRenderer.end();

        // Render HUD
        hud.render(game.batch, shapeRenderer);
    }

    // Win + Lose Handlers
    private void handleWin() {
        if (gameOver) return;
        gameOver = true;
        SoundManager.getInstance().playWin();
        SoundManager.getInstance().stopBgMusic();
        final int coins = coinsCollected;
        final int time  = (int) timeRemaining;
        ApiClient.submitGameResult(game.playerStudentId, "COMPLETED", time, coins, 1,
            new ApiClient.ApiCallback() {
                @Override public void onSuccess(String body) { Gdx.app.log("API", "Win: " + body); }
                @Override public void onFailure(String err)  { Gdx.app.log("API", "err: " + err); }
            }
        );
        Gdx.app.postRunnable(() ->
            game.setScreen(new GameOverScreen(game, true, coins, time, null))
        );
    }

    private void handleLose(String reason) {
        if (gameOver) return;
        gameOver = true;
        SoundManager.getInstance().playLose();
        SoundManager.getInstance().stopBgMusic();
        final int coins = coinsCollected;
        final int time  = (int) timeRemaining;
        final String r  = reason;
        String status   = reason.contains("Waktu") ? "TIMEOUT" : "FAILED";
        ApiClient.submitGameResult(game.playerStudentId, status, time, coins, 1,
            new ApiClient.ApiCallback() {
                @Override public void onSuccess(String body) { Gdx.app.log("API", "Lose: " + body); }
                @Override public void onFailure(String err)  { Gdx.app.log("API", "err: " + err); }
            }
        );
        Gdx.app.postRunnable(() ->
            game.setScreen(new GameOverScreen(game, false, coins, time, r))
        );
    }

    private void returnToMenu() {
        game.setScreen(new MainMenuScreen(game));
    }

    // Screen lifecycle
    @Override public void resize(int w, int h) { viewport.update(w, h); }
    @Override public void pause()   {}
    @Override public void resume()  {}
    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
            shapeRenderer = null;
        }
        if (hud != null) {
            hud.dispose();
            hud = null;
        }
        if (font != null) {
            font.dispose();
            font = null;
        }
    }
}
