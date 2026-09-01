package xyz.zzzxb.toolkit.screen;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import xyz.zzzxb.toolkit.core.SceneScreen;
import xyz.zzzxb.toolkit.core.Screen;
import xyz.zzzxb.toolkit.core.camera.CameraAction;
import xyz.zzzxb.toolkit.loaders.ScreenResourceLoader;
import xyz.zzzxb.toolkit.utils.ResourceManager;

import java.lang.reflect.InvocationTargetException;

public class LoadingScreen extends SceneScreen {
    private final Class<? extends Screen> targetScreenClass;

    private ShapeRenderer shapeRenderer;
    private Animation<TextureRegion> earthAnimation;
    private TextureAtlas earthAtlas;
    private float earthAnimationDelta;

    public LoadingScreen(Class<? extends Screen> targetScreenClass) {
        this.targetScreenClass = targetScreenClass;
    }

    @Override
    protected void onCreate() {
        shapeRenderer = new ShapeRenderer();
    }

    @Override
    protected void onShow() {
        earthAnimationDelta = 0;
        cam.setZoom(1f);
        ScreenResourceLoader.loadScreenResources(targetScreenClass.getSimpleName());
    }

    @Override
    public void update(float delta) {
        earthAnimationDelta += delta;
        updateResource();
        switchScreen();
    }

    @Override
    public void draw(float delta) {
        if (ResourceManager.isLoaded("images/space.png")) {
            Texture texture = ResourceManager.getTexture("images/space.png");
            float width = getWorldWidth() * camera.zoom;
            float height = getWorldHeight() * camera.zoom;
            getBatch().draw(texture, getCenteredX(width), getCenteredY(height),
                width, height);
        }

        if (earthAnimation != null) {
            float earthSize = 384;
            TextureRegion textureRegion = earthAnimation.getKeyFrame(earthAnimationDelta, true);
            getBatch().draw(textureRegion, getCenteredX(earthSize), getCenteredY(earthSize),
                earthSize, earthSize);
            if(earthAnimation.isAnimationFinished(earthAnimationDelta)) {

            }
        }

    }

    @Override
    public void drawUI(float delta) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.96f, 1f, 0.9f, 1);
        shapeRenderer.rect(0, 0, getWorldWidth(), 8);
        shapeRenderer.setColor(0.18f, 0.34f, 0.32f, 1);
        shapeRenderer.rect(2, 2,
            getWorldWidth() - 4, 4);
        shapeRenderer.setColor(0.38f, 0.67f, 0.24f, 1);
        shapeRenderer.rect(2, 2,
            (getWorldWidth() - 4) * ResourceManager.getProgress(), 4);
        shapeRenderer.end();
    }

    @Override
    protected void onGameLoopStart(float delta) {
        getBatch().setColor(1f, 1f, 1f, MathUtils.clamp(camera.zoom, 0.01f, 1));
    }

    @Override
    protected void onGameLoopEnd(float delta) {
        getBatch().setColor(1f, 1f, 1f, 1f);
    }

    private void updateResource() {
        ResourceManager.update();
        if (earthAtlas == null && ResourceManager.isLoaded("atlas/earth.atlas")) {
            if (earthAtlas == null) {
                earthAtlas = ResourceManager.getTextureAtlas("atlas/earth.atlas");
            }
            if (earthAnimation == null) {
                earthAnimation = new Animation<>(0.15f, earthAtlas.findRegions("earth"),
                    Animation.PlayMode.LOOP);
            }
        }
    }

    private void switchScreen() {
        if (ResourceManager.isLoaded() && !cam.isActing()) {
            cam.act(
                CameraAction.sequence(
                    CameraAction.zoomTo(0.1f, 1f, Interpolation.sineOut),
                    CameraAction.run(this::createScreen)
                )
            );
        }
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
