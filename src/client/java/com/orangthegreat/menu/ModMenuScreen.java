package com.orangthegreat.menu;

import com.google.common.collect.Lists;
import java.awt.Color;
import com.orangthegreat.utils.ETConfigs;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.api.ConfigScreen;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import static com.orangthegreat.EntityTrackerClient.currentColor;

public class ModMenuScreen {
    private static final ETConfigs configs = ETConfigs.getInstance();

    public static Screen getMainModScreen(Screen parent){
        return getModConfigScreenFactory().create(parent);
    }

    public static ConfigScreenFactory<?> getModConfigScreenFactory(){
        return parent ->{
            ConfigBuilder configBuilder = ConfigBuilder.create().setTitle(Text.literal("Entity Tracker"));
            ConfigEntryBuilder entryBuilder = configBuilder.entryBuilder();

            ConfigCategory modSettingsCategory = configBuilder.getOrCreateCategory(Text.literal("Mod Settings"));
            createModSettingsScreen(modSettingsCategory, entryBuilder);

            //ConfigCategory entityListCategory = configBuilder.getOrCreateCategory(Text.literal("Entities"));
            //createEntitySelectionScreen(entityListCategory, entryBuilder);

            configBuilder.setSavingRunnable(() -> {
                configs.saveConfig();
                configs.loadConfig();
            });
            return configBuilder.setParentScreen(parent).build();
        };
    }

    public static void createModSettingsScreen(ConfigCategory category, ConfigEntryBuilder entryBuilder){
        category.addEntry(
                entryBuilder.startBooleanToggle(Text.literal("Enable Tracking"), configs.getSettings().isEnabled())
                        .setSaveConsumer(toggleValue -> configs.getSettings().mainToggle())
                        .setDefaultValue(false)
                        .build()
        );
        category.addEntry(entryBuilder.startDropdownMenu(Text.literal("Suggestion Random Int"), DropdownMenuBuilder.TopCellElementBuilder.of(10,
                s -> {
                    try {
                        return Integer.parseInt(s);
                    } catch (NumberFormatException ignored) {

                    }
                    return null;
                })).setDefaultValue(10).setSelections(Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)).build());
        category.addEntry(
                entryBuilder.startColorField(Text.literal("Default Color"), Color.decode(currentColor).getRGB())
                        .setSaveConsumer(color -> currentColor = String.format("#%06X", (0xFFFFFF & color)))
                        .build()
        );
    }

}
