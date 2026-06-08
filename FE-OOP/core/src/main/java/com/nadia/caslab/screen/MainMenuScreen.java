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
    private BitmapFont promptFont;

    private GlyphLayout layout;
    private float animTimer = 0f;
    private int selectedOption = 0;
    private static final String[] MENU_ITEMS = {"Start Game", "Leaderboard", "Exit"};

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

        titleFont    = new BitmapFont(); titleFont.getData().setScale(5.0f);
        subtitleFont = new BitmapFont(); subtitleFont.getData().setScale(1.5f);
        menuFont     = new BitmapFont(); menuFont.getData().setScale(2.0f);
        promptFont   = new BitmapFont(); promptFont.getData().setScale(1.6f);
    }

    @Override
    public void render(float delta) {
        animTimer += delta;
        handleInput();

        int W = GameConstants.VIEWPORT_WIDTH;
        int H = GameConstants.VIEWPORT_HEIGHT;
        float cx = W / 2f;

        Gdx.gl.glClearColor(0.12f, 0.10f, 0.08f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shape.setProjectionMatrix(camera.combined);
        shape.begin(ShapeRenderer.ShapeType.Filled);

        // Background Base
        shape.setColor(new Color(0.12f, 0.10f, 0.08f, 1f));
        shape.rect(0, 0, W, H);

        // Floating Coins (Background)
        for(int i = 0; i < 15; i++) {
            float px = (W / 15f) * i + (float)Math.sin(animTimer * 0.5f + i * 1.5f) * 80f;
            float py = (H / 15f) * i + (float)Math.cos(animTimer * 0.8f + i) * 80f;
            float bounce = (float) Math.sin(animTimer * 3f + i) * 5f;
            shape.setColor(new Color(1f, 0.84f, 0f, 0.25f));
            shape.circle(px, py + bounce, 12f);
            shape.setColor(new Color(1f, 0.95f, 0.5f, 0.25f));
            shape.circle(px - 2f, py + bounce + 2f, 6f);
        }

        float menuBoxWidth = 480f;
        float menuBoxHeight = 260f;
        float menuBoxX = cx - menuBoxWidth / 2f;
        float menuBoxY = (H / 2f) - (menuBoxHeight / 2f);

        float rowHeight = 75f;
        float btnHeight = 55f;
        float totalMenuHeight = MENU_ITEMS.length * rowHeight;

        float panelCenterY = menuBoxY + (menuBoxHeight / 2f);
        float startRowY = panelCenterY + (totalMenuHeight / 2f) - rowHeight;

        // Draw Panel Menu
        shape.setColor(new Color(0.16f, 0.13f, 0.11f, 0.98f));
        shape.rect(menuBoxX, menuBoxY, menuBoxWidth, menuBoxHeight);

        // Border emas di sisi kiri
        shape.setColor(new Color(0.85f, 0.65f, 0.25f, 1f));
        shape.rect(menuBoxX, menuBoxY, 5, menuBoxHeight);

        // Draw Highlight Box (Emas Solid)
        float selectedRowCenterY = startRowY - (selectedOption * rowHeight) + (rowHeight / 2f);
        float highlightRectY = selectedRowCenterY - (btnHeight / 2f);

        shape.setColor(new Color(0.85f, 0.65f, 0.25f, 1f));
        shape.rect(menuBoxX + 25, highlightRectY, menuBoxWidth - 50, btnHeight);

        shape.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // RENDERING TEKS (BATCH)
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        // Title
        titleFont.setColor(new Color(0.85f, 0.65f, 0.25f, 1f));
        layout.setText(titleFont, "CAMPUS DASH");
        titleFont.draw(game.batch, layout, cx - layout.width / 2f, H - 120);

        // Subtitle
        subtitleFont.setColor(new Color(0.9f, 0.8f, 0.6f, 1f));
        layout.setText(subtitleFont, "Deadline Pursuit");
        subtitleFont.draw(game.batch, layout, cx - layout.width / 2f, H - 185);

        // Menu Items
        for (int i = 0; i < MENU_ITEMS.length; i++) {
            menuFont.setColor(i == selectedOption ? Color.BLACK : Color.WHITE);
            layout.setText(menuFont, MENU_ITEMS[i]);

            float rowCenterY = startRowY - (i * rowHeight) + (rowHeight / 2f);
            float textY = rowCenterY + (layout.height / 2f);

            menuFont.draw(game.batch, layout, cx - layout.width / 2f, textY);
        }

        // Prompt
        promptFont.setColor(new Color(0.85f, 0.65f, 0.25f, 1f));
        layout.setText(promptFont, "Press ENTER or SPACE to continue");
        promptFont.draw(game.batch, layout, cx - layout.width / 2f, menuBoxY - 60);

        game.batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W))
            selectedOption = (selectedOption - 1 + MENU_ITEMS.length) % MENU_ITEMS.length;
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S))
            selectedOption = (selectedOption + 1) % MENU_ITEMS.length;

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (selectedOption == 0) {
                game.setScreen(new TutorialScreen(game));
            } else if (selectedOption == 1) {
                game.setScreen(new LeaderboardScreen(game));
            } else {
                Gdx.app.exit();
            }
            Gdx.app.postRunnable(this::dispose);
        }
    }

    @Override public void resize(int w, int h) { viewport.update(w, h); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        shape.dispose();
        titleFont.dispose();
        menuFont.dispose();
        promptFont.dispose();
        subtitleFont.dispose();
    }
}
