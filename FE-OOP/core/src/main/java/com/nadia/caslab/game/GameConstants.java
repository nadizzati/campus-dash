package com.nadia.caslab.game;

// Konstanta global untuk Campus Dash.
public final class GameConstants {

    private GameConstants() {}

    // Grid & Map
    public static final int TILE_SIZE       = 48;   // pixel per tile
    public static final int GRID_COLS       = 24;   // jumlah kolom map
    public static final int GRID_ROWS       = 16;   // jumlah baris map
    public static final int MAP_WIDTH       = GRID_COLS * TILE_SIZE;  // 1152 px
    public static final int MAP_HEIGHT      = GRID_ROWS * TILE_SIZE;  // 768 px

    // Window
    public static final int VIEWPORT_WIDTH  = MAP_WIDTH;
    public static final int VIEWPORT_HEIGHT = MAP_HEIGHT;

    // Gameplay
    public static final int COINS_TO_WIN    = 20;   // koin yang dibutuhkan buka pintu
    public static final int GAME_TIME_SEC   = 90;   // 1.30 menit countdown
    public static final int TIME_PENALTY    = 10;   // detik dikurangi jika kena enemy
    public static final float PLAYER_SPEED  = 4.0f; // tile per detik
    public static final float ENEMY_SPEED   = 2.5f; // tile per detik

    // Backend API
    public static final String API_BASE_URL = "http://localhost:8080/api";

    //Tile Types
    public static final int TILE_FLOOR    = 0;
    public static final int TILE_WALL     = 1;
    public static final int TILE_DOOR     = 2;
    public static final int TILE_DESK     = 3;
    public static final int TILE_COMPUTER = 4;
}
