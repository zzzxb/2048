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

public class BoardManager {
    private final Logger log = Logger.of(this.getClass());
    private static final float SPAWN_2_PROBABILITY = 0.9f;
    private static final int[] POW2 = {1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048};

    private final BoardData boardData;
    private final LongArray freeGrids;
    public int swing;
    private float elapsedTime = 0f;
    private float duration = 0.18f;
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

    // ==================== 动画 ====================

    private void moveAnimation(float delta) {
        elapsedTime += delta;
        float progress = Math.min(1f, elapsedTime / duration);
        float eased = Interpolation.smooth.apply(progress);

        for (int i = 0; i < boardData.getDataList().size; i++) {
            TileData tile = boardData.getDataList().get(i);

            // 滑动动画
            float currentX = tile.getFromX() + (tile.getTargetX() - tile.getFromX()) * eased;
            float currentY = tile.getFromY() + (tile.getTargetY() - tile.getFromY()) * eased;
            tile.setPosition(currentX, currentY);

            // 合并动画（与滑动并行）
            if (tile.isMerging()) {
                tile.updateMergeAnimation(delta);
            }
        }

        if (progress >= 1f) {
            boolean allMergeDone = true;
            for (int i = 0; i < boardData.getDataList().size; i++) {
                TileData tile = boardData.getDataList().get(i);
                if (tile.isMerging()) {
                    allMergeDone = false;
                    break;
                }
            }

            if (allMergeDone) {
                finishMove();
            }
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

        int swingX = Math.abs(swing) == 2 ? MathUtils.clamp(swing, -1, 1) : 0;
        int swingY = Math.abs(swing) == 1 ? swing : 0;
        int beginRow = swingY > 0 ? boardData.getRows() - 1 : 0;
        int beginCol = swingX > 0 ? boardData.getCols() - 1 : 0;
        TileData[][] tiles = boardData.getTiles();

        for (int row = 0; row < boardData.getRows(); row++) {
            for (int col = 0; col < boardData.getCols(); col++) {
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
                    nowTile.enableMerge();
                    nowTile.setTargetPosition(boardData.getWorldX(frontCol), boardData.getWorldY(frontRow));
                    boardData.placeTile(nowTile, frontRow, frontCol, false);
                } else if (frontTile.isAllowMerge() && nowTile.getValue() == frontTile.getValue()) {
                    nowTile.disableMerge();
                    nowTile.setOption(TileData.UPGRADE);
                    nowTile.setTargetPosition(boardData.getWorldX(frontCol), boardData.getWorldY(frontRow));
                    boardData.placeTile(nowTile, frontRow, frontCol, false);

                    frontTile.setOption(TileData.REMOVE);
                    frontTile.startRemoveAnimation();

                    nowTile.startMergeAnimation();
                } else {
                    // 不能合并，放在前方方块身后
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

    private void spawnTiles(int batch) {
        updateFreeGrids();
        if (freeGrids.size == 0) return;

        int actualBatch = Math.min(batch, freeGrids.size);
        for (int i = 0; i < actualBatch; i++) {
            int maxExp = MathUtils.random() < SPAWN_2_PROBABILITY ? 1 : 2;
            spawnTile(maxExp);
        }
    }

    private void spawnTile(int maxExp) {
        int exp = MathUtils.clamp(maxExp, 1, 11);
        int value = POW2[exp];

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
                if (boardData.getTiles()[row][col] == null) {
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
