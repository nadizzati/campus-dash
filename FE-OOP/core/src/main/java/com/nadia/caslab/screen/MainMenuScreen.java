package com.nadia.caslab.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.nadia.caslab.game.CampusDashGame;
import com.nadia.caslab.game.GameConstants;

public class MainMenuScreen implements Screen {

    private final CampusDashGame game;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private ShapeRenderer shape;

    private BitmapFont titleFont;
    private BitmapFont subtitleFont;
    private BitmapFont menuFont;
    private BitmapFont infoFont;

    private GlyphLayout layout;

    private float animTimer = 0f;
    private int selectedOption = 0;
    private static final String[] MENU_ITEMS = {"Mulai Game", "Leaderboard", "Keluar"};

    public MainMenuScreen(CampusDashGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        camera   = new OrthographicCamera();
        viewport = new FitViewport(GameConstants.VIEWPORT_WIDTH, GameConstants.VIEWPORT_HEIGHT, camera);
        camera.setToOrtho(false, GameConstants.VIEWPORT_WIDTH, GameConstants.VIEWPORT_HEIGHT);

        shape = new ShapeRenderer();
        layout = new GlyphLayout();

        titleFont    = new BitmapFont(); titleFont.getData().setScale(5.5f);
        subtitleFont = new BitmapFont(); subtitleFont.getData().setScale(1.5f);
        menuFont     = new BitmapFont(); menuFont.getData().setScale(2.0f);
        infoFont     = new BitmapFont(); infoFont.getData().setScale(1.4f);
    }

