package com.nadia.caslab.factory;

import com.nadia.caslab.entity.Enemy;
import com.nadia.caslab.game.GameConstants;
import com.nadia.caslab.strategy.*;

/**
 * FACTORY METHOD PATTERN - EnemyFactory.
 * Jenis enemy yang bisa dibuat:
 *   - createPatrolAslab()     : Aslab patroli horizontal
 *   - createRandomAslab()     : Aslab bergerak acak
 *   - createPatrolDosen()     : Dosen patroli vertikal
 */
public class EnemyFactory {
    public static Enemy createPatrolAslab(int tileX, int tileY, int patrolStart, int patrolEnd) {
        return new Enemy(
            tileX * GameConstants.TILE_SIZE,
            tileY * GameConstants.TILE_SIZE,
            Enemy.EnemyType.ASLAB,
            new PatrolMovementStrategy(PatrolMovementStrategy.PatrolAxis.HORIZONTAL, patrolStart, patrolEnd)
        );
    }

    /**
     * Factory Method: Buat Aslab dengan gerakan acak.
     */
    public static Enemy createRandomAslab(int tileX, int tileY) {
        return new Enemy(
            tileX * GameConstants.TILE_SIZE,
            tileY * GameConstants.TILE_SIZE,
            Enemy.EnemyType.ASLAB,
            new RandomMovementStrategy()
        );
    }

    public static Enemy createPatrolDosen(int tileX, int tileY, int patrolStart, int patrolEnd) {
        return new Enemy(
            tileX * GameConstants.TILE_SIZE,
            tileY * GameConstants.TILE_SIZE,
            Enemy.EnemyType.DOSEN,
            new PatrolMovementStrategy(PatrolMovementStrategy.PatrolAxis.VERTICAL, patrolStart, patrolEnd)
        );
    }
}
