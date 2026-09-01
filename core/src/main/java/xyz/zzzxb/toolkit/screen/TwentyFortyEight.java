package xyz.zzzxb.toolkit.screen;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import xyz.zzzxb.toolkit.core.SceneScreen;
import xyz.zzzxb.toolkit.core.camera.CameraAction;
import xyz.zzzxb.toolkit.entity.BoardData;
import xyz.zzzxb.toolkit.manager.BoardManager;
import xyz.zzzxb.toolkit.render.BoardRender;
import xyz.zzzxb.toolkit.utils.ResourceManager;

/**
 * @author zzzxb
 * 2026/8/23
 */
public class TwentyFortyEight extends SceneScreen {
    private Animation<TextureRegion> loutsAnimation;
    private TextureAtlas lotusAtlas;
    private float loutsAnimationDelta;

    private TextureAtlas tileAtlas;
    private BoardData boardData;
    private BoardManager boardManager;
    private BoardRender boardRender;

    @Override
    protected void onCreate() {
        lotusAtlas = ResourceManager.getTextureAtlas("atlas/lotus.atlas");
        tileAtlas = ResourceManager.getTextureAtlas("atlas/2048.atlas");
        boardData = new BoardData(4, 4, 64);
        boardData.setPosition(getCenteredX(boardData.width), getCenteredY(boardData.height));
        boardManager = new BoardManager(boardData);
        boardRender = new BoardRender(boardData, tileAtlas);
    }

    @Override
    protected void onShow() {
        cam.setZoom(0.1f);
        cam.act(CameraAction.zoomTo(1, 1f, Interpolation.sineOut));
        boardManager.init();
    }

    @Override
    public void update(float delta) {
        loutsAnimationDelta += delta;
        if (lotusAtlas != null && loutsAnimation == null) {
            loutsAnimation = new Animation<>(0.2f, lotusAtlas.findRegions("lotus"),
                Animation.PlayMode.LOOP);
        }
        boardManager.update(delta);
    }

    @Override
    public void draw(float delta) {
        drawBackgroundAnimation();
        boardRender.render(getBatch());
    }

    @Override
    protected void onGameLoopStart(float delta) {
        getBatch().setColor(1f, 1f, 1f, MathUtils.clamp(camera.zoom, 0.01f, 1));
    }

    @Override
    protected void onGameLoopEnd(float delta) {
        getBatch().setColor(1, 1, 1, 1);
    }

    private void drawBackgroundAnimation() {
        if (loutsAnimation != null) {
            TextureRegion textureRegion = loutsAnimation.getKeyFrame(loutsAnimationDelta, true);
            getBatch().draw(textureRegion,
                getCenteredX(textureRegion.getRegionWidth()),
                getCenteredY(textureRegion.getRegionHeight()));
            if (loutsAnimation.isAnimationFinished(loutsAnimationDelta)) {
                loutsAnimationDelta = 0;
            }
        }
    }
}
