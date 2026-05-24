package com.nadia.caslab.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.nadia.caslab.game.CampusDashGame;
import com.nadia.caslab.game.GameConstants;

public class Lwjgl3Launcher {

    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return;

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Campus Dash: Deadline Pursuit");
        config.setWindowedMode(GameConstants.VIEWPORT_WIDTH, GameConstants.VIEWPORT_HEIGHT);
        config.setResizable(false);
        config.setForegroundFPS(60);
        config.setIdleFPS(30);
        config.useVsync(true);
        // config.setWindowIcon("assets/icon.png");

        new Lwjgl3Application(new CampusDashGame(), config);
    }
}
