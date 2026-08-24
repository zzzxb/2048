package xyz.zzzxb.toolkit.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import xyz.zzzxb.toolkit.core.CameraController;
import xyz.zzzxb.toolkit.core.SceneScreen;
import xyz.zzzxb.toolkit.core.camera.CameraAction;
import xyz.zzzxb.toolkit.utils.ResourceManager;

/**
 * @author zzzxb
 * 2026/8/23
 */
public class TwentyFortyEight extends SceneScreen {
    private float scale;
    private CameraController cameraController;

    @Override
    protected void onCreate() {
        cameraController = new CameraController(camera);

        inputManager.addKeyListener(Input.Keys.Q, () -> Gdx.app.exit());
        inputManager.addKeyListener(Input.Keys.MINUS, () -> scale -= 0.1f);
        inputManager.addKeyListener(Input.Keys.EQUALS, () -> scale += 0.1f);

        inputManager.addKeyListener(Input.Keys.A, () -> {
            cameraController.act(
                CameraAction.sequence(
                    CameraAction.moveTo(100, 200, 1.0f),
                    CameraAction.delay(0.3f),
                    CameraAction.shake(10, 0.5f),
                    CameraAction.run(() -> {
                        System.out.println("震动结束！");
                    })
                )
            );
        });

    }

    @Override
    protected void onShow() {
        scale = 2;
    }

    @Override
    public void update(float delta) {
        // ✅ 每帧更新相机控制器，驱动动作执行
        cameraController.update(delta);

        // ✅ 如果没有动作在执行，才手动控制相机位置
        if (!cameraController.isActing()) {
            cameraController.setPosition(getWorldWidth(), getWorldHeight());
            cameraController.setZoom(scale);
        }
    }

    @Override
    public void draw(float delta) {
        // ✅ 应用相机投影矩阵
        cameraController.apply(getBatch());

        getBatch().draw(ResourceManager.getTexture("images/background.png"), 0, 0);
        Texture texture = ResourceManager.getTexture("images/location.png");
        getBatch().draw(texture, getWorldWidth() - (float) texture.getWidth() / 2 * scale,
            getWorldHeight() - (float) texture.getHeight() / 2 * scale,
            getWorldWidth() * scale, getWorldHeight() * scale);
    }
}
