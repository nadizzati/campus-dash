package com.nadia.caslab.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.nadia.caslab.game.GameConstants;
import com.nadia.caslab.strategy.MovementStrategy;

// Entity Enemy (Aslab/Dosen Patroli).

public class Enemy {

    public enum EnemyType { ASLAB, DOSEN }

    private float pixelX;
    private float pixelY;
    private EnemyType type;
    private MovementStrategy movementStrategy;

    // Collision cooldown: tidak langsung menyerang lagi
    private float hitCooldown = 0f;
    private static final float HIT_COOLDOWN_MAX = 3.0f;

    public Enemy(float startPixelX, float startPixelY, EnemyType type, MovementStrategy strategy) {
        this.pixelX            = startPixelX;
        this.pixelY            = startPixelY;
        this.type              = type;
        this.movementStrategy  = strategy;
    }

    public void update(float delta, Player player, int[][] mapData) {
        // Update movement via strategy
        movementStrategy.move(this, player, delta, mapData);

        float minX = GameConstants.TILE_SIZE;
        float maxX = (GameConstants.GRID_COLS - 2) * GameConstants.TILE_SIZE;
        float minY = GameConstants.TILE_SIZE;
        float maxY = (GameConstants.GRID_ROWS - 2) * GameConstants.TILE_SIZE;
        pixelX = Math.max(minX, Math.min(maxX, pixelX));
        pixelY = Math.max(minY, Math.min(maxY, pixelY));

        // Update cooldown
        if (hitCooldown > 0) hitCooldown -= delta;
    }

    // Render enemy sebagai persegi merah (Aslab) atau ungu (Dosen)
    public void render(ShapeRenderer shape) {
        Color bodyColor = (type == EnemyType.ASLAB)
                ? new Color(0.9f, 0.2f, 0.2f, 1f)   // Aslab: merah
                : new Color(0.5f, 0.1f, 0.8f, 1f);  // Dosen: ungu

        // Body
        shape.setColor(bodyColor);
        shape.rect(pixelX + 6, pixelY + 4, GameConstants.TILE_SIZE - 12, GameConstants.TILE_SIZE - 8);

        // Kepala
        shape.setColor(new Color(1f, 0.8f, 0.6f, 1f));
        shape.circle(pixelX + GameConstants.TILE_SIZE / 2f, pixelY + GameConstants.TILE_SIZE - 10f, 10f);

        // Badge/identitas
        shape.setColor(Color.WHITE);
        shape.rect(pixelX + 10, pixelY + 20, 10, 6);
    }

    // Cek apakah enemy menyentuh player (pixel-based collision)
    public boolean isCollidingWithPlayer(Player player) {
        if (hitCooldown > 0) return false;

        float ex = pixelX + GameConstants.TILE_SIZE / 2f;
        float ey = pixelY + GameConstants.TILE_SIZE / 2f;
        float px = player.getPixelX() + GameConstants.TILE_SIZE / 2f;
        float py = player.getPixelY() + GameConstants.TILE_SIZE / 2f;

        float dist = (float) Math.sqrt((ex - px) * (ex - px) + (ey - py) * (ey - py));
        return dist < GameConstants.TILE_SIZE * 0.5f;
    }

    // enemy sudah menyerang, mulai cooldown.
    public void triggerHitCooldown() {
        hitCooldown = HIT_COOLDOWN_MAX;
    }


    // Getters / Setters
    public float getPixelX()         { return pixelX; }
    public float getPixelY()         { return pixelY; }
    public void  setPixelX(float px) { this.pixelX = px; }
    public void  setPixelY(float py) { this.pixelY = py; }
    public int   getTileX()          { return (int)(pixelX / GameConstants.TILE_SIZE); }
    public int   getTileY()          { return (int)(pixelY / GameConstants.TILE_SIZE); }
    public EnemyType getType()       { return type; }
}
