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

public class SplashScreen implements Screen {

    private final CampusDashGame game;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private ShapeRenderer shape;
    private BitmapFont titleFont;
    private BitmapFont subtitleFont;
    private BitmapFont promptFont;
    private GlyphLayout layout;
    private float animTimer = 0f;

    public SplashScreen(CampusDashGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        camera   = new OrthographicCamera();
        viewport = new FitViewport(GameConstants.VIEWPORT_WIDTH, GameConstants.VIEWPORT_HEIGHT, camera);
        camera.setToOrtho(false, GameConstants.VIEWPORT_WIDTH, GameConstants.VIEWPORT_HEIGHT);
        shape    = new ShapeRenderer();
        layout   = new GlyphLayout();

        titleFont    = new BitmapFont(); titleFont.getData().setScale(6f);
        subtitleFont = new BitmapFont(); subtitleFont.getData().setScale(2f);
        promptFont   = new BitmapFont(); promptFont.getData().setScale(1.6f);
    }

    @Override
    public void render(float delta) {
        animTimer += delta;

        Gdx.gl.glClearColor(0.12f, 0.10f, 0.08f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();

        int W = GameConstants.VIEWPORT_WIDTH;
        int H = GameConstants.VIEWPORT_HEIGHT;

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

            // Outer ring (gold, slightly transparent)
            shape.setColor(new Color(1f, 0.84f, 0f, 0.25f));
            shape.circle(px, py + bounce, 12f);

            // Inner shine (lighter gold, slightly transparent)
            shape.setColor(new Color(1f, 0.95f, 0.5f, 0.25f));
            shape.circle(px - 2f, py + bounce + 2f, 6f);
        }

        shape.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        // Title
        titleFont.setColor(new Color(0.85f, 0.65f, 0.25f, 1f));
        layout.setText(titleFont, "CAMPUS DASH");
        titleFont.draw(game.batch, layout, W / 2f - layout.width / 2f, H / 2f + 120);

        // Subtitle (Posisi diturunkan dari 50 ke 25 agar tidak terlalu nempel)
        subtitleFont.setColor(new Color(0.9f, 0.8f, 0.6f, 1f));
        layout.setText(subtitleFont, "Deadline Pursuit");
        subtitleFont.draw(game.batch, layout, W / 2f - layout.width / 2f, H / 2f + 25);

        // Mission
        subtitleFont.getData().setScale(1.4f);
        subtitleFont.setColor(new Color(0.8f, 0.75f, 0.65f, 1f));
        String mission = "Collect " + GameConstants.COINS_TO_WIN + " coins before 23:59!";
        layout.setText(subtitleFont, mission);
        subtitleFont.draw(game.batch, layout, W / 2f - layout.width / 2f, H / 2f - 15);

        // Prompt
        promptFont.setColor(new Color(0.85f, 0.65f, 0.25f, 1f));
        layout.setText(promptFont, "Press ENTER or SPACE to continue");
        promptFont.draw(game.batch, layout, W / 2f - layout.width / 2f, H / 2f - 100);

        game.batch.end();

        // Input
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) ||
            Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ||
            Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            game.setScreen(new LoginScreen(game));
            dispose();
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
        subtitleFont.dispose();
        promptFont.dispose();
    }
}
