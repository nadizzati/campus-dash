package com.nadia.caslab.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.nadia.caslab.game.GameConstants;
import com.nadia.caslab.observer.GameObserver;

// HUD (Heads-Up Display)
public class HUD implements GameObserver {

    private BitmapFont font;
    private GlyphLayout layout; // Untuk presisi penempatan teks rata tengah

    private int currentCoins    = 0;
    private int timeRemaining   = GameConstants.GAME_TIME_SEC;
    private boolean doorUnlocked = false;

    // Notifikasi sementara
    private String notification = "";
    private float notifTimer    = 0f;
    private static final float NOTIF_DURATION = 2.5f;

    public HUD() {
        font = new BitmapFont();
        font.getData().setScale(1.4f);
        layout = new GlyphLayout();
    }

    // GameObserver callbacks
    @Override
    public void onCoinCollected(int totalCoins) {
        this.currentCoins = totalCoins;
        showNotification("+1 Koin Nilai!");
    }

    @Override
    public void onEnemyHit(int timeRemaining) {
        this.timeRemaining = timeRemaining;
        showNotification("Ketahuan! -" + GameConstants.TIME_PENALTY + " detik!");
    }

    @Override
    public void onDoorUnlocked() {
        this.doorUnlocked = true;
        showNotification("Pintu Lab Terbuka! Segera masuk!");
    }

    @Override
    public void onGameWin(int coinsCollected, int timeRemaining) {
        showNotification("SELAMAT! Tugas dikumpulkan!");
    }

    @Override
    public void onGameLose(String reason) {
        showNotification("GAGAL: " + reason);
    }

    // Update & Render
    public void update(float delta, int timeRemaining) {
        this.timeRemaining = timeRemaining;
        if (notifTimer > 0) notifTimer -= delta;
    }

    public void render(SpriteBatch batch, ShapeRenderer shape) {
        float W = GameConstants.VIEWPORT_WIDTH;
        float H = GameConstants.VIEWPORT_HEIGHT;
        float barH = 65f;
        float barY = H - barH;
        float iconSize = 30f;
        float iconY = barY + (barH - iconSize) / 2f; // Posisi Y ikon di tengah vertikal bar

        // Render Shapes
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shape.begin(ShapeRenderer.ShapeType.Filled);

        // Background Top Bar
        shape.setColor(new Color(0.08f, 0.1f, 0.15f, 0.85f));
        shape.rect(0, barY, W, barH);

        // Garis aksen bawah bar
        shape.setColor(new Color(0.2f, 0.5f, 1.0f, 0.6f));
        shape.rect(0, barY, W, 3);

        // Kotak Ikon Koin (Kiri)
        float coinX = 30f;
        shape.setColor(new Color(1f, 0.85f, 0.2f, 1f)); // Emas
        shape.rect(coinX, iconY, iconSize, iconSize);

        // Kotak Ikon Waktu (Tengah-Kiri)
        float timeX = 220f;
        Color timeColor = timeRemaining < 60 ? new Color(1f, 0.3f, 0.3f, 1f) : new Color(0.2f, 0.8f, 1f, 1f);
        shape.setColor(timeColor);
        shape.rect(timeX, iconY, iconSize, iconSize);

        // Kotak Ikon Pintu (Kanan Atas)
        float doorAreaW = 240f;
        float doorX = W - doorAreaW - 30f;
        Color doorColor = doorUnlocked ? new Color(0.2f, 0.9f, 0.3f, 1f) : new Color(0.9f, 0.3f, 0.3f, 1f);

        // Background khusus panel pintu
        shape.setColor(new Color(0.15f, 0.18f, 0.25f, 0.8f));
        shape.rect(doorX, barY + 8, doorAreaW, barH - 16);

        // Ikon status pintu
        shape.setColor(doorColor);
        shape.rect(doorX + 15, iconY, iconSize, iconSize);

        shape.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // Render text
        batch.begin();
        float textMargin = 15f; // Jarak dari kotak ke teks

        // Kalkulasi posisi Y agar teks selalu presisi di tengah kotak
        float textBaseY = iconY + (iconSize / 2f);

        // Teks Koin
        font.setColor(Color.WHITE);
        String coinText = currentCoins + " / " + GameConstants.COINS_TO_WIN;
        layout.setText(font, coinText);
        font.draw(batch, layout, coinX + iconSize + textMargin, textBaseY + (layout.height / 2f));

        // Teks Waktu
        int mins = timeRemaining / 60;
        int secs = timeRemaining % 60;
        String timeText = String.format("%02d : %02d", mins, secs);
        font.setColor(timeColor);
        layout.setText(font, timeText);
        font.draw(batch, layout, timeX + iconSize + textMargin, textBaseY + (layout.height / 2f));

        // Teks Pintu
        font.setColor(doorColor);
        String doorText = doorUnlocked ? "PINTU TERBUKA" : "PINTU TERKUNCI";
        layout.setText(font, doorText);
        font.draw(batch, layout, doorX + 15 + iconSize + textMargin, textBaseY + (layout.height / 2f));

        // Teks Notifikasi Terapung (Floating Notification) di bawah Bar
        if (notifTimer > 0) {
            // Efek memudar saat hampir habis
            float alpha = Math.min(1f, notifTimer / 0.5f);
            font.setColor(new Color(1f, 0.9f, 0.4f, alpha));
            font.getData().setScale(1.6f);

            layout.setText(font, notification);
            // Muncul tepat di bawah panel tengah
            float notifX = (W / 2f) - (layout.width / 2f);
            float notifY = barY - 30f;

            font.draw(batch, layout, notifX, notifY);
            font.getData().setScale(1.4f); // Kembalikan ukuran
        }

        batch.end();
    }

    private void showNotification(String msg) {
        this.notification = msg;
        this.notifTimer   = NOTIF_DURATION;
    }

    public void dispose() {
        if (font != null) font.dispose();
    }
}
