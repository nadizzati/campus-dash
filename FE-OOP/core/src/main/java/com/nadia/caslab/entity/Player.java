package com.nadia.caslab.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.nadia.caslab.game.GameConstants;
import com.nadia.caslab.state.DoorContext;

// Entity Player (Mahasiswa)
public class Player {

    public enum Direction { UP, DOWN, LEFT, RIGHT }

    // Grid position
    private int tileX;
    private int tileY;

    // Target tile untuk movement (tile yang akan dituju)
    private int targetTileX;
    private int targetTileY;

    // Pixel position (diinterpolasi saat bergerak)
    private float pixelX;
    private float pixelY;

    // State
    private Direction facingDirection = Direction.DOWN;
    private boolean isMoving = false;
    private float moveProgress = 0f; // 0.0 - 1.0 progress menuju target tile
    private boolean invincible = false;
    private float invincibleTimer = 0f;
    private static final float INVINCIBLE_DURATION = 2.0f; // detik setelah kena enemy

    public Player(int startTileX, int startTileY) {
        this.tileX       = startTileX;
        this.tileY       = startTileY;
        this.targetTileX = startTileX;
        this.targetTileY = startTileY;
        this.pixelX      = startTileX * GameConstants.TILE_SIZE;
        this.pixelY      = startTileY * GameConstants.TILE_SIZE;
    }

    // Update pergerakan antar tile

    public void update(float delta) {
        // Update invincibility
        if (invincible) {
            invincibleTimer -= delta;
            if (invincibleTimer <= 0) invincible = false;
        }

        if (isMoving) {
            moveProgress += GameConstants.PLAYER_SPEED * delta;

            if (moveProgress >= 1.0f) {
                // Sampai di target tile
                moveProgress = 1.0f;
                tileX   = targetTileX;
                tileY   = targetTileY;
                pixelX  = tileX * GameConstants.TILE_SIZE;
                pixelY  = tileY * GameConstants.TILE_SIZE;
                isMoving = false;
                moveProgress = 0f;
            } else {
                // Interpolasi linear menuju target
                float fromX = tileX * GameConstants.TILE_SIZE;
                float fromY = tileY * GameConstants.TILE_SIZE;
                float toX   = targetTileX * GameConstants.TILE_SIZE;
                float toY   = targetTileY * GameConstants.TILE_SIZE;
                pixelX      = fromX + (toX - fromX) * moveProgress;
                pixelY      = fromY + (toY - fromY) * moveProgress;
            }
        }

        float minPx = GameConstants.TILE_SIZE;
        float maxPx = (GameConstants.GRID_COLS - 2) * GameConstants.TILE_SIZE;
        float minPy = GameConstants.TILE_SIZE;
        float maxPy = (GameConstants.GRID_ROWS - 2) * GameConstants.TILE_SIZE;
        pixelX = Math.max(minPx, Math.min(maxPx, pixelX));
        pixelY = Math.max(minPy, Math.min(maxPy, pixelY));
    }

    // Render player sebagai persegi biru
    public void render(ShapeRenderer shape) {
        float alpha = invincible && ((int)(invincibleTimer * 4) % 2 == 0) ? 0.3f : 1.0f;

        // Body (biru)
        shape.setColor(new Color(0.2f, 0.4f, 0.9f, alpha));
        shape.rect(pixelX + 8, pixelY + 4, GameConstants.TILE_SIZE - 16, GameConstants.TILE_SIZE - 8);

        // Kepala (kulit)
        shape.setColor(new Color(1f, 0.8f, 0.6f, alpha));
        shape.circle(pixelX + GameConstants.TILE_SIZE / 2f, pixelY + GameConstants.TILE_SIZE - 10f, 10f);

        // Tas (oranye - identitas mahasiswa)
        shape.setColor(new Color(1f, 0.5f, 0.1f, alpha));
        shape.rect(pixelX + GameConstants.TILE_SIZE - 16, pixelY + 16, 8, 16);
    }

    // Request gerakan ke target tile
    public boolean requestMove(int newTargetX, int newTargetY, int[][] mapData, DoorContext doorContext) {
        if (isMoving) return false;

        // Boundary check ketat
        if (newTargetX < 1 || newTargetY < 1 ||
            newTargetX >= GameConstants.GRID_COLS - 1 ||
            newTargetY >= GameConstants.GRID_ROWS - 1) {
            return false;
        }

        // Cek tile
        int targetTile = mapData[newTargetY][newTargetX];
        if (targetTile == GameConstants.TILE_WALL ||
            targetTile == GameConstants.TILE_DESK ||
            targetTile == GameConstants.TILE_COMPUTER) {
            return false;
        }

        // Pintu hanya bisa dilewati jika sudah terbuka
        if (targetTile == GameConstants.TILE_DOOR && !doorContext.isOpen()) {
            return false;
        }

        targetTileX = newTargetX;
        targetTileY = newTargetY;
        isMoving = true;
        moveProgress = 0f;
        return true;
    }

    // Aktifkan invincibility setelah kena enemy
    public void hitByEnemy() {
        invincible      = true;
        invincibleTimer = INVINCIBLE_DURATION;
    }

    public boolean canBeHit()       { return !invincible && !isMoving; }

    // Target setters untuk Command Pattern
    public void setTargetX(int targetTileX) {
        if (!isMoving) { this.targetTileX = targetTileX; isMoving = true; }
    }
    public void setTargetY(int targetTileY) {
        if (!isMoving) { this.targetTileY = targetTileY; isMoving = true; }
    }

    // Getters / Setters
    public int   getTileX()              { return tileX; }
    public int   getTileY()              { return tileY; }
    public float getPixelX()             { return pixelX; }
    public float getPixelY()             { return pixelY; }
    public void  setPixelX(float px)     { this.pixelX = px; }
    public void  setPixelY(float py)     { this.pixelY = py; }
    public boolean isMoving()            { return isMoving; }
    public Direction getFacingDirection(){ return facingDirection; }
    public void setFacingDirection(Direction d) { this.facingDirection = d; }
}
