package com.orangthegreat.menu;

import com.google.common.collect.Lists;
import com.orangthegreat.utils.ETConfigs;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;

import java.util.List;

import static com.orangthegreat.EntityTrackerClient.*;


public class ModMenuScreen {
    private static final ETConfigs modConfigs = ETConfigs.getInstance();

    public static Screen getMainModScreen(Screen parent){
        return getModConfigScreenFactory().create(parent);
    }

    public static ConfigScreenFactory<?> getModConfigScreenFactory(){
        return parent ->{
            ConfigBuilder configBuilder = ConfigBuilder.create().setTitle(Text.literal("Entity Tracker"));
            ConfigEntryBuilder entryBuilder = configBuilder.entryBuilder();

            ConfigCategory modSettingsCategory = configBuilder.getOrCreateCategory(Text.literal("Mod Settings"));
            createModSettingsScreen(modSettingsCategory, entryBuilder);

            ConfigCategory entityListCategory = configBuilder.getOrCreateCategory(Text.literal("Entities"));
            createEntitySelectionScreen(entityListCategory, entryBuilder, modConfigs.getNonPlayerEntityNames());

            ConfigCategory playerListCategory = configBuilder.getOrCreateCategory(Text.literal("Player Entities"));
            createEntitySelectionScreen(playerListCategory, entryBuilder, modConfigs.getPlayerEntityNames());

            configBuilder.setSavingRunnable(() -> {
                modConfigs.refresh();
            });
            return configBuilder.setParentScreen(parent).build();
        };
    }

    public static void createModSettingsScreen(ConfigCategory category, ConfigEntryBuilder entryBuilder){
        category.addEntry(
                entryBuilder.startBooleanToggle(Text.literal("Enable Tracking"), modConfigs.getSettings().isEnabled())
                        .setSaveConsumer(toggleValue -> modConfigs.getSettings().setEnabled(String.valueOf(toggleValue)))
                        .setDefaultValue(modConfigs.getSettings().isEnabled())
                        .build()
        );
    }

    public static void createEntitySelectionScreen(ConfigCategory category, ConfigEntryBuilder entryBuilder, List<String> list){
        for (int i = 0; i < list.size(); i++) {
            final String entityName = list.get(i);

            final String hexColor = modConfigs.getOrCreateSettings(entityName).color;
            final int intColor = (int) Long.parseLong((hexColor).replace("#", ""), 16);

            var toggleEntry = entryBuilder.startBooleanToggle(Text.literal(entityName), modConfigs.getOrCreateSettings(entityName).isEnabled)
                    .setDefaultValue(modConfigs.getOrCreateSettings(entityName).isEnabled)
                    .setSaveConsumer(toggled -> modConfigs.updateEntityEnabled(entityName, toggled))
                    .setTooltip(Text.literal("Toggle tracking for this entity"))
                    .build();

            category.addEntry(toggleEntry);

            var colorEntry = entryBuilder.startColorField(Text.literal(entityName + " Color"), intColor)
                    .setSaveConsumer(color -> modConfigs.updateEntityColor(entityName, String.format("#%06X", color)))
                    .setTooltip(Text.literal("Current Color: " + hexColor))
                    .build();

            var renderModeEntry = entryBuilder.startDropdownMenu(
                            Text.literal(entityName + " Render Mode"),
                            DropdownMenuBuilder.TopCellElementBuilder.of(modConfigs.getOrCreateSettings(entityName).renderMode, s -> {
                                if (s.equalsIgnoreCase("Hitbox") || s.equalsIgnoreCase("Fill Hitbox") || s.equalsIgnoreCase("Fill Entity")) {
                                    return s;
                                }
                                return null;
                            }))
                    .setDefaultValue(modConfigs.getOrCreateSettings(entityName).renderMode)
                    .setSelections(Lists.newArrayList("Hitbox", "Fill Hitbox", "Fill Entity"))
                    .setTooltip(Text.literal("Render with: 'Hitbox', 'Fill Hitbox', or 'Fill Entity'"))
                    .setSaveConsumer(selected -> modConfigs.updateEntityRenderMode(entityName, selected))
                    .build();

            //wrapping into subCategory
            var subCategoryBuilder = entryBuilder.startSubCategory(Text.literal(entityName + " Settings"))
                    .setExpanded(false);

            subCategoryBuilder.add(colorEntry);
            subCategoryBuilder.add(renderModeEntry);
            category.addEntry(subCategoryBuilder.build());

        }
    }
}
