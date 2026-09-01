package xyz.zzzxb.toolkit.entity;

/**
 *
 * @author zzzxb
 * 2026/9/1
 */
public class BoardData {
    public int rows;
    public int cols;
    public int cellSize;
    public int width;
    public int height;
    public float x;
    public float y;
    public TileData[][] tiles;

    public BoardData(int rows, int cols, int cellSize) {
        this.rows = rows;
        this.cols = cols;
        this.cellSize = cellSize;
        this.width = cols * cellSize;
        this.height = rows * cellSize;
        this.tiles = new TileData[rows][cols];
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
