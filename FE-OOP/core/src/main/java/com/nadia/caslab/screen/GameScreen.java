package com.nadia.caslab.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
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
import java.util.List;

// Screen utama gameplay Campus Dash.
public class GameScreen implements Screen {

    // Core
    private final CampusDashGame game;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private ShapeRenderer shapeRenderer;
    private Texture whitePixel; // Tambahan untuk drawRectangle

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
    private boolean gameOver = false;
    private boolean doorInteracted = false;
    private boolean showingWinScreen = false;
    private boolean showingLoseScreen = false;
    private int finalCoins = 0;
    private int finalTime = 0;

    private float endAnimTimer = 0f;
    private float alphaBackground = 0f; // Untuk fade-in
    private final GlyphLayout layout = new GlyphLayout();

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

        // Inisialisasi white pixel untuk background
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        whitePixel = new Texture(pixmap);
        pixmap.dispose();

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
        enemies.add(new Enemy(10 * GameConstants.TILE_SIZE, 3 * GameConstants.TILE_SIZE, Enemy.EnemyType.ASLAB, new RandomMovementStrategy()));
        enemies.add(new Enemy(5 * GameConstants.TILE_SIZE, 8 * GameConstants.TILE_SIZE, Enemy.EnemyType.ASLAB, new RandomMovementStrategy()));
        enemies.add(new Enemy(18 * GameConstants.TILE_SIZE, 10 * GameConstants.TILE_SIZE, Enemy.EnemyType.ASLAB, new RandomMovementStrategy()));
        enemies.add(new Enemy(8 * GameConstants.TILE_SIZE, 6 * GameConstants.TILE_SIZE, Enemy.EnemyType.ASLAB, new RandomMovementStrategy()));
        enemies.add(new Enemy(21 * GameConstants.TILE_SIZE, 5 * GameConstants.TILE_SIZE, Enemy.EnemyType.DOSEN, new PatrolMovementStrategy(PatrolMovementStrategy.PatrolAxis.VERTICAL, 2, 13)));

        // Audio
        SoundManager.getInstance().playBgMusic();

