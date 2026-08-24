package xyz.zzzxb.toolkit.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import xyz.zzzxb.toolkit.core.SceneScreen;
import xyz.zzzxb.toolkit.utils.ResourceManager;

/**
 *
 * @author zzzxb
 * 2026/8/23
 */
public class TwentyFortyEight extends SceneScreen {
    private float scale;

    @Override
    protected void onCreate() {
        inputManager.addKeyListener(Input.Keys.Q, () -> Gdx.app.exit());
        inputManager.addKeyListener(Input.Keys.MINUS, () -> scale -= 0.1f);
        inputManager.addKeyListener(Input.Keys.EQUALS, () -> scale += 0.1f);
        scale = camera.zoom;
    }

    @Override
    public void update(float delta) {
        camera.position.set(getWorldWidth(), getWorldHeight(), 0);
        camera.zoom = scale;
    }

    @Override
    public void draw(float delta) {
        getBatch().draw(ResourceManager.getTexture("images/background.png"), 0, 0);
        Texture texture = ResourceManager.getTexture("images/location.png");
        getBatch().draw(texture, getWorldWidth() - (float) texture.getWidth() / 2 * scale,
            getWorldHeight() - (float) texture.getHeight() / 2 * scale,
            getWorldWidth() * scale, getWorldHeight() * scale);
    }
}
