package com.nadia.caslab.observer;

// Interface untuk event listener game.
public interface GameObserver {
    void onCoinCollected(int totalCoins);
    void onEnemyHit(int timeRemaining);
    void onDoorUnlocked();
    void onGameWin(int coinsCollected, int timeRemaining);
    void onGameLose(String reason);
}
