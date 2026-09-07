package xyz.zzzxb.toolkit.entity;

import com.badlogic.gdx.utils.Array;

/**
 *
 * @author zzzxb
 * 2026/9/4
 */
public class BoardData {
    private final Array<TileData> tilePool;
    private final Array<TileData> dataList;
    private final TileData[][] tiles;

    private final int rows;
    private final int cols;
    private final int width;
    private final int height;
    private final int cellSize;

    private float x;
    private float y;

    public BoardData(int rows, int cols, int cellSize) {
        this.rows = rows;
        this.cols = cols;
        this.cellSize = cellSize;
        this.width = cols * cellSize;
        this.height = rows * cellSize;
        this.tilePool = new Array<>();
        this.dataList = new Array<>();
        this.tiles = new TileData[rows][cols];
        initTilePool();
    }

    private void initTilePool() {
        int totalGrid = rows * cols;
        for (int i = 0; i < totalGrid; i++) {
            tilePool.add(new TileData(0, 0, 0));
        }
    }

    public void init() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                TileData tile = tiles[row][col];
                if (tile != null) {
                    dataList.removeValue(tile, true);
                    recycleTile(tile);
                    tiles[row][col] = null;
                }
            }
        }

        while (dataList.size > 0) {
            recycleTile(dataList.removeIndex(0));
        }
    }

    public void placeTile(int value, int row, int col) {
        TileData tile = tilePool.pop();
        tile.setValue(value);
        tile.setPosition(getWorldX(col), getWorldY(row));
        tile.setTargetPosition(tile.getX(), tile.getY());
        tiles[row][col] = tile;
        dataList.add(tile);
    }

    public void placeTile(TileData tile, int row, int col, boolean updatePos) {
        if (tile == null) return;
        tiles[row][col] = tile;
        if (updatePos) {
            tile.setPosition(getWorldX(col), getWorldY(row));
        }
        if (!dataList.contains(tile, true)) {
            dataList.add(tile);
        }
    }

    public TileData removeTile(int row, int col) {
        TileData tile = tiles[row][col];
        if (tile != null) {
            tiles[row][col] = null;
            dataList.removeValue(tile, true);
        }
        return tile;
    }

    public void recycleTile(TileData tile) {
        if (tile != null) {
            tile.reset();
            tilePool.add(tile);
        }
    }

    public float getWorldX(int col) {
        return x + col * cellSize;
    }

    public float getWorldY(int row) {
        return y + row * cellSize;
    }

    public int getGridX(float worldX) {
        return Math.round((worldX - x) / cellSize);
    }

    public int getGridY(float worldY) {
        return Math.round((worldY - y) / cellSize);
    }

    /* === Getter and Setter === */

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getCellSize() {
        return cellSize;
    }

    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }

    public TileData[][] getTiles() {
        return tiles;
    }

    public Array<TileData> getDataList() {
        return dataList;
    }

    public Array<TileData> getTilePool() {
        return tilePool;
    }

}
