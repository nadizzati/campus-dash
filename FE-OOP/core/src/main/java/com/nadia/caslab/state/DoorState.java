package com.nadia.caslab.state;

// Interface untuk state pintu lab.

public interface DoorState {
    // Player mencoba masuk ke pintu
    void interact(DoorContext door);

    // Cek apakah pintu bisa dilalui player
    boolean isPassable();

    // Nama state untuk debugging/UI
    String getStateName();
}
