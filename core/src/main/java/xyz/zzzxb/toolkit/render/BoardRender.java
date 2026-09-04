package xyz.zzzxb.toolkit.render;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import xyz.zzzxb.toolkit.entity.BoardData;
import xyz.zzzxb.toolkit.entity.TileData;
import xyz.zzzxb.toolkit.manager.BoardManager;
import xyz.zzzxb.toolkit.utils.Logger;

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

    public BoardRender(SpriteBatch batch, BoardManager boardManager, TextureAtlas tileAtlas) {
        this.batch = batch;
        this.boardManager = boardManager;
        this.tileAtlas = tileAtlas;
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
                batch.draw(tileAtlas.findRegion("0"),
                    boardData.getWorldX(col), boardData.getWorldY(row));
            }
        }
    }

    private void renderTiles() {
        BoardData boardData = boardManager.getBoardData();
        for (int i = 0; i < boardData.getDataList().size; i++) {
            TileData tileData = boardData.getDataList().get(i);
            batch.draw(tileAtlas.findRegion(Integer.toString(tileData.getValue())),
                tileData.getX(), tileData.getY());
        }
    }
}
