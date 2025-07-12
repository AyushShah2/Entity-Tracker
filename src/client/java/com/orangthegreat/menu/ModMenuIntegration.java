package com.orangthegreat.menu;

import com.terraformersmc.modmenu.api.ModMenuApi;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import net.minecraft.client.gui.screen.Screen;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<Screen> getModConfigScreenFactory() {
        return ModMenuScreen::getMainModScreen;
    }
}