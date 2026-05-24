package com.nadia.caslab.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.nadia.caslab.game.GameConstants;
import com.nadia.caslab.state.DoorContext;

/**
 * TileMap, Generator dan renderer grid map kampus
 * Membuat layout ruangan lab dengan:
 * - Dinding tepi (WALL)
 * - Lantai (FLOOR)
 * - Meja dan komputer sebagai obstacle
 * - Pintu lab (DOOR), dikontrol DoorContext (State Pattern)
 */
public class TileMap {

    private final int[][] mapData;    // [row][col] = tile type
    private final DoorContext door;

    // Posisi pintu di grid
    private int doorTileX;
    private int doorTileY;

    public TileMap(DoorContext door) {
        this.door    = door;
        this.mapData = new int[GameConstants.GRID_ROWS][GameConstants.GRID_COLS];
        generateMap();
    }

    // Generate layout map kampus.
    private void generateMap() {
        int rows = GameConstants.GRID_ROWS; // 16
        int cols = GameConstants.GRID_COLS; // 24

        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                mapData[r][c] = GameConstants.TILE_FLOOR;

        // Border
        for (int r = 0; r < rows; r++) {
            mapData[r][0]        = GameConstants.TILE_WALL;
            mapData[r][cols - 1] = GameConstants.TILE_WALL;
        }
        for (int c = 0; c < cols; c++) {
            mapData[0][c]        = GameConstants.TILE_WALL;
            mapData[rows - 1][c] = GameConstants.TILE_WALL;
        }

        // partition — 6 buah
        for (int c = 6; c <= 9; c++) mapData[3][c] = GameConstants.TILE_WALL;
        mapData[3][7] = GameConstants.TILE_FLOOR;
        for (int c = 14; c <= 18; c++) mapData[5][c] = GameConstants.TILE_WALL;
        mapData[5][16] = GameConstants.TILE_FLOOR;
        for (int r = 7; r <= 10; r++) mapData[r][10] = GameConstants.TILE_WALL;
        mapData[9][10] = GameConstants.TILE_FLOOR;
        for (int c = 3; c <= 8; c++) mapData[11][c] = GameConstants.TILE_WALL;
        mapData[11][5] = GameConstants.TILE_FLOOR;
        for (int r = 13; r <= 14; r++) mapData[r][12] = GameConstants.TILE_WALL;
        for (int r = 7; r <= 9; r++) mapData[r][6] = GameConstants.TILE_WALL;
        mapData[8][6] = GameConstants.TILE_FLOOR;

        // desk — 9 buah
        placeDeskCluster(2, 4, 2, 2);
        placeDeskCluster(2, 17, 2, 3);
        placeDeskCluster(2, 11, 2, 2);
        placeDeskCluster(6, 2, 3, 2);
        placeDeskCluster(6, 19, 2, 2);
        placeDeskCluster(7, 14, 2, 2);
        placeDeskCluster(12, 2, 2, 3);
        placeDeskCluster(12, 7, 2, 2);
        placeDeskCluster(12, 16, 2, 3);
        placeDeskCluster(3, 20, 2, 2);

        // computer — 4 buah
        mapData[2][19]  = GameConstants.TILE_COMPUTER;
        mapData[4][7]   = GameConstants.TILE_COMPUTER;
        mapData[8][17]  = GameConstants.TILE_COMPUTER;
        mapData[11][14] = GameConstants.TILE_COMPUTER;

        // door (posisi tetap: col=22, row=8)
        doorTileX = cols - 2; // 22
        doorTileY = rows / 2; // 8
        mapData[doorTileY][doorTileX] = GameConstants.TILE_DOOR;
    }

    // cluster meja L×W mulai dari (startCol, startRow)
    private void placeDeskCluster(int startCol, int startRow, int width, int height) {
        for (int r = startRow; r < startRow + height; r++) {
            for (int c = startCol; c < startCol + width; c++) {
                if (r > 0 && r < GameConstants.GRID_ROWS - 1 &&
                    c > 0 && c < GameConstants.GRID_COLS - 1) {
                    mapData[r][c] = GameConstants.TILE_DESK;
                }
            }
        }
    }

    // Render seluruh tilemap dengan ShapeRenderer
    public void render(ShapeRenderer shape) {
        int ts = GameConstants.TILE_SIZE;
        boolean doorOpen = door.isOpen();

        for (int r = 0; r < GameConstants.GRID_ROWS; r++) {
            for (int c = 0; c < GameConstants.GRID_COLS; c++) {
                float px = c * ts;
                float py = r * ts;

                switch (mapData[r][c]) {
                    case GameConstants.TILE_WALL:
                        shape.setColor(new Color(0.3f, 0.25f, 0.2f, 1f));
                        shape.rect(px, py, ts, ts);
                        // Bata pattern
                        shape.setColor(new Color(0.25f, 0.2f, 0.15f, 1f));
                        shape.line(px, py + ts/2f, px + ts, py + ts/2f);
                        break;

                    case GameConstants.TILE_FLOOR:
                        // Lantai: abu-abu terang
                        shape.setColor(new Color(0.85f, 0.85f, 0.82f, 1f));
                        shape.rect(px, py, ts, ts);
                        // Grid line
                        shape.setColor(new Color(0.75f, 0.75f, 0.72f, 1f));
                        shape.rect(px, py, ts, 1);
                        shape.rect(px, py, 1, ts);
                        break;

                    case GameConstants.TILE_DESK:
                        shape.setColor(new Color(0.6f, 0.4f, 0.2f, 1f));
                        shape.rect(px + 3, py + 3, ts - 6, ts - 6);
                        shape.setColor(new Color(0.5f, 0.35f, 0.15f, 1f));
                        shape.rect(px + 5, py + 5, ts - 10, ts - 10);
                        break;

                    case GameConstants.TILE_COMPUTER:
                        shape.setColor(new Color(0.6f, 0.4f, 0.2f, 1f));
                        shape.rect(px + 3, py + 3, ts - 6, ts - 12);
                        // Monitor
                        shape.setColor(new Color(0.1f, 0.1f, 0.15f, 1f));
                        shape.rect(px + 8, py + 20, ts - 16, ts - 24);
                        shape.setColor(new Color(0.2f, 0.7f, 0.3f, 1f));
                        shape.rect(px + 10, py + 22, ts - 20, ts - 28);
                        break;

                    case GameConstants.TILE_DOOR:
                        if (doorOpen) {
                            // Pintu terbuka: hijau
                            shape.setColor(new Color(0.2f, 0.8f, 0.2f, 1f));
                        } else {
                            // Pintu terkunci: coklat gelap
                            shape.setColor(new Color(0.5f, 0.3f, 0.1f, 1f));
                        }
                        shape.rect(px + 4, py + 2, ts - 8, ts - 4);
                        // Handle pintu
                        shape.setColor(Color.GOLD);
                        shape.circle(px + ts - 10, py + ts / 2f, 3f);
                        break;
                }
            }
        }
    }

    // Cek apakah tile di posisi tertentu adalah dinding/obstacle
    public boolean isWall(int tileX, int tileY) {
        if (tileX < 0 || tileY < 0 || tileX >= GameConstants.GRID_COLS || tileY >= GameConstants.GRID_ROWS)
            return true;
        int tile = mapData[tileY][tileX];
        return tile == GameConstants.TILE_WALL  ||
               tile == GameConstants.TILE_DESK   ||
               tile == GameConstants.TILE_COMPUTER;
    }

    public int[][] getMapData()  { return mapData; }
    public int getDoorTileX()    { return doorTileX; }
    public int getDoorTileY()    { return doorTileY; }
}
