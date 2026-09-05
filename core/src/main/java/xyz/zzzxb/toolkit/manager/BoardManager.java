package xyz.zzzxb.toolkit.manager;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.LongArray;
import xyz.zzzxb.toolkit.core.GameStateManager;
import xyz.zzzxb.toolkit.entity.BoardData;
import xyz.zzzxb.toolkit.entity.TileData;
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
    private float elapsedTime = 0f;
    private float duration = 0.5f;
    public boolean isAnimation;

    public BoardManager(BoardData boardData) {
        this.boardData = boardData;
        this.freeGrids = new LongArray();
    }

    public void init() {
        swing = 0;
        isAnimation = false;
        elapsedTime = 0f;
        boardData.init();
        spawnTiles(2);
    }

    public void update(float delta) {
        if (!isAnimation && swing != 0) {
            moveTile();
            elapsedTime = 0f;
            isAnimation = true;
        } else if (isAnimation) {
            moveAnimation(delta);
        }
    }

    private void moveAnimation(float delta) {
        elapsedTime += delta;
        float progress = Math.min(1f, elapsedTime / duration);
        float eased = Interpolation.smoother.apply(progress);
        for (int i = 0; i < boardData.getDataList().size; i++) {
            TileData tile = boardData.getDataList().get(i);
            float currentX = tile.getFromX() + (tile.getTargetX() - tile.getFromX()) * eased;
            float currentY = tile.getFromY() + (tile.getTargetY() - tile.getFromY()) * eased;
            tile.setPosition(currentX, currentY);
        }

        if (progress >= 1f) {
            finishMove();
        }
    }

    private void finishMove() {
        Array<TileData> dataList = boardData.getDataList();
        for (int i = dataList.size - 1; i >= 0; i--) {
            TileData tile = dataList.get(i);
            if (tile == null) continue;

            tile.setPosition(tile.getTargetX(), tile.getTargetY());
            tile.setTargetPosition(tile.getTargetX(), tile.getTargetY());

            if (tile.getOption() == TileData.UPGRADE) {
                tile.enableMerge();
                tile.setOption(TileData.NONE);
                tile.setValue(tile.getValue() * 2);
            } else if (tile.getOption() == TileData.REMOVE) {
                boardData.recycleTile(dataList.removeIndex(i));
            }
        }
        swing = 0;
        elapsedTime = 0;
        isAnimation = false;
        spawnTiles(1);
    }

    private void moveTile() {
        if (swing == 0) return;
        boardData.getDataList().clear();
        int swingX = Math.abs(swing) == 2 ? MathUtils.clamp(swing, -1, 1) : 0;
        int swingY = Math.abs(swing) == 1 ? swing : 0;
        int beginRow = swingY > 0 ? boardData.getRows() - 1 : 0;
        int beginCol = swingX > 0 ? boardData.getCols() - 1 : 0;
        for (int row = 0; row < boardData.getRows(); row++) {
            for (int col = 0; col < boardData.getCols(); col++) {
                TileData[][] tiles = boardData.getTiles();
                int nowRow = swingY == 0 ? beginRow + row : beginRow - swingY * row;
                int nowCol = swingX == 0 ? beginCol + col : beginCol - swingX * col;
                TileData nowTile = tiles[nowRow][nowCol];
                if (nowTile == null) continue;

                long frontTilePacked = frontTile(nowRow, nowCol, swingX, swingY);
                int frontCol = LongPacker.getLowInt(frontTilePacked);
                int frontRow = LongPacker.getHighInt(frontTilePacked);
                TileData frontTile = tiles[frontRow][frontCol];
                tiles[nowRow][nowCol] = null;
                if (nowTile == frontTile || frontTile == null) {
                    // 最前边的方格为空，直接把当前 tile 移动过去, 允许后边方格合并
                    nowTile.enableMerge();
                    nowTile.setTargetPosition(boardData.getWorldX(frontCol), boardData.getWorldY(frontRow));
                    boardData.placeTile(nowTile, frontRow, frontCol, false);
                } else if (frontTile.isAllowMerge() && nowTile.getValue() == frontTile.getValue()) {
                    // 当前方格设置为升级、关闭合并，并挪到前边方格, 提前挪动目的是为了后续方格容易进行计算
                    nowTile.disableMerge();
                    nowTile.setOption(TileData.UPGRADE);
                    nowTile.setTargetPosition(boardData.getWorldX(frontCol), boardData.getWorldY(frontRow));
                    boardData.placeTile(nowTile, frontRow, frontCol, false);
                    // 前边方格被占后，设置为移除代表有人把它合并
                    frontTile.setOption(TileData.REMOVE);
                } else {
                    // 前边方格既不空也不能合并，就放置在前边方格身后并允许后边方格合并
                    int backRow = MathUtils.clamp(frontRow - swingY, 0, boardData.getRows() - 1);
                    int backCol = MathUtils.clamp(frontCol - swingX, 0, boardData.getCols() - 1);
                    nowTile.enableMerge();
                    nowTile.setOption(TileData.NONE);
                    nowTile.setTargetPosition(boardData.getWorldX(backCol), boardData.getWorldY(backRow));
                    boardData.placeTile(nowTile, backRow, backCol, false);
                }
            }
        }
    }

    private long frontTile(int row, int col, int swingX, int swingY) {
        int frontCol = col + swingX;
        int frontRow = row + swingY;
        if (frontCol < 0 || frontCol >= boardData.getCols() ||
            frontRow < 0 || frontRow >= boardData.getRows()) {
            return LongPacker.packInts(row, col);
        }
        if (boardData.getTiles()[frontRow][frontCol] != null) {
            return LongPacker.packInts(frontRow, frontCol);
        }
        return frontTile(frontRow, frontCol, swingX, swingY);
    }

    /**
     * 批量生成方块（90% 概率生成 2，10% 概率生成 4）
     */
    private void spawnTiles(int batch) {
        updateFreeGrids();
        if (freeGrids.size == 0) return;

        int actualBatch = Math.min(batch, freeGrids.size);
        for (int i = 0; i < actualBatch; i++) {
            int maxExp = MathUtils.random() < 0.8f ? 1 : 2;
            spawnTile(maxExp);
        }
    }

    /**
     * 批量生成指定最大指数的方块
     */
    private void spawnTile(int maxExp) {
        int exp = MathUtils.clamp(maxExp, 1, 11);
        int value = (int) Math.pow(2, exp);

        int index = MathUtils.random(freeGrids.size - 1);
        long packed = freeGrids.removeIndex(index);
        int row = LongPacker.getHighInt(packed);
        int col = LongPacker.getLowInt(packed);
        boardData.placeTile(value, row, col);
    }

    private void updateFreeGrids() {
        freeGrids.clear();
        for (int row = 0; row < boardData.getRows(); row++) {
            for (int col = 0; col < boardData.getCols(); col++) {
                TileData tileData = boardData.getTiles()[row][col];
                if (tileData == null) {
                    freeGrids.add(LongPacker.packInts(row, col));
                }
            }
        }
    }

    public BoardData getBoardData() {
        return boardData;
    }

    public void setSwing(int swing) {
        if (GameStateManager.isPlaying() && !isAnimation && this.swing == 0) {
            if (swing >= -2 && swing <= 2 && swing != 0) {
                this.swing = swing;
            }
        }
    }
}
