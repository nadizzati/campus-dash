package com.nadia.caslab.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class SoundManager {

    // Singleton instance
    private static SoundManager instance;

    // Audio resources
    private Music bgMusic;
    private Sound coinPickupSound;
    private Sound enemyHitSound;
    private Sound doorOpenSound;
    private Sound winSound;
    private Sound loseSound;

    private boolean soundEnabled = true;
    private boolean musicEnabled = true;
    private float masterVolume  = 0.8f;

    private SoundManager() {}

    public static SoundManager getInstance() {
        if (instance == null) {
            synchronized (SoundManager.class) {
                if (instance == null) {
                    instance = new SoundManager();
                }
            }
        }
        return instance;
    }

    public void init() {
        try {
            bgMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/bgm_campus.mp3"));
            bgMusic.setLooping(true);
            bgMusic.setVolume(0.4f * masterVolume);

            coinPickupSound = Gdx.audio.newSound(Gdx.files.internal("audio/coin_pickup.wav"));
            enemyHitSound   = Gdx.audio.newSound(Gdx.files.internal("audio/enemy_hit.wav"));
            doorOpenSound   = Gdx.audio.newSound(Gdx.files.internal("audio/door_open.wav"));
            winSound        = Gdx.audio.newSound(Gdx.files.internal("audio/win.wav"));
            loseSound       = Gdx.audio.newSound(Gdx.files.internal("audio/lose.wav"));
        } catch (Exception e) {
            // jika file audio tidak tersedia
            Gdx.app.log("SoundManager", "Audio files tidak ditemukan, mode silent.");
        }
    }

    public void playBgMusic() {
        if (musicEnabled && bgMusic != null && !bgMusic.isPlaying()) {
            bgMusic.play();
        }
    }

    public void stopBgMusic() {
        if (bgMusic != null) bgMusic.stop();
    }

    public void playCoinPickup() {
        if (soundEnabled && coinPickupSound != null) {
            coinPickupSound.play(masterVolume);
        }
    }

    public void playEnemyHit() {
        if (soundEnabled && enemyHitSound != null) {
            enemyHitSound.play(masterVolume);
        }
    }

    public void playDoorOpen() {
        if (soundEnabled && doorOpenSound != null) {
            doorOpenSound.play(masterVolume);
        }
    }

    public void playWin() {
        if (soundEnabled && winSound != null) {
            winSound.play(masterVolume);
        }
    }

    public void playLose() {
        if (soundEnabled && loseSound != null) {
            loseSound.play(masterVolume);
        }
    }

    public void toggleSound()  { soundEnabled = !soundEnabled; }
    public void toggleMusic()  { musicEnabled = !musicEnabled; if (!musicEnabled) stopBgMusic(); else playBgMusic(); }
    public void setVolume(float v) { masterVolume = Math.max(0, Math.min(1, v)); }

    public void dispose() {
        if (bgMusic       != null) bgMusic.dispose();
        if (coinPickupSound != null) coinPickupSound.dispose();
        if (enemyHitSound  != null) enemyHitSound.dispose();
        if (doorOpenSound  != null) doorOpenSound.dispose();
        if (winSound       != null) winSound.dispose();
        if (loseSound      != null) loseSound.dispose();
    }
}
