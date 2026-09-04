package xyz.zzzxb.toolkit.manager;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.LongArray;
import xyz.zzzxb.toolkit.entity.BoardData;
import xyz.zzzxb.toolkit.utils.Logger;
import xyz.zzzxb.toolkit.utils.LongPacker;

/**
 *
 * @author zzzxb
 * 2026/9/4
 */
public class BoardManager {
    private final Logger log = Logger.of(this.getClass());
    private final BoardData boardData;
    private final LongArray freeGrids;
    // 0 stop, 1 up -1 down -2 left 2 right
    public int swing;

    public BoardManager(BoardData boardData) {
        this.boardData = boardData;
        this.freeGrids = new LongArray();
    }

    public void init() {
        boardData.init();
        spawnTiles(2);
    }

    public void update(float delta) {
    }

    public void moveTile() {
        if (swing == 0) return;
        int swingX = Math.abs(swing) == 2 ? MathUtils.clamp(swing, -1, 1) : 0;
        int swingY = Math.abs(swing) == 1 ? swing : 0;
        int beginRow = swingY > 0 ? boardData.getRows() - 1 : 0;
        int beginCol = swingX > 0 ? boardData.getCols() - 1 : 0;
        for (int row = 0; row < boardData.getRows(); row++) {
            for (int col = 0; col < boardData.getCols(); col++) {
                int gridY = beginRow - swingY * row;
                int gridX = beginCol - swingX * col;
                long frontTilePacked = frontTile(gridX, gridY, swingX, swingY);
                if(LongPacker.packInts(gridY, gridX) == frontTilePacked) continue;
                // todo 判断是否能合并，否则就放置后边
            }
        }
    }

    private long frontTile(int gridX, int gridY, int swingX, int swingY) {
        int frontX = gridX + swingX;
        int frontY = gridY + swingY;
        // 如果撞墙了或者前边有方块就直接返回
        if ((swingX < 0 && frontX == 0) || (swingX > 0 && swingX == boardData.getCols() - 1) ||
            (swingY < 0 && frontY == 0) || (swingY > 0 && swingY == boardData.getRows() - 1) ||
            boardData.getTiles()[frontY][frontX] != null) {
            return LongPacker.packInts(frontY, frontX);
        }
        return frontTile(frontX, frontY, swingX, swingY);
    }

    /**
     * 批量生成方块（90% 概率生成 2，10% 概率生成 4）
     */
    private void spawnTiles(int batch) {
        int maxExp = MathUtils.random() < 0.9f ? 1 : 2;
        spawnTiles(maxExp, batch);
    }

    /**
     * 批量生成指定最大指数的方块
     */
    private void spawnTiles(int maxExp, int batch) {
        updateFreeGrids();
        if (freeGrids.size == 0) return;

        int actualBatch = Math.min(batch, freeGrids.size);
        for (int i = 0; i < actualBatch; i++) {
            int exp = MathUtils.random(1, Math.min(maxExp, 11));
            int value = (int) Math.pow(2, exp);

            int index = MathUtils.random(freeGrids.size - 1);
            long packed = freeGrids.removeIndex(index);
            int row = LongPacker.getHighInt(packed);
            int col = LongPacker.getLowInt(packed);
            boardData.placeTile(value, row, col);
            log.debug("生成Tile - value: %s, col: %s, row: %s, ", value, row, col);
        }
    }

    private void updateFreeGrids() {
        freeGrids.clear();
        for (int row = 0; row < boardData.getRows(); row++) {
            for (int col = 0; col < boardData.getCols(); col++) {
                if (boardData.getTiles()[row][col] == null) {
                    freeGrids.add(LongPacker.packInts(row, col));
                }
            }
        }
    }

    public BoardData getBoardData() {
        return boardData;
    }
}
