package xyz.zzzxb.toolkit.render;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import xyz.zzzxb.toolkit.entity.BoardData;
import xyz.zzzxb.toolkit.entity.TileData;
import xyz.zzzxb.toolkit.manager.BoardManager;
import xyz.zzzxb.toolkit.utils.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author zzzxb
 * 2026/9/4
 */
public class BoardRender {
    private final Logger log = Logger.of(this.getClass());
    private final SpriteBatch batch;
    private final TextureAtlas tileAtlas;
    private final BoardManager boardManager;
    private final TextureRegion boardRegion;
    private final Map<Integer, TextureRegion> regionCache;

    public BoardRender(SpriteBatch batch, BoardManager boardManager, TextureAtlas tileAtlas) {
        this.batch = batch;
        this.boardManager = boardManager;
        this.tileAtlas = tileAtlas;
        this.regionCache = new HashMap<>();
        this.boardRegion = tileAtlas.findRegion("0");
        int[] values = {0, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048};
        for (int value : values) {
            TextureRegion region = tileAtlas.findRegion(String.valueOf(value));
            if (region != null) {
                regionCache.put(value, region);
            }
        }
    }

    public void render() {
        renderBoard();
        renderTiles();
    }

    private void renderBoard() {
        BoardData boardData = boardManager.getBoardData();
        int rows = boardData.getRows();
        int cols = boardData.getCols();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                batch.draw(boardRegion, boardData.getWorldX(col), boardData.getWorldY(row));
            }
        }
    }

    private void renderTiles() {
        BoardData boardData = boardManager.getBoardData();
        for (int i = 0; i < boardData.getDataList().size; i++) {
            TileData tileData = boardData.getDataList().get(i);
            TextureRegion region = regionCache.get(tileData.getValue());
            if (region != null && tileData.getScale() > 0.01f) {
                float x = tileData.getX();
                float y = tileData.getY();
                float scale = tileData.getScale();
                float size = boardData.getCellSize() * scale;
                float offset = (boardData.getCellSize() - size) / 2;
                batch.draw(region, x + offset, y + offset, size, size);
            }
        }
    }
}
