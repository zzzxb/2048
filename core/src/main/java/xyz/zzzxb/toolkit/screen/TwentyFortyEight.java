package xyz.zzzxb.toolkit.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import xyz.zzzxb.toolkit.core.SceneScreen;
import xyz.zzzxb.toolkit.core.camera.CameraAction;
import xyz.zzzxb.toolkit.utils.ResourceManager;

/**
 * @author zzzxb
 * 2026/8/23
 */
public class TwentyFortyEight extends SceneScreen {
    private float animationDelta;
    private Animation<TextureRegion> loutsAnimation;
    private float scale = 1.4f;

    @Override
    protected void onCreate() {
        inputManager.addKeyListener(Input.Keys.Q, () -> Gdx.app.exit());
        inputManager.addKeyListener(Input.Keys.R, this::onShow);
        inputManager.addKeyListener(Input.Keys.MINUS, () -> scale = Math.clamp(scale -= 0.1f, 1, 2));
        inputManager.addKeyListener(Input.Keys.EQUALS, () -> scale = Math.clamp(scale += 0.1f, 1, 2));
    }

    @Override
    protected void onShow() {
        cam.setZoom(0.1f);
        cam.act(CameraAction.zoomTo(scale, 1f, Interpolation.sineOut));
    }

    @Override
    public void update(float delta) {
        animationDelta += delta;
        if (ResourceManager.isLoaded("atlas/lotus.atlas")) {
            TextureAtlas atlas = ResourceManager.getTextureAtlas("atlas/lotus.atlas");
            if (loutsAnimation == null) {
                loutsAnimation = new Animation<>(0.2f, atlas.findRegions("lotus"),
                    Animation.PlayMode.LOOP);
            }
        }
    }

    @Override
    public void draw(float delta) {
        float alpha = Math.min(camera.zoom / scale, 1f);
        getBatch().setColor(1f, 1f, 1f, alpha);
        if (loutsAnimation != null) {
            TextureRegion textureRegion = loutsAnimation.getKeyFrame(animationDelta, true);
            getBatch().draw(textureRegion,
                getCenteredX(textureRegion.getRegionWidth()),
                getCenteredY(textureRegion.getRegionHeight()));
        }
        getBatch().setColor(1, 1, 1, 1);
    }
}
