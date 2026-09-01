package xyz.zzzxb.toolkit.manager;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.LongArray;
import xyz.zzzxb.toolkit.entity.BoardData;
import xyz.zzzxb.toolkit.entity.TileData;
import xyz.zzzxb.toolkit.utils.LongPacker;

/**
 *
 * @author zzzxb
 * 2026/9/1
 */
public class BoardManager {
    private BoardData boardData;
    private LongArray freeGrid;


    public BoardManager(BoardData boardData) {
        this.boardData = boardData;
        this.freeGrid = new LongArray();
    }

    public void init() {
        for (int i = 0; i < 2; i++) {
            spawnTile();
        }
    }

    public void update(float delta) {
    }

    /**
     * 在随机空位生成一个方块
     * 标准 2048 规则：90% 概率生成 2，10% 概率生成 4
     */
    public void spawnTile() {
        updateFreeGrid();
        if (freeGrid.size == 0) return;

        int value = MathUtils.random() < 0.9f ? 2 : 4;
        placeTileAtRandom(value);
    }

    /**
     * 在随机空位生成一个指定等级的方块（用于测试或特殊模式）
     *
     * @param maxExp 最大指数，例如 maxExp=3 时可能生成 2,4,8
     */
    public void spawnTile(int maxExp) {
        updateFreeGrid();
        if (freeGrid.size == 0) return;

        int exp = MathUtils.random(1, Math.min(maxExp, 11));
        int value = (int) Math.pow(2, exp);
        placeTileAtRandom(value);
    }

    /**
     * 在随机空位放置一个指定值的方块
     */
    private void placeTileAtRandom(int value) {
        if (freeGrid.size == 0) return;

        int gridIndex = MathUtils.random(0, freeGrid.size - 1);
        long gridPacked = freeGrid.get(gridIndex);
        int row = LongPacker.getHighInt(gridPacked);
        int col = LongPacker.getLowInt(gridPacked);
        float x = boardData.x + (boardData.cols - 1 - col) * boardData.cellSize;
        float y = boardData.y + (boardData.rows - 1 - row) * boardData.cellSize;

        TileData tile = new TileData(value, x, y);
        boardData.tiles[row][col] = tile;

        freeGrid.removeIndex(gridIndex);
    }

    public void updateFreeGrid() {
        freeGrid.clear();
        for (int row = 0; row < boardData.rows; row++) {
            for (int col = 0; col < boardData.cols; col++) {
                if (boardData.tiles[row][col] == null) {
                    freeGrid.add(LongPacker.packInts(row, col));
                }
            }
        }
    }
}
