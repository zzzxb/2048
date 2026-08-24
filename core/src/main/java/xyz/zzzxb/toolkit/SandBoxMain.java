package xyz.zzzxb.toolkit;

import xyz.zzzxb.toolkit.core.Game;
import xyz.zzzxb.toolkit.core.ScreenBehavior;
import xyz.zzzxb.toolkit.screen.LoadingScreen;
import xyz.zzzxb.toolkit.screen.TwentyFortyEight;
import xyz.zzzxb.toolkit.utils.ConfigManager;
import xyz.zzzxb.toolkit.utils.Logger;

public class SandBoxMain extends Game {

    @Override
    protected void onCreate() {
        ConfigManager.load();
        Logger.applyConfig();

        registerBehavior(LoadingScreen.class, ScreenBehavior.DESTROY);
        registerBehavior(TwentyFortyEight.class, ScreenBehavior.DESTROY);
        goTo(new LoadingScreen());
    }
}
