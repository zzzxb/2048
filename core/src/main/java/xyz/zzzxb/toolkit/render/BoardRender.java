package xyz.zzzxb.toolkit.render;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import xyz.zzzxb.toolkit.entity.BoardData;
import xyz.zzzxb.toolkit.entity.TileData;
import xyz.zzzxb.toolkit.utils.Logger;

/**
 *
 * @author zzzxb
 * 2026/9/1
 */
public class BoardRender {
    private Logger log = Logger.of(BoardRender.class);
    private BoardData boardData;
    private TextureAtlas tileAtlas;

    public BoardRender(BoardData boardData, TextureAtlas tileAtlas) {
        this.boardData = boardData;
        this.tileAtlas = tileAtlas;
    }

    public void render(SpriteBatch batch) {
        tileRender(batch);
    }

    private void tileRender(SpriteBatch batch) {
        for (int row = 0; row < boardData.rows; row++) {
            for (int col = 0; col < boardData.cols; col++) {
                float x = boardData.x + (boardData.cols - 1 - col) * boardData.cellSize;
                float y = boardData.y + (boardData.rows - 1 - row) * boardData.cellSize;
                TileData tileData = boardData.tiles[row][col];
                if (tileData == null) {
                    batch.draw(tileAtlas.findRegion("0"), x, y, boardData.cellSize, boardData.cellSize);
                } else {
                    batch.draw(tileAtlas.findRegion(Integer.toString(tileData.value)),
                        tileData.x, tileData.y, boardData.cellSize, boardData.cellSize);
                }
            }
        }
    }
}
