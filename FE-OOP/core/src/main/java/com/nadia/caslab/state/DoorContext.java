package com.nadia.caslab.state;

import com.badlogic.gdx.Gdx;

/**
 * STATE PATTERN, Implementasi state pintu lab.
 * LockedState  : Pintu terkunci, player tidak bisa lewat. Transisi ke OpenState saat koin cukup.
 * OpenState    : Pintu terbuka, player bisa masuk, WIN condition.
 * DoorContext  : Context yang menyimpan state aktif dan mendelegasikan perilaku ke state.
 */

// LockedState
class LockedState implements DoorState {
    @Override
    public void interact(DoorContext door) {
        Gdx.app.log("Door", "Pintu terkunci! Kumpulkan " + door.getRequiredCoins() + " koin dulu.");
        // Bisa tampilkan notifikasi di UI
    }

    @Override
    public boolean isPassable() {
        return false;
    }

    @Override
    public String getStateName() {
        return "LOCKED";
    }
}

// OpenState
class OpenState implements DoorState {
    @Override
    public void interact(DoorContext door) {
        Gdx.app.log("Door", "Pintu terbuka! Player masuk lab -> WIN!");
        door.onPlayerEnter();  // trigger WIN event
    }

    @Override
    public boolean isPassable() {
        return true;
    }

    @Override
    public String getStateName() {
        return "OPEN";
    }
}

// DoorContext - menyimpan dan mengelola state aktif
public class DoorContext {

    private DoorState currentState;
    private final int requiredCoins;
    private Runnable onWinCallback;

    public DoorContext(int requiredCoins) {
        this.requiredCoins = requiredCoins;
        this.currentState  = new LockedState(); // Mulai dalam keadaan terkunci
    }

    public void setOnWinCallback(Runnable callback) {
        this.onWinCallback = callback;
    }

    // saat player mengambil koin dan total koin berubah
    public void checkUnlock(int currentCoins) {
        if (currentState instanceof LockedState && currentCoins >= requiredCoins) {
            currentState = new OpenState();
            Gdx.app.log("Door", "Pintu lab terbuka! Koin: " + currentCoins + "/" + requiredCoins);
        }
    }

    // Player mencoba berinteraksi dengan pintu
    public void interact() {
        currentState.interact(this);
    }

    // Cek apakah bisa dilalui (untuk collision check)
    public boolean isPassable() {
        return currentState.isPassable();
    }

    // Dipanggil oleh OpenState saat player masuk
    public void onPlayerEnter() {
        if (onWinCallback != null) onWinCallback.run();
    }

    public DoorState getCurrentState()  { return currentState; }
    public int getRequiredCoins()       { return requiredCoins; }
    public boolean isLocked()           { return currentState instanceof LockedState; }
    public boolean isOpen()             { return currentState instanceof OpenState; }
}
