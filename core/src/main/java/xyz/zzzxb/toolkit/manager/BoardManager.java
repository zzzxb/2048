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
    private boolean emptyTiles;


    public BoardManager(BoardData boardData) {
        this.boardData = boardData;
        this.freeGrid = new LongArray();
    }

    public void init() {
        clearTiles();
        spawnTile(2);
    }

    public void setSwing(int swing) {
        boardData.swing = swing;
    }


    public void update(float delta) {
        move();
    }

    public void move() {
        if (boardData.swing == 0) return;
        int swingX = Math.abs(boardData.swing) == 1 ? boardData.swing : 0;
        int swingY = Math.abs(boardData.swing) == 2 ? MathUtils.clamp(boardData.swing, -1, 1) : 0;
        // 根据倾倒方向计算从哪开始遍历， 向上的话 会从 0,3 开始 向下会从0,0, 向左会从 0,0 向右会从 3,0
        int beginX = swingX > 0 ? boardData.cols - 1 : 0;
        int beginY = swingY > 0 ? boardData.rows - 1 : 0;
        for (int i = 0; i < boardData.cols; i++) {
            for (int j = 0; j < boardData.rows; j++) {
                // 行数列数作为最大限制数, 根据开始点来决定是从小到大还是从大到小生成索引进行遍历
                int x = beginX != 0 ? beginX - i : beginX + i;
                int y = beginY != 0 ? beginY - j : beginY + j;
                TileData nowTileData = boardData.tiles[y][x];
                // 根据倾倒方向计算上一个tile的坐标，比如往上倾倒 swingX =  1 我们对 x 坐标 +1
                int frontX = MathUtils.clamp(x, 0, boardData.cols - 1);
                int frontY = MathUtils.clamp(y, 0, boardData.rows - 1);
                TileData frontTileData = boardData.tiles[frontY][frontX];
                if (nowTileData == frontTileData) {
                    continue;
                } else if (nowTileData.value == frontTileData.value && frontTileData.allowMerge) {
                    // 前方的方块允许合并并、值也相等我们就让当前方块替代前一个方块
                    nowTileData.allowMerge = false;
                    boardData.tiles[frontY][frontX] = nowTileData;
                } else {
                    // todo 如果不匹配我们就把当前方块放在前一个方块后边
                }
            }
        }
    }

    /**
     * 在随机空位生成一个方块
     * 标准 2048 规则：90% 概率生成 2，10% 概率生成 4
     */
    public void spawnTile(int batch) {
        int maxExp = MathUtils.random() < 0.9f ? 1 : 2;
        batchSpawnTile(maxExp, batch);
    }

    public void batchSpawnTile(int maxExp, int batch) {
        updateFreeGrid();
        if (freeGrid.size == 0) return;

        for (int i = 0; i < batch; i++) {
            int exp = MathUtils.random(1, Math.min(maxExp, 11));
            int value = (int) Math.pow(2, exp);
            placeTileAtRandom(value);
        }
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

    private void clearTiles() {
        for (int row = 0; row < boardData.rows; row++) {
            for (int col = 0; col < boardData.cols; col++) {
                boardData.tiles[row][col] = null;
            }
        }
    }

    private void updateFreeGrid() {
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
