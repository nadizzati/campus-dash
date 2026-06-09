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

public class TutorialScreen implements Screen {

    private final CampusDashGame game;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private ShapeRenderer shape;
    private BitmapFont titleFont;
    private BitmapFont sectionFont;
    private BitmapFont bodyFont;
    private GlyphLayout layout;
    private float animTimer = 0f;

    public TutorialScreen(CampusDashGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        camera   = new OrthographicCamera();
        viewport = new FitViewport(GameConstants.VIEWPORT_WIDTH, GameConstants.VIEWPORT_HEIGHT, camera);
        camera.setToOrtho(false, GameConstants.VIEWPORT_WIDTH, GameConstants.VIEWPORT_HEIGHT);
        shape    = new ShapeRenderer();
        layout   = new GlyphLayout();

        titleFont   = new BitmapFont(); titleFont.getData().setScale(5.0f);
        sectionFont = new BitmapFont(); sectionFont.getData().setScale(1.9f);
        bodyFont    = new BitmapFont(); bodyFont.getData().setScale(1.4f);
    }

    @Override
    public void render(float delta) {
        animTimer += delta;

        Gdx.gl.glClearColor(0.12f, 0.10f, 0.08f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();

        int W = GameConstants.VIEWPORT_WIDTH;
        int H = GameConstants.VIEWPORT_HEIGHT;
        float cx = W / 2f;

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shape.setProjectionMatrix(camera.combined);
        shape.begin(ShapeRenderer.ShapeType.Filled);

        // Background Base
        shape.setColor(new Color(0.12f, 0.10f, 0.08f, 1f));
        shape.rect(0, 0, W, H);

        // Floating Coins (Background Animation)
        for(int i = 0; i < 15; i++) {
            float px = (W / 15f) * i + (float)Math.sin(animTimer * 0.5f + i * 1.5f) * 80f;
            float py = (H / 15f) * i + (float)Math.cos(animTimer * 0.8f + i) * 80f;
            float bounce = (float) Math.sin(animTimer * 3f + i) * 5f;
            shape.setColor(new Color(1f, 0.84f, 0f, 0.25f));
            shape.circle(px, py + bounce, 12f);
            shape.setColor(new Color(1f, 0.95f, 0.5f, 0.25f));
            shape.circle(px - 2f, py + bounce + 2f, 6f);
        }

        // Main Panel Board
        float panelX = 180f;
        float panelW = W - 360f;
        float panelY = 200f;
        float panelH = H - 400f;

        shape.setColor(new Color(0.16f, 0.13f, 0.11f, 0.95f));
        shape.rect(panelX, panelY, panelW, panelH);

        float charStartX = panelX + 20f;
        float charAvailableW = panelW - 40f;
        float colW = charAvailableW / 4f;

        // Base posisi Y ubin simulasi disesuaikan agar sejajar
        float tileBaseY = 275f;
        float tileSize = GameConstants.TILE_SIZE;

        // Player
        float pX0 = (charStartX + (0 * colW) + (colW / 2f)) - (tileSize / 2f);

        // Body (Biru)
        shape.setColor(new Color(0.2f, 0.4f, 0.9f, 1.0f));
        shape.rect(pX0 + 8, tileBaseY + 4, tileSize - 16, tileSize - 8);
        // Kepala (Kulit)
        shape.setColor(new Color(1f, 0.8f, 0.6f, 1.0f));
        shape.circle(pX0 + tileSize / 2f, tileBaseY + tileSize - 10f, 10f);
        // Tas (Oranye)
        shape.setColor(new Color(1f, 0.5f, 0.1f, 1.0f));
        shape.rect(pX0 + tileSize - 16, tileBaseY + 16, 8, 16);


        // Enemy (Aslab)
        float pX1 = (charStartX + (1 * colW) + (colW / 2f)) - (tileSize / 2f);

        // Body (Merah)
        shape.setColor(new Color(0.9f, 0.2f, 0.2f, 1f));
        shape.rect(pX1 + 6, tileBaseY + 4, tileSize - 12, tileSize - 8);
        // Kepala (Kulit)
        shape.setColor(new Color(1f, 0.8f, 0.6f, 1f));
        shape.circle(pX1 + tileSize / 2f, tileBaseY + tileSize - 10f, 10f);
        // Badge Identitas (Putih)
        shape.setColor(Color.WHITE);
        shape.rect(pX1 + 10, tileBaseY + 20, 10, 6);


        // Enemy (Dosen)
        float pX2 = (charStartX + (2 * colW) + (colW / 2f)) - (tileSize / 2f);

        // Body (Ungu)
        shape.setColor(new Color(0.5f, 0.1f, 0.8f, 1f));
        shape.rect(pX2 + 6, tileBaseY + 4, tileSize - 12, tileSize - 8);
        // Kepala (Kulit)
        shape.setColor(new Color(1f, 0.8f, 0.6f, 1f));
        shape.circle(pX2 + tileSize / 2f, tileBaseY + tileSize - 10f, 10f);
        // Badge Identitas (Putih)
        shape.setColor(Color.WHITE);
        shape.rect(pX2 + 10, tileBaseY + 20, 10, 6);


        // Coin
        float pX3 = charStartX + (3 * colW) + (colW / 2f);
        shape.setColor(new Color(1f, 0.85f, 0.1f, 1f));
        shape.circle(pX3, tileBaseY + (tileSize / 2f), 14f);
        shape.setColor(new Color(1f, 0.95f, 0.5f, 1f));
        shape.circle(pX3 - 4, tileBaseY + (tileSize / 2f) + 4, 7f);

        shape.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // Rendering Teks (Batch)
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        // Teks How to Play
        titleFont.setColor(new Color(0.85f, 0.65f, 0.25f, 1f));
        layout.setText(titleFont, "HOW TO PLAY");
        titleFont.draw(game.batch, layout, cx - layout.width / 2f, H - 55);

        // Controls
        float leftColumnX = cx - 310f;
        float topRowY = panelY + panelH - 40f;

        sectionFont.setColor(new Color(0.9f, 0.8f, 0.6f, 1f));
        sectionFont.draw(game.batch, "CONTROLS", leftColumnX, topRowY);

        bodyFont.setColor(Color.WHITE);
        bodyFont.draw(game.batch, "W -> Move Up", leftColumnX, topRowY - 32);
        bodyFont.draw(game.batch, "S -> Move Down", leftColumnX, topRowY - 57);
        bodyFont.draw(game.batch, "A -> Move Left", leftColumnX, topRowY - 82);
        bodyFont.draw(game.batch, "D -> Move Right", leftColumnX, topRowY - 107);
        bodyFont.draw(game.batch, "ESC -> Pause Game", leftColumnX, topRowY - 132);

        // Rules
        float rightColumnX = cx + 40f;

        sectionFont.setColor(new Color(0.9f, 0.5f, 0.4f, 1f));
        sectionFont.draw(game.batch, "RULES", rightColumnX, topRowY);

        bodyFont.setColor(Color.WHITE);
        bodyFont.draw(game.batch, "- Hit enemy: -" + GameConstants.TIME_PENALTY + "s penalty", rightColumnX, topRowY - 32);
        bodyFont.draw(game.batch, "- Collect " + GameConstants.COINS_TO_WIN + " coins to unlock door", rightColumnX, topRowY - 57);
        bodyFont.draw(game.batch, "- Enter door to WIN!", rightColumnX, topRowY - 82);
        bodyFont.draw(game.batch, "- Time runs out = LOSE!", rightColumnX, topRowY - 107);

        sectionFont.setColor(new Color(0.9f, 0.8f, 0.6f, 1f));
        layout.setText(sectionFont, "CHARACTERS");
        sectionFont.draw(game.batch, layout, cx - layout.width / 2f, tileBaseY + tileSize + 35f);

        float textOffsetY = tileBaseY - 20f;

        // Teks Player
        bodyFont.setColor(new Color(0.5f, 0.7f, 1.0f, 1f));
        layout.setText(bodyFont, "YOU (Player)");
        bodyFont.draw(game.batch, layout, (pX0 + tileSize / 2f) - layout.width / 2f, textOffsetY);

        // Teks Aslab
        bodyFont.setColor(new Color(1f, 0.4f, 0.4f, 1f));
        layout.setText(bodyFont, "ASLAB");
        bodyFont.draw(game.batch, layout, (pX1 + tileSize / 2f) - layout.width / 2f, textOffsetY);

        // Teks Dosen
        bodyFont.setColor(new Color(0.8f, 0.5f, 1.0f, 1f));
        layout.setText(bodyFont, "DOSEN");
        bodyFont.draw(game.batch, layout, (pX2 + tileSize / 2f) - layout.width / 2f, textOffsetY);

        // Teks Coin
        bodyFont.setColor(new Color(1f, 0.9f, 0.3f, 1f));
        layout.setText(bodyFont, "COIN");
        bodyFont.draw(game.batch, layout, pX3 - layout.width / 2f, textOffsetY);

        bodyFont.setColor(new Color(0.85f, 0.65f, 0.25f, 1f));
        layout.setText(bodyFont, "Press ENTER or SPACE to Start Game");
        bodyFont.draw(game.batch, layout, cx - layout.width / 2f, 130f);

        game.batch.end();

        // Input Handler
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            game.setScreen(new GameScreen(game));
            Gdx.app.postRunnable(this::dispose);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
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
        sectionFont.dispose();
        bodyFont.dispose();
    }
}