    @Override
    public void render(float delta) {
        animTimer += delta;
        handleInput();

        int W = GameConstants.VIEWPORT_WIDTH;
        int H = GameConstants.VIEWPORT_HEIGHT;
        float centerX = W / 2f;

        // bg
        Gdx.gl.glClearColor(0.06f, 0.06f, 0.10f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        shape.setProjectionMatrix(camera.combined);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shape.begin(ShapeRenderer.ShapeType.Filled);

        shape.setColor(new Color(0.2f, 0.5f, 1.0f, 0.3f));
        shape.rect(0, H - 6, W, 6);
        shape.rect(0, 0, W, 6);

        // Panel Header (Atas)
        float headerWidth = W * 0.8f;
        float headerX = centerX - (headerWidth / 2f);
        shape.setColor(new Color(0.08f, 0.15f, 0.30f, 0.85f));
        shape.rect(headerX, H - 220, headerWidth, 180);

        shape.setColor(new Color(0.2f, 0.5f, 1.0f, 0.8f));
        shape.rect(headerX, H - 220, 6, 180);

        // Panel Menu (Tengah)
        float menuBoxWidth = 500f;
        float menuBoxHeight = 220f;
        float menuBoxX = centerX - (menuBoxWidth / 2f);
        float menuBoxY = (H / 2f) - (menuBoxHeight / 2f) + 20; // Sedikit dinaikkan

        shape.setColor(new Color(0.08f, 0.10f, 0.20f, 0.85f));
        shape.rect(menuBoxX, menuBoxY, menuBoxWidth, menuBoxHeight);
        shape.setColor(new Color(0.2f, 0.5f, 1.0f, 0.4f));
        shape.rect(menuBoxX, menuBoxY, menuBoxWidth, 4);                   // Garis bawah
        shape.rect(menuBoxX, menuBoxY + menuBoxHeight - 4, menuBoxWidth, 4); // Garis atas

        // Highlight tombol aktif
        float btnW = 460f;
        float btnH = 55f;
        float btnX = centerX - (btnW / 2f);
        // Kalkulasi Y untuk highlight agar pas dengan teks
        float startMenuY = menuBoxY + menuBoxHeight - 50f;
        float btnY = startMenuY - (selectedOption * 65f) - (btnH / 1.5f);

        // Efek pulse pada highlight
        float pulse = 0.5f + 0.1f * MathUtils.sin(animTimer * 6f);
        shape.setColor(new Color(0.2f, 0.45f, 0.9f, pulse));
        shape.rect(btnX, btnY, btnW, btnH);

        // Panel Info (Bawah)
        float infoWidth = W * 0.8f;
        float infoX = centerX - (infoWidth / 2f);
        shape.setColor(new Color(0.06f, 0.10f, 0.18f, 0.85f));
        shape.rect(infoX, 50, infoWidth, 120);
        shape.setColor(new Color(0.2f, 0.5f, 1.0f, 0.5f));
        shape.rect(infoX, 50, 6, 120);

        shape.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // Render teks
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        // Judul Utama
        titleFont.setColor(new Color(0.35f, 0.75f, 1.0f, 1f));
        layout.setText(titleFont, "CAMPUS DASH");
        titleFont.draw(game.batch, layout, centerX - (layout.width / 2f), H - 70);

        // Subtitle
        subtitleFont.getData().setScale(2.0f);
        subtitleFont.setColor(new Color(1f, 0.85f, 0.25f, 1f));
        layout.setText(subtitleFont, "Deadline Pursuit");
        subtitleFont.draw(game.batch, layout, centerX - (layout.width / 2f), H - 135);

        // Deskripsi Misi
        subtitleFont.getData().setScale(1.4f);
        subtitleFont.setColor(new Color(0.75f, 0.85f, 1.0f, 1f));
        String missionText = "Kumpulkan " + GameConstants.COINS_TO_WIN + " koin sebelum pukul 23:59!";
        layout.setText(subtitleFont, missionText);
        subtitleFont.draw(game.batch, layout, centerX - (layout.width / 2f), H - 175);

        // Menu Items
        for (int i = 0; i < MENU_ITEMS.length; i++) {
            float textY = startMenuY - (i * 65f);
            String text = MENU_ITEMS[i];

            if (i == selectedOption) {
                menuFont.getData().setScale(2.4f);
                menuFont.setColor(new Color(1f, 1f, 1f, 1f)); // Putih terang untuk yang dipilih
                text = "> " + text + " <";
            } else {
                menuFont.getData().setScale(2.0f);
                menuFont.setColor(new Color(0.55f, 0.60f, 0.75f, 1f));
            }

            layout.setText(menuFont, text);
            menuFont.draw(game.batch, layout, centerX - (layout.width / 2f), textY);
        }

        // Info & Kontrol
        infoFont.getData().setScale(1.4f);
        infoFont.setColor(new Color(0.6f, 0.85f, 0.6f, 1f));
        layout.setText(infoFont, "Gerak: W A S D");
        infoFont.draw(game.batch, layout, centerX - (infoWidth / 4f) - (layout.width / 2f), 140);

        infoFont.setColor(new Color(1f, 0.6f, 0.5f, 1f));
        layout.setText(infoFont, "Hindari Aslab & Dosen!");
        infoFont.draw(game.batch, layout, centerX + (infoWidth / 4f) - (layout.width / 2f), 140);

        infoFont.setColor(new Color(0.7f, 0.75f, 0.9f, 1f));
        String penaltyText = "Kena enemy = waktu berkurang " + GameConstants.TIME_PENALTY + " detik";
        layout.setText(infoFont, penaltyText);
        infoFont.draw(game.batch, layout, centerX - (layout.width / 2f), 100);

        infoFont.getData().setScale(1.6f);
        infoFont.setColor(new Color(1f, 1f, 0.5f, 1f));
        layout.setText(infoFont, "Tekan ENTER atau SPASI untuk mulai");
        infoFont.draw(game.batch, layout, centerX - (layout.width / 2f), menuBoxY - 40);

        // Navigasi
        infoFont.getData().setScale(1.2f);
        infoFont.setColor(new Color(0.45f, 0.45f, 0.55f, 1f));
        layout.setText(infoFont, "Atas / Bawah untuk navigasi menu");
        infoFont.draw(game.batch, layout, centerX - (layout.width / 2f), 25);

        game.batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            selectedOption = (selectedOption - 1 + MENU_ITEMS.length) % MENU_ITEMS.length;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            selectedOption = (selectedOption + 1) % MENU_ITEMS.length;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            switch (selectedOption) {
                case 0:
                    game.setScreen(new GameScreen(game));
                    break;
                case 1:
                    game.setScreen(new LeaderboardScreen(game));
                    break;
                case 2:
                    Gdx.app.exit();
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
        shape.dispose();
        titleFont.dispose();
        subtitleFont.dispose();
        menuFont.dispose();
        infoFont.dispose();
    }
}
