package xyz.zzzxb.toolkit.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import xyz.zzzxb.toolkit.core.InputManager;
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

    private BoardManager boardManager;
    private BoardRender boardRender;

    @Override
    protected void onCreate() {
        lotusAtlas = ResourceManager.getTextureAtlas("atlas/lotus.atlas");
        TextureAtlas tileAtlas = ResourceManager.getTextureAtlas("atlas/2048.atlas");
        BoardData boardData = new BoardData(4, 4, 64);
        boardData.setPosition(getCenteredX(boardData.width), getCenteredY(boardData.height));
        boardManager = new BoardManager(boardData);
        boardRender = new BoardRender(boardData, tileAtlas);
        inputManager.addKeyListener(Input.Keys.R, this::onShow);
        inputManager.addKeyListener(Input.Keys.Q, () -> Gdx.app.exit());
        inputManager.addKeyListener(Input.Keys.W, () -> boardManager.setSwing(1));
        inputManager.addKeyListener(Input.Keys.S, () -> boardManager.setSwing(-1));
        inputManager.addKeyListener(Input.Keys.A, () -> boardManager.setSwing(-2));
        inputManager.addKeyListener(Input.Keys.D, () -> boardManager.setSwing(2));
    }

    @Override
    protected void onShow() {
        cam.act(CameraAction.sequence(
            CameraAction.zoomTo(0.01f, 0.5f, Interpolation.sineOut),
            CameraAction.run(() -> boardManager.init()),
            CameraAction.zoomTo(1f, 0.8f, Interpolation.smoother)
        ));
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
        getBatch().setColor(1f, 1f, 1f, MathUtils.clamp(camera.zoom, 0.01f, 0.9f));
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
