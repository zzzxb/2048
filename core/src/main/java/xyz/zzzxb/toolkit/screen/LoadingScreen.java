package xyz.zzzxb.toolkit.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import xyz.zzzxb.toolkit.core.SceneScreen;
import xyz.zzzxb.toolkit.core.Screen;
import xyz.zzzxb.toolkit.core.camera.CameraAction;
import xyz.zzzxb.toolkit.loaders.ScreenResourceLoader;
import xyz.zzzxb.toolkit.utils.ResourceManager;

import java.lang.reflect.InvocationTargetException;

public class LoadingScreen extends SceneScreen {
    private ShapeRenderer shapeRenderer;
    private Animation<TextureRegion> earthAnimation;
    private final Class<? extends Screen> targetScreenClass;

    private boolean transitioned;
    private float earthDelta;

    public LoadingScreen(Class<? extends Screen> targetScreenClass) {
        this.targetScreenClass = targetScreenClass;
    }

    @Override
    protected void onCreate() {
        shapeRenderer = new ShapeRenderer();
    }

    @Override
    protected void onShow() {
        earthDelta = 0;
        cam.setZoom(1f);
        ScreenResourceLoader.loadScreenResources(targetScreenClass.getSimpleName());
    }

    @Override
    public void update(float delta) {
        earthDelta += delta;
        ResourceManager.update();

        if (ResourceManager.isLoaded("atlas/earth.atlas")) {
            TextureAtlas atlas = ResourceManager.getTextureAtlas("atlas/earth.atlas");
            if (earthAnimation == null) {
                earthAnimation = new Animation<>(0.15f, atlas.findRegions("earth"),
                    Animation.PlayMode.LOOP);
            }
        }

        if (ResourceManager.isLoaded() && !cam.isActing() && !transitioned) {
            cam.act(
                CameraAction.sequence(
                    CameraAction.delay(0.5f),
                    CameraAction.zoomTo(0.1f, 1f, Interpolation.sineOut),
                    CameraAction.run(this::createScreen)
                )
            );
        }
    }

    @Override
    public void draw(float delta) {
        getBatch().setColor(1f, 1f, 1f, camera.zoom);

        if (ResourceManager.isLoaded("images/space.png")) {
            Texture texture = ResourceManager.getTexture("images/space.png");
            float width = getWorldWidth() * camera.zoom;
            float height = getWorldHeight() * camera.zoom;
            getBatch().draw(texture, getCenteredX(width), getCenteredY(height),
                width, height);
        }

        if (earthAnimation != null) {
            float earthSize = 384;
            TextureRegion textureRegion = earthAnimation.getKeyFrame(earthDelta, true);
            getBatch().draw(textureRegion, getCenteredX(earthSize), getCenteredY(earthSize),
                earthSize, earthSize);
        }

        getBatch().setColor(1f, 1f, 1f, 1f);
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

    private void createScreen() {
        try {
            goTo(targetScreenClass.getDeclaredConstructor().newInstance());
        } catch (NoSuchMethodException | InstantiationException |
                 IllegalAccessException | InvocationTargetException e) {
            log.error("无法创建目标屏幕: " + targetScreenClass.getSimpleName(), e);
        }
    }
}
