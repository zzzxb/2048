package xyz.zzzxb.toolkit;

import xyz.zzzxb.toolkit.core.Game;
import xyz.zzzxb.toolkit.core.ScreenBehavior;
import xyz.zzzxb.toolkit.loaders.ScreenResourceLoader;
import xyz.zzzxb.toolkit.screen.LoadingScreen;
import xyz.zzzxb.toolkit.screen.TwentyFortyEight;
import xyz.zzzxb.toolkit.utils.ConfigManager;
import xyz.zzzxb.toolkit.utils.Logger;

public class SandBoxMain extends Game {

    @Override
    protected void onCreate() {
        ConfigManager.load();
        Logger.applyConfig();

        registerBehavior(LoadingScreen.class, ScreenBehavior.KEEP);
        registerBehavior(TwentyFortyEight.class, ScreenBehavior.DESTROY);
        ScreenResourceLoader.loadCommonResources();
        goTo(new LoadingScreen(TwentyFortyEight.class));
    }
}
