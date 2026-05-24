package com.nadia.caslab.strategy;

import com.badlogic.gdx.math.MathUtils;
import com.nadia.caslab.entity.Enemy;
import com.nadia.caslab.entity.Player;
import com.nadia.caslab.game.GameConstants;

// mplementasi gerakan enemy random

public class RandomMovementStrategy implements MovementStrategy {

    private int stuckCount = 0;
    private final float speedMultiplier;
    private float dirChangeTimer = 0f;
    private static final float DIR_CHANGE_INTERVAL = 2.0f;
    private float nextChangeInterval = 0f;

    // Arah saat ini: 0=UP, 1=DOWN, 2=LEFT, 3=RIGHT
    private int currentDir = 0;

    public RandomMovementStrategy() {
        this.speedMultiplier = 1.0f;
        this.currentDir = MathUtils.random(3);
        this.nextChangeInterval = MathUtils.random(0.8f, 2.5f);
    }

    public RandomMovementStrategy(float speedMultiplier) {
        this.speedMultiplier = speedMultiplier;
        this.currentDir = MathUtils.random(3);
        this.nextChangeInterval = MathUtils.random(0.5f, 1.8f);
    }

    @Override
    public void move(Enemy enemy, Player player, float delta, int[][] mapData) {
        dirChangeTimer += delta;
        if (dirChangeTimer >= nextChangeInterval) {
            dirChangeTimer = 0f;
            currentDir = MathUtils.random(3);
            stuckCount = 0;
            nextChangeInterval = MathUtils.random(0.8f, 2.5f);
        }

        float speed = GameConstants.ENEMY_SPEED * speedMultiplier * GameConstants.TILE_SIZE * delta;
        float nx = enemy.getPixelX();
        float ny = enemy.getPixelY();

        switch (currentDir) {
            case 0: ny += speed; break;
            case 1: ny -= speed; break;
            case 2: nx -= speed; break;
            case 3: nx += speed; break;
        }

        if (isBoundingBoxWalkable(nx, ny, mapData)) {
            enemy.setPixelX(nx);
            enemy.setPixelY(ny);
            stuckCount = 0;
        } else {
            stuckCount++;
            if (stuckCount >= 3) {
                enemy.setPixelX(Math.round(enemy.getPixelX() / GameConstants.TILE_SIZE) * GameConstants.TILE_SIZE);
                enemy.setPixelY(Math.round(enemy.getPixelY() / GameConstants.TILE_SIZE) * GameConstants.TILE_SIZE);
                stuckCount = 0;
                currentDir = MathUtils.random(3);
            } else {
                currentDir = MathUtils.random(3);
            }
        }
    }

    private boolean isBoundingBoxWalkable(float px, float py, int[][] mapData) {
        float ts = GameConstants.TILE_SIZE;
        int tx = (int)(px / ts);
        int ty = (int)(py / ts);
        return isWalkable(tx, ty, mapData) && isWalkable((int)((px + ts - 1) / ts), ty, mapData)
            && isWalkable(tx, (int)((py + ts - 1) / ts), mapData)
            && isWalkable((int)((px + ts - 1) / ts), (int)((py + ts - 1) / ts), mapData);
    }

    private boolean isWalkable(int tx, int ty, int[][] mapData) {
        if (tx < 1 || ty < 1 || tx >= GameConstants.GRID_COLS - 1 || ty >= GameConstants.GRID_ROWS - 1) {
            return false;
        }
        int tile = mapData[ty][tx];
        return tile == GameConstants.TILE_FLOOR || tile == GameConstants.TILE_DOOR;
    }
}
