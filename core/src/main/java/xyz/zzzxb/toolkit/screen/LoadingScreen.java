package xyz.zzzxb.toolkit.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import xyz.zzzxb.toolkit.core.SceneScreen;
import xyz.zzzxb.toolkit.core.Screen;
import xyz.zzzxb.toolkit.loaders.ScreenResourceLoader;
import xyz.zzzxb.toolkit.utils.ResourceManager;

import java.lang.reflect.InvocationTargetException;

public class LoadingScreen extends SceneScreen {
    private ShapeRenderer shapeRenderer;
    private Animation<TextureRegion> earthAnimation;
    private float earthDelta;
    private final Class<? extends Screen> targetScreenClass;

    public LoadingScreen(Class<? extends Screen> targetScreenClass) {
        this.targetScreenClass = targetScreenClass;
    }


    @Override
    protected void onCreate() {
        shapeRenderer = new ShapeRenderer();
        inputManager.addKeyListener(Input.Keys.Q, () -> Gdx.app.exit());
    }

    @Override
    protected void onShow() {
        earthDelta = 0;
        ScreenResourceLoader.loadScreenResources(targetScreenClass.getSimpleName());
    }

    @Override
    public void update(float delta) {
        ResourceManager.update();
        earthDelta += delta;
        if (ResourceManager.isLoaded("atlas/earth.atlas")) {
            TextureAtlas atlas = ResourceManager.getTextureAtlas("atlas/earth.atlas");
            if (earthAnimation == null) {
                earthAnimation = new Animation<>(0.15f,
                    atlas.findRegions("earth"),
                    Animation.PlayMode.LOOP);
            }
        }

        if (ResourceManager.isLoaded()) {
            try {
                goTo(targetScreenClass.getDeclaredConstructor().newInstance());
            } catch (NoSuchMethodException | InstantiationException |
                     IllegalAccessException | InvocationTargetException e) {
                log.error("无法创建目标屏幕: " + targetScreenClass.getSimpleName(), e);
            }
        }
    }

    @Override
    public void draw(float delta) {
        if (ResourceManager.isLoaded("images/space.png")) {
            getBatch().draw(ResourceManager.getTexture("images/space.png"), 0, 0);
        }
        if (earthAnimation != null) {
            float earthSize = 384;
            getBatch().draw(earthAnimation.getKeyFrame(earthDelta, true),
                getCenteredX(earthSize), getCenteredY(earthSize),
                earthSize, earthSize);
        }
    }

    @Override
    public void drawUI(float delta) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(0, 0, getWorldWidth(), 8);
        shapeRenderer.setColor(Color.OLIVE);
        shapeRenderer.rect(2, 2,
            (getWorldWidth() - 4) * ResourceManager.getProgress(), 4);
        shapeRenderer.end();
    }
}
