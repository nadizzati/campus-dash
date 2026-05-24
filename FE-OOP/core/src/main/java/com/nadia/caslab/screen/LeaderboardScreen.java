package com.nadia.caslab.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.nadia.caslab.game.CampusDashGame;
import com.nadia.caslab.game.GameConstants;
import com.nadia.caslab.network.ApiClient;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardScreen implements Screen {

    private final CampusDashGame game;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private BitmapFont titleFont;
    private BitmapFont rowFont;

    // Data leaderboard (diisi secara async dari API)
    private final List<String[]> leaderboardRows = new ArrayList<>();
    private boolean loading = true;
    private String loadError = null;
    private float animTimer = 0f;

    public LeaderboardScreen(CampusDashGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(GameConstants.VIEWPORT_WIDTH, GameConstants.VIEWPORT_HEIGHT, camera);
        camera.setToOrtho(false, GameConstants.VIEWPORT_WIDTH, GameConstants.VIEWPORT_HEIGHT);

        titleFont = new BitmapFont();
        titleFont.getData().setScale(2.5f);

        rowFont = new BitmapFont();
        rowFont.getData().setScale(1.3f);

        // Fetch leaderboard dari backend Spring Boot
        ApiClient.getLeaderboard(new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(String body) {
                parseLeaderboard(body);
                loading = false;
            }

            @Override
            public void onFailure(String errorMessage) {
                loadError = "Gagal memuat data: " + errorMessage;
                loading = false;

                // Fallback data dummy jika offline
                leaderboardRows.clear();
                leaderboardRows.add(new String[]{"1", "mahasiswa_alpha", "120", "5"});
                leaderboardRows.add(new String[]{"2", "mahasiswa_beta", "80", "3"});
                leaderboardRows.add(new String[]{"3", "mahasiswa_gamma", "40", "2"});
            }
        });
    }

    private void parseLeaderboard(String json) {
        try {
            leaderboardRows.clear();
            JsonValue root = new JsonReader().parse(json);

            JsonValue array = root.isArray() ? root : root.get("data");
            if (array == null) array = root;

            int rankCounter = 1;
            for (JsonValue entry : array) {
                String rank = entry.getString("rank", String.valueOf(rankCounter));
                String username = entry.getString("username", "unknown");
                String koin = entry.getString("totalKoin", "0");
                String sesi = entry.getString("totalSesiCompleted", "0");

                leaderboardRows.add(new String[]{rank, username, koin, sesi});
                rankCounter++;
            }
        } catch (Exception e) {
            Gdx.app.error("LEADERBOARD", "Gagal parsing JSON", e);
            loadError = "Format data berbeda";
        }
    }

    @Override
    public void render(float delta) {
        animTimer += delta;

        // Background Biru Gelap Cyber
        Gdx.gl.glClearColor(0.04f, 0.05f, 0.08f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();

        int W = GameConstants.VIEWPORT_WIDTH;
        int H = GameConstants.VIEWPORT_HEIGHT;

        // Render Shape
        game.shapeRenderer.setProjectionMatrix(camera.combined);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Panel Utama Tengah (Semi-transparent Glassmorphism)
        game.shapeRenderer.setColor(new Color(0.08f, 0.1f, 0.18f, 0.6f));
        game.shapeRenderer.rect(40, 80, W - 80, H - 200);

        // Header Tabel
        game.shapeRenderer.setColor(new Color(0.12f, 0.16f, 0.28f, 1f));
        game.shapeRenderer.rect(40, H - 120, W - 80, 50);

        // Garis Pembatas Emas
        game.shapeRenderer.setColor(Color.GOLD);
        game.shapeRenderer.rect(40, H - 122, W - 80, 2);

        // Render baris background tabel
        if (!loading) {
            for (int i = 0; i < leaderboardRows.size() && i < 10; i++) {
                float rowY = H - 185 - (i * 52f);

                Color rowColor = (i % 2 == 0)
                    ? new Color(0.11f, 0.13f, 0.22f, 1f)
                    : new Color(0.07f, 0.09f, 0.15f, 1f);

                // Warna Premium khusus podium Top 3
                if (i == 0)      rowColor = new Color(0.28f, 0.22f, 0.05f, 0.9f);
                else if (i == 1) rowColor = new Color(0.18f, 0.20f, 0.25f, 0.9f);
                else if (i == 2) rowColor = new Color(0.22f, 0.14f, 0.07f, 0.9f);

                game.shapeRenderer.setColor(rowColor);
                game.shapeRenderer.rect(50, rowY, W - 100, 44);
            }
        }

        // Animasi Loading Bulatan Berputar
        if (loading) {
            float angle = animTimer * 240f;
            for (int i = 0; i < 8; i++) {
                double rad = Math.toRadians(angle + i * 45);
                float bx = W / 2f + (float) Math.cos(rad) * 35;
                float by = H / 2f - 20 + (float) Math.sin(rad) * 35;
                float alpha = (i / 8f);
                game.shapeRenderer.setColor(new Color(0f, 0.75f, 1f, alpha));
                game.shapeRenderer.circle(bx, by, 6);
            }
        }

        game.shapeRenderer.end();


        // Render Teks
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        // Judul Utama Screen
        titleFont.setColor(Color.GOLD);
        titleFont.draw(game.batch, "RANKING MAHASISWA", 60, H - 35);

        // Sub-deskripsi teks
        rowFont.setColor(Color.LIGHT_GRAY);
        rowFont.getData().setScale(1.0f);
        titleFont.getData().setScale(2.5f); // reset scale judul
        rowFont.draw(game.batch, "Papan peringkat akumulasi perolehan skor laboratorium", 60, H - 85);

        // Label Kolom Tabel
        rowFont.getData().setScale(1.1f);
        rowFont.setColor(new Color(0.4f, 0.7f, 1f, 1f));
        rowFont.draw(game.batch, "PERINGKAT", 70,  H - 95);
        rowFont.draw(game.batch, "NAMA PENGGUNA", 220, H - 95);
        rowFont.draw(game.batch, "TOTAL KOIN", 520, H - 95);
        rowFont.draw(game.batch, "TOTAL SESI", 760, H - 95);

        if (loading) {
            rowFont.getData().setScale(1.4f);
            rowFont.setColor(Color.CYAN);
            rowFont.draw(game.batch, "Sinkronisasi Cloud...", W / 2f - 95, H / 2f + 35);
        } else {
            // Render Isi Baris Data Leaderboard
            for (int i = 0; i < leaderboardRows.size() && i < 10; i++) {
                String[] row = leaderboardRows.get(i);
                float rowTextY = H - 156 - (i * 52f);

                Color rankColor;
                String rankLabel;

                // Desain teks ala arcade pengganti emoji agar terhindar dari bug kotak "?"
                if (i == 0) {
                    rankColor = Color.GOLD;
                    rankLabel = "[ 1ST ]";
                } else if (i == 1) {
                    rankColor = Color.WHITE;
                    rankLabel = "[ 2ND ]";
                } else if (i == 2) {
                    rankColor = new Color(0.9f, 0.55f, 0.3f, 1f);
                    rankLabel = "[ 3RD ]";
                } else {
                    rankColor = Color.LIGHT_GRAY;
                    rankLabel = "  " + row[0];
                }

                rowFont.getData().setScale(1.2f);

                // Cetak data ke kolom masing-masing menggunakan game.batch yang valid
                rowFont.setColor(rankColor);
                rowFont.draw(game.batch, rankLabel, 70, rowTextY);

                rowFont.setColor(Color.WHITE);
                rowFont.draw(game.batch, row[1], 220, rowTextY);

                rowFont.setColor(Color.GOLD);
                rowFont.draw(game.batch, row[2] + " Pts", 520, rowTextY);

                rowFont.setColor(new Color(0.4f, 0.9f, 0.4f, 1f));
                rowFont.draw(game.batch, row[3] + " Sesi", 760, rowTextY);
            }

            // Pesan Peringatan Mode Offline
            if (loadError != null) {
                rowFont.getData().setScale(1.0f);
                rowFont.setColor(new Color(1f, 0.4f, 0.4f, 0.8f));
                rowFont.draw(game.batch, "Koneksi Gagal. Menampilkan data lokal offline.", 60, 115);
            }
        }

        // Teks Petunjuk Navigasi Keluar (Efek berkedip)
        float blinkAlpha = (float) (0.5 + 0.5 * Math.sin(animTimer * 3.0f));
        rowFont.getData().setScale(1.1f);
        rowFont.setColor(new Color(0.7f, 0.7f, 0.4f, blinkAlpha));
        rowFont.draw(game.batch, "◀ Tekan ESCAPE atau BACKSPACE untuk kembali ke Menu Utama", 60, 45);

        game.batch.end();

        // Logika Tombol Kembali
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) ||
            Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
            game.setScreen(new MainMenuScreen(game));
            dispose();
        }
    }

    @Override public void resize(int w, int h) { viewport.update(w, h); }
    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void hide()   {}

    @Override
    public void dispose() {
        titleFont.dispose();
        rowFont.dispose();
    }
}
