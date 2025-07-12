package com.orangthegreat.menu;

import net.minecraft.client.gui.screen.Screen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;


public class ScreenOpener {
    private static Screen screenToOpen = null;

    public static void openNextTick(Screen screen) {
        screenToOpen = screen;
    }

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (screenToOpen != null) {
                client.setScreen(screenToOpen);
                screenToOpen = null;
            }
        });
    }
}