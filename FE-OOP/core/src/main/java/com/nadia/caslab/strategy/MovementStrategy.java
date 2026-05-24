package com.nadia.caslab.strategy;

import com.nadia.caslab.entity.Enemy;
import com.nadia.caslab.entity.Player;

// Interface untuk perilaku gerakan enemy.
public interface MovementStrategy {
    /**@param enemy    enemy yang bergerak
     * @param player   posisi player (untuk strategi chase)
     * @param delta    delta time (detik)
     * @param mapData  data grid map untuk collision
     */
    void move(Enemy enemy, Player player, float delta, int[][] mapData);
}
