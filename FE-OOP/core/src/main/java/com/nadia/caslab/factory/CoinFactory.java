package com.nadia.caslab.factory;

import com.badlogic.gdx.math.MathUtils;
import com.nadia.caslab.entity.Coin;
import com.nadia.caslab.entity.Enemy;
import com.nadia.caslab.entity.Player;
import com.nadia.caslab.game.GameConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CoinFactory {

    private final int[][] mapData;
    private final List<int[]> occupiedPositions = new ArrayList<>();

    public CoinFactory(int[][] mapData) {
        this.mapData = mapData;
    }

    // Spawn awal, buat 10 coin baru
    public List<Coin> spawnBatch(int count) {
        List<Coin> coins = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            int[] pos = findValidPositionZoned(null, null);
            if (pos != null) {
                occupiedPositions.add(pos);
                coins.add(new Coin(pos[0], pos[1]));
            }
        }
        return coins;
    }

    // Object pooling, reuse coin yang sudah diambil player
    public void respawnCoin(Coin coin, Player player, List<Enemy> enemies) {
        // Bebaskan posisi lama
        freePosition(coin.getTileX(), coin.getTileY());

        int[] pos = findValidPositionZoned(player, enemies);
        if (pos != null) {
            occupiedPositions.add(pos);
            coin.respawn(pos[0], pos[1]);
        }
    }

    public void freePosition(int tileX, int tileY) {
        occupiedPositions.removeIf(pos -> pos[0] == tileX && pos[1] == tileY);
    }

    public void clearOccupied() {
        occupiedPositions.clear();
    }

    // Cari posisi valid dengan zone-based distribution agar coin tersebar merata.
    private int[] findValidPositionZoned(Player player, List<Enemy> enemies) {
        int zoneW = (GameConstants.GRID_COLS - 4) / 4;  // -4 untuk margin border
        int zoneH = (GameConstants.GRID_ROWS - 4) / 4;

        // Buat daftar semua zona lalu shuffle agar tidak bias
        List<int[]> zones = new ArrayList<>();
        for (int zx = 0; zx < 4; zx++)
            for (int zy = 0; zy < 4; zy++)
                zones.add(new int[]{zx, zy});
        Collections.shuffle(zones);

        for (int[] zone : zones) {
            int startC = 2 + zone[0] * zoneW;
            int startR = 2 + zone[1] * zoneH;

            // Coba beberapa posisi acak dalam zona ini
            for (int attempt = 0; attempt < 15; attempt++) {
                int tx = startC + MathUtils.random(zoneW - 1);
                int ty = startR + MathUtils.random(zoneH - 1);

                // Pastikan masih dalam batas map
                if (tx >= GameConstants.GRID_COLS - 2 || ty >= GameConstants.GRID_ROWS - 2) continue;

                if (isValidSpawnPosition(tx, ty, player, enemies)) {
                    return new int[]{tx, ty};
                }
            }
        }

        // Fallback, linear scan seluruh map
        for (int r = 2; r < GameConstants.GRID_ROWS - 2; r++)
            for (int c = 2; c < GameConstants.GRID_COLS - 2; c++)
                if (isValidSpawnPosition(c, r, player, enemies))
                    return new int[]{c, r};

        return null;
    }

    private boolean isValidSpawnPosition(int tx, int ty, Player player, List<Enemy> enemies) {
        // Harus tile FLOOR
        if (mapData[ty][tx] != GameConstants.TILE_FLOOR) return false;

        // Tidak boleh sudah ada coin lain
        for (int[] pos : occupiedPositions)
            if (pos[0] == tx && pos[1] == ty) return false;

        // Tidak boleh di tile player (jika diberikan)
        if (player != null && player.getTileX() == tx && player.getTileY() == ty)
            return false;

        // Tidak boleh di tile enemy (jika diberikan)
        if (enemies != null) {
            for (Enemy e : enemies)
                if (e.getTileX() == tx && e.getTileY() == ty) return false;
        }

        return true;
    }
}
