package com.nadia.caslab.observer;

import java.util.ArrayList;
import java.util.List;

// Event Manager (Subject/Observable).
public class GameEventManager {
    private final List<GameObserver> observers = new ArrayList<>();

    // Daftarkan observer baru
    public void addObserver(GameObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    // Hapus observer
    public void removeObserver(GameObserver observer) {
        observers.remove(observer);
    }


    // Saat player mengambil koin
    public void notifyCoinCollected(int totalCoins) {
        for (GameObserver obs : observers) {
            obs.onCoinCollected(totalCoins);
        }
    }

    // Saat player terkena enemy
    public void notifyEnemyHit(int timeRemaining) {
        for (GameObserver obs : observers) {
            obs.onEnemyHit(timeRemaining);
        }
    }

    // Saat koin cukup, pintu lab terbuka
    public void notifyDoorUnlocked() {
        for (GameObserver obs : observers) {
            obs.onDoorUnlocked();
        }
    }

    // Saat player masuk pintu dan menang
    public void notifyGameWin(int coinsCollected, int timeRemaining) {
        for (GameObserver obs : observers) {
            obs.onGameWin(coinsCollected, timeRemaining);
        }
    }

    // Dipanggil saat player kalah (waktu habis / kondisi lain)
    public void notifyGameLose(String reason) {
        for (GameObserver obs : observers) {
            obs.onGameLose(reason);
        }
    }
}
