package com.nadia.caslab.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.nadia.caslab.game.GameConstants;

// Entity Coin (Koin Nilai).
public class Coin {

    private int tileX;
    private int tileY;
    private boolean collected = false;
    private float animTimer   = 0f;   // untuk animasi bobbing

    public Coin(int tileX, int tileY) {
        this.tileX = tileX;
        this.tileY = tileY;
    }

    public void update(float delta) {
        if (!collected) {
            animTimer += delta * 3f; // kecepatan animasi
        }
    }

    // Render koin (lingkaran kuning) dengan ShapeRenderer
    public void render(ShapeRenderer shape) {
        if (collected) return;

        float px     = tileX * GameConstants.TILE_SIZE + GameConstants.TILE_SIZE / 2f;
        float py     = tileY * GameConstants.TILE_SIZE + GameConstants.TILE_SIZE / 2f;
        float bounce = (float) Math.sin(animTimer) * 3f; // efek bobbing ±3px

        // Outer ring (gold)
        shape.setColor(new Color(1f, 0.84f, 0f, 1f));
        shape.circle(px, py + bounce, 10f);

        // Inner shine (lighter gold)
        shape.setColor(new Color(1f, 0.95f, 0.5f, 1f));
        shape.circle(px - 2f, py + bounce + 2f, 5f);
    }

    public void collect() {
        this.collected = true;
    }

    public void respawn(int newTileX, int newTileY) {
        this.tileX     = newTileX;
        this.tileY     = newTileY;
        this.collected = false;
        this.animTimer = 0f;
    }

    // Collision check: apakah player berada di tile yang sama
    public boolean isCollidingWith(int playerTileX, int playerTileY) {
        return !collected && tileX == playerTileX && tileY == playerTileY;
    }

    // Getters / Setters
    public int getTileX()       { return tileX; }
    public int getTileY()       { return tileY; }
    public boolean isCollected(){ return collected; }

    @Override
    public String toString() {
        return "Coin[" + tileX + "," + tileY + "]" + (collected ? "(collected)" : "");
    }
}
