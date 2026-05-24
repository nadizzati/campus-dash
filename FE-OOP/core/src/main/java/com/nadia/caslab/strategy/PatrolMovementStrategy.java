package com.nadia.caslab.strategy;

import com.nadia.caslab.entity.Enemy;
import com.nadia.caslab.entity.Player;
import com.nadia.caslab.game.GameConstants;

// Implementasi enemy patroli jalur tetap.
public class PatrolMovementStrategy implements MovementStrategy {

    public enum PatrolAxis { HORIZONTAL, VERTICAL }

    private final PatrolAxis axis;
    private final float patrolStart;  // koordinat pixel awal
    private final float patrolEnd;    // koordinat pixel akhir
    private boolean movingForward = true;

    /**@param axis         HORIZONTAL atau VERTICAL
     * @param startTile    tile awal patroli
     * @param endTile      tile akhir patroli
     */
    public PatrolMovementStrategy(PatrolAxis axis, int startTile, int endTile) {
        this.axis        = axis;
        this.patrolStart = startTile * GameConstants.TILE_SIZE;
        this.patrolEnd   = endTile   * GameConstants.TILE_SIZE;
    }

    @Override
    public void move(Enemy enemy, Player player, float delta, int[][] mapData) {
        float speed = GameConstants.ENEMY_SPEED * GameConstants.TILE_SIZE * delta;

        if (axis == PatrolAxis.HORIZONTAL) {
            float nx = enemy.getPixelX() + (movingForward ? speed : -speed);

            if (nx >= patrolEnd) {
                nx = patrolEnd;
                movingForward = false;
            } else if (nx <= patrolStart) {
                nx = patrolStart;
                movingForward = true;
            }
            enemy.setPixelX(nx);

        } else { // vertikal
            float ny = enemy.getPixelY() + (movingForward ? speed : -speed);

            if (ny >= patrolEnd) {
                ny = patrolEnd;
                movingForward = false;
            } else if (ny <= patrolStart) {
                ny = patrolStart;
                movingForward = true;
            }
            enemy.setPixelY(ny);
        }
    }
}
