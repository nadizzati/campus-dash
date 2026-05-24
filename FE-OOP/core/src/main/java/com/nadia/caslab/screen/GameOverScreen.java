package com.nadia.caslab.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.nadia.caslab.game.CampusDashGame;
import com.nadia.caslab.game.GameConstants;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

// Layar hasil akhir (WIN atau LOSE).
public class GameOverScreen implements Screen {

    private final CampusDashGame game;
    private final boolean playerWon;
    private final int coinsCollected;
    private final int timeRemaining;
    private final String reason;

    private OrthographicCamera camera;
    private FitViewport viewport;
    private BitmapFont bigFont;
    private BitmapFont midFont;
    private BitmapFont smallFont;

    private float animTimer = 0f;
    private int selectedOption = 0; // 0=MainLagi, 1=Leaderboard, 2=Menu
    private static final String[] OPTIONS = {"Main Lagi", "Leaderboard", "Menu Utama"};

    public GameOverScreen(CampusDashGame game, boolean playerWon,
                          int coinsCollected, int timeRemaining, String reason) {
        this.game           = game;
        this.playerWon      = playerWon;
        this.coinsCollected = coinsCollected;
        this.timeRemaining  = timeRemaining;
        this.reason         = reason;
    }

    @Override
    public void show() {
        camera   = new OrthographicCamera();
        viewport = new FitViewport(GameConstants.VIEWPORT_WIDTH, GameConstants.VIEWPORT_HEIGHT, camera);
        camera.setToOrtho(false, GameConstants.VIEWPORT_WIDTH, GameConstants.VIEWPORT_HEIGHT);

        bigFont   = new BitmapFont(); bigFont.getData().setScale(5f);
        midFont   = new BitmapFont(); midFont.getData().setScale(2f);
        smallFont = new BitmapFont(); smallFont.getData().setScale(1.5f);

    }

    @Override
    public void render(float delta) {
        animTimer += delta;
        handleInput();

        int W = GameConstants.VIEWPORT_WIDTH;
        int H = GameConstants.VIEWPORT_HEIGHT;

        // Background solid
        if (playerWon) {
            Gdx.gl.glClearColor(0.05f, 0.2f, 0.05f, 1f);
        } else {
            Gdx.gl.glClearColor(0.2f, 0.05f, 0.05f, 1f);
        }
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();

        game.batch.begin();

        // Judul
        bigFont.getData().setScale(5f);
        bigFont.setColor(playerWon ? Color.GREEN : Color.RED);
        String title = playerWon ? "BERHASIL!" : "GAGAL!";
        bigFont.draw(game.batch, title, W / 2f - 200, H - 80);

        // Statistik
        midFont.getData().setScale(2f);
        midFont.setColor(Color.WHITE);
        midFont.draw(game.batch,
            "Total Koin  :  " + coinsCollected + " / " + GameConstants.COINS_TO_WIN,
            W / 2f - 220, H / 2f + 60);

        int mins = timeRemaining / 60;
        int secs = timeRemaining % 60;
        midFont.draw(game.batch,
            String.format("Sisa Waktu  :  %02d:%02d", mins, secs),
            W / 2f - 220, H / 2f);

        // Tombol
        for (int i = 0; i < OPTIONS.length; i++) {
            float textY = H / 2f - 120 - (i * 60f);
            if (i == selectedOption) {
                smallFont.setColor(playerWon ? Color.GREEN : Color.RED);
                smallFont.getData().setScale(1.8f);
                smallFont.draw(game.batch, ">  " + OPTIONS[i], W / 2f - 100, textY);
            } else {
                smallFont.setColor(Color.LIGHT_GRAY);
                smallFont.getData().setScale(1.5f);
                smallFont.draw(game.batch, "   " + OPTIONS[i], W / 2f - 100, textY);
            }
        }

        // Instruksi
        float alpha = 0.4f + 0.4f * (float) Math.sin(animTimer * 2.5f);
        smallFont.setColor(new Color(0.6f, 0.6f, 0.6f, alpha));
        smallFont.getData().setScale(1.2f);
        smallFont.draw(game.batch, "Atas / Bawah   |   ENTER untuk pilih", W / 2f - 190, 50);

        game.batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectedOption = (selectedOption - 1 + OPTIONS.length) % OPTIONS.length;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedOption = (selectedOption + 1) % OPTIONS.length;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) ||
            Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            switch (selectedOption) {
                case 0:
                    game.setScreen(new GameScreen(game));
                    break;
                case 1:
                    game.setScreen(new LeaderboardScreen(game));
                    break;
                case 2:
                    game.setScreen(new MainMenuScreen(game));
                    break;
            }
            dispose();
        }
    }

    @Override public void resize(int w, int h) { viewport.update(w, h); }
    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void hide()   {}

    @Override
    public void dispose() {
        bigFont.dispose();
        midFont.dispose();
        smallFont.dispose();
    }
}
