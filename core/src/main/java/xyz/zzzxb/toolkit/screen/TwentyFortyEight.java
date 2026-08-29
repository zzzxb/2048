package xyz.zzzxb.toolkit.screen;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import xyz.zzzxb.toolkit.core.SceneScreen;
import xyz.zzzxb.toolkit.core.camera.CameraAction;
import xyz.zzzxb.toolkit.utils.ResourceManager;

/**
 * @author zzzxb
 * 2026/8/23
 */
public class TwentyFortyEight extends SceneScreen {
    private Animation<TextureRegion> loutsAnimation;
    private TextureAtlas lotusAtlas;
    private float loutsAnimationDelta;

    @Override
    protected void onCreate() {
        if (ResourceManager.isLoaded("atlas/lotus.atlas")) {
            lotusAtlas = ResourceManager.getTextureAtlas("atlas/lotus.atlas");
        }
    }

    @Override
    protected void onShow() {
        cam.setZoom(0.1f);
        cam.act(CameraAction.zoomTo(1, 1f, Interpolation.sineOut));
    }

    @Override
    public void update(float delta) {
        loutsAnimationDelta += delta;
        if (lotusAtlas != null && loutsAnimation == null) {
            loutsAnimation = new Animation<>(0.2f, lotusAtlas.findRegions("lotus"),
                Animation.PlayMode.LOOP);
        }
    }

    @Override
    public void draw(float delta) {
        drawBackgroundAnimation();
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