        // Backend Session
        ApiClient.startSession(game.playerStudentId, new ApiClient.ApiCallback() {
            @Override public void onSuccess(String body) { Gdx.app.log("API", "Sesi game dimulai: " + body); }
            @Override public void onFailure(String err) { Gdx.app.log("API", "Gagal mulai sesi (offline mode): " + err); }
        });
    }

    // Helper untuk menggambar kotak dengan SpriteBatch
    private void drawRectangle(float x, float y, float width, float height, Color color) {
        game.batch.setColor(color);
        game.batch.draw(whitePixel, x, y, width, height);
        game.batch.setColor(Color.WHITE);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.18f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();

        if (showingWinScreen || showingLoseScreen) {
            renderEndScreen();
            handleEndInput();
            return;
        }

        update(delta);
        if (!gameOver) renderGame();
    }

    private void update(float delta) {
        timeRemaining -= delta;
        if (timeRemaining <= 0) {
            timeRemaining = 0;
            handleLose("Waktu Habis! Deadline terlewat!");
            return;
        }

        if (!player.isMoving()) {
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
            }
        }

        player.update(delta);
        for (Enemy enemy : enemies) enemy.update(delta, player, tileMap.getMapData());

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
                coinFactory.respawnCoin(coin, player, enemies);
            }
        }

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

        if (!gameOver && !doorInteracted && player.getTileX() == tileMap.getDoorTileX() && player.getTileY() == tileMap.getDoorTileY()) {
            doorInteracted = true;
            doorContext.interact();
            return;
        }

        hud.update(delta, (int) timeRemaining);
    }

    private void renderGame() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        tileMap.render(shapeRenderer);
        for (Coin coin : coins) if (!coin.isCollected()) coin.render(shapeRenderer);
        for (Enemy enemy : enemies) enemy.render(shapeRenderer);
        player.render(shapeRenderer);
        shapeRenderer.end();
        hud.render(game.batch, shapeRenderer);
    }

    private void handleWin() {
        if (gameOver) return;
        gameOver = true;
        finalCoins = coinsCollected;
        finalTime = (int) timeRemaining;
        showingWinScreen = true;
        SoundManager.getInstance().playWin();
        SoundManager.getInstance().stopBgMusic();
        ApiClient.submitGameResult(game.playerStudentId, "COMPLETED", finalTime, finalCoins, 1, new ApiClient.ApiCallback() {
            @Override public void onSuccess(String body) { Gdx.app.log("API", "Win: " + body); }
            @Override public void onFailure(String err)  { Gdx.app.log("API", "err: " + err); }
        });
    }

    private void handleLose(String reason) {
        if (gameOver) return;
        gameOver = true;
        finalCoins = coinsCollected;
        finalTime = (int) timeRemaining;
        showingLoseScreen = true;
        SoundManager.getInstance().playLose();
        SoundManager.getInstance().stopBgMusic();
        String status = reason.contains("Waktu") ? "TIMEOUT" : "FAILED";
        ApiClient.submitGameResult(game.playerStudentId, status, finalTime, finalCoins, 1, new ApiClient.ApiCallback() {
            @Override public void onSuccess(String body) { Gdx.app.log("API", "Lose: " + body); }
            @Override public void onFailure(String err)  { Gdx.app.log("API", "err: " + err); }
        });
    }

    private int endSelected = 0;
    private static final String[] END_OPTIONS = {"Main Lagi", "Leaderboard", "Menu Utama"};

    private void renderEndScreen() {
        endAnimTimer += Gdx.graphics.getDeltaTime();
        alphaBackground = Math.min(1f, alphaBackground + Gdx.graphics.getDeltaTime() * 2f);

        int W = GameConstants.VIEWPORT_WIDTH;
        int H = GameConstants.VIEWPORT_HEIGHT;
        float cx = W / 2f;

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Background sama seperti MainMenu
        shapeRenderer.setColor(new Color(0.12f, 0.10f, 0.08f, alphaBackground * 0.95f));
        shapeRenderer.rect(0, 0, W, H);

        // Floating coins background (sama seperti MainMenu)
        for (int i = 0; i < 15; i++) {
            float px = (W / 15f) * i + (float) Math.sin(endAnimTimer * 0.5f + i * 1.5f) * 80f;
            float py = (H / 15f) * i + (float) Math.cos(endAnimTimer * 0.8f + i) * 80f;
            float bounce = (float) Math.sin(endAnimTimer * 3f + i) * 5f;
            shapeRenderer.setColor(new Color(1f, 0.84f, 0f, 0.25f));
            shapeRenderer.circle(px, py + bounce, 12f);
            shapeRenderer.setColor(new Color(1f, 0.95f, 0.5f, 0.25f));
            shapeRenderer.circle(px - 2f, py + bounce + 2f, 6f);
        }

        // Panel
        float menuBoxWidth  = 480f;
        float menuBoxHeight = 260f;
        float menuBoxX = cx - menuBoxWidth / 2f;
        float menuBoxY = (H / 2f) - (menuBoxHeight / 2f) - 30f;

        float rowHeight = 75f;
        float btnHeight = 55f;
        float totalMenuHeight = END_OPTIONS.length * rowHeight;
        float panelCenterY = menuBoxY + (menuBoxHeight / 2f);
        float startRowY = panelCenterY + (totalMenuHeight / 2f) - rowHeight;

        // Panel background
        shapeRenderer.setColor(new Color(0.16f, 0.13f, 0.11f, 0.98f));
        shapeRenderer.rect(menuBoxX, menuBoxY, menuBoxWidth, menuBoxHeight);

        // Border emas kiri
        shapeRenderer.setColor(new Color(0.85f, 0.65f, 0.25f, 1f));
        shapeRenderer.rect(menuBoxX, menuBoxY, 5, menuBoxHeight);

        // Highlight emas pada item yang dipilih
        float selectedRowCenterY = startRowY - (endSelected * rowHeight) + (rowHeight / 2f);
        float highlightRectY = selectedRowCenterY - (btnHeight / 2f);
        shapeRenderer.setColor(new Color(0.85f, 0.65f, 0.25f, 1f));
        shapeRenderer.rect(menuBoxX + 25, highlightRectY, menuBoxWidth - 50, btnHeight);

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        // Judul
        font.getData().setScale(4.0f);
        font.setColor(new Color(0.85f, 0.65f, 0.25f, 1f));
        String titleText = showingWinScreen ? "LEVEL SELESAI!" : "GAGAL!";
        layout.setText(font, titleText);
        font.draw(game.batch, layout, cx - layout.width / 2f, H - 100);

        // Statistik
        font.getData().setScale(1.5f);
        font.setColor(new Color(0.9f, 0.8f, 0.6f, 1f));
        layout.setText(font, "Coins : " + finalCoins + " / " + GameConstants.COINS_TO_WIN);
        font.draw(game.batch, layout, cx - layout.width / 2f, H - 165);

        layout.setText(font, "Time  : " + String.format("%02d:%02d", finalTime / 60, finalTime % 60));
        font.draw(game.batch, layout, cx - layout.width / 2f, H - 200);

        // Menu items
        String[] labels = {"Play Again", "Leaderboard", "Main Menu"};
        font.getData().setScale(2.0f);
        for (int i = 0; i < labels.length; i++) {
            font.setColor(i == endSelected ? Color.BLACK : Color.WHITE);
            layout.setText(font, labels[i]);
            float rowCenterY = startRowY - (i * rowHeight) + (rowHeight / 2f);
            float textY = rowCenterY + (layout.height / 2f);
            font.draw(game.batch, layout, cx - layout.width / 2f, textY);
        }

        // Prompt
        font.getData().setScale(1.6f);
        font.setColor(new Color(0.85f, 0.65f, 0.25f, 1f));
        layout.setText(font, "Press ENTER or SPACE to continue");
        font.draw(game.batch, layout, cx - layout.width / 2f, menuBoxY - 50);

        game.batch.end();
    }

    private void handleEndInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W))
            endSelected = (endSelected - 1 + 3 + 3) % 3; // Menggunakan 3 karena jumlah menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S))
            endSelected = (endSelected + 1) % 3;

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (endSelected == 0) game.setScreen(new GameScreen(game));
            else if (endSelected == 1) game.setScreen(new LeaderboardScreen(game));
            else game.setScreen(new MainMenuScreen(game));
            dispose();
        }
    }

    @Override public void resize(int w, int h) { viewport.update(w, h); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override
    public void dispose() {
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (whitePixel != null) whitePixel.dispose();
        if (hud != null) hud.dispose();
        if (font != null) font.dispose();
    }
}
