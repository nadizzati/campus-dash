package com.nadia.caslab.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nadia.caslab.manager.SoundManager;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.nadia.caslab.screen.SplashScreen;

//Class utama
public class CampusDashGame extends Game {

    // SpriteBatch tunggal untuk seluruh game
    public SpriteBatch batch;

    // ID mahasiswa yang sedang login (dari backend)
    public long playerStudentId = 1L;
    public String playerUsername = "Mahasiswa";
    public ShapeRenderer shapeRenderer;

    @Override
    public void create() {
        batch = new SpriteBatch();

        // Inisialisasi Singleton SoundManager
        SoundManager.getInstance().init();

        // Tampilkan main menu pertama kali
        setScreen(new SplashScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        SoundManager.getInstance().dispose();
    }
}
