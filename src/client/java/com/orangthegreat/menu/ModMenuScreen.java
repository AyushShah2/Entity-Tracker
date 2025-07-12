package com.orangthegreat.menu;

import com.google.common.collect.Lists;
import com.orangthegreat.utils.BetterArrayList;
import com.orangthegreat.utils.ETConfigs;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;

import static com.orangthegreat.EntityTrackerClient.*;


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

            ConfigCategory entityListCategory = configBuilder.getOrCreateCategory(Text.literal("Entities"));
            createEntitySelectionScreen(entityListCategory, entryBuilder, configs.getLoadedEntities());

            ConfigCategory playerListCategory = configBuilder.getOrCreateCategory(Text.literal("Players"));
            createEntitySelectionScreen(playerListCategory, entryBuilder, configs.getPlayersList());

            configBuilder.setSavingRunnable(() -> {
                entitiesToRender.clear();
                configs.saveConfig();
                configs.loadConfig();
                if(MC != null && MC.world != null) {
                    for (Entity entity : MC.world.getEntities()) {
                        if (configs.getEnabledEntities().contains(entity.getName().getString()))
                            entitiesToRender.add(entity);
                    }
                }
            });
            return configBuilder.setParentScreen(parent).build();
        };
    }

    public static void createModSettingsScreen(ConfigCategory category, ConfigEntryBuilder entryBuilder){
        category.addEntry(
                entryBuilder.startBooleanToggle(Text.literal("Enable Tracking"), configs.getSettings().isEnabled())
                        .setSaveConsumer(toggleValue -> configs.getSettings().setEnabled(String.valueOf(toggleValue)))
                        .setDefaultValue(false)
                        .build()
        );
    }

    public static void createEntitySelectionScreen(ConfigCategory category, ConfigEntryBuilder entryBuilder, BetterArrayList list){
        for (int i = 0; i < list.size(); i++) {
            final String entityName = list.get(i);
            final int indexOfEnabled = configs.getEnabledEntities().indexOf(entityName);

            int defaultIntColor = 0xFFFFFF; // Default white
            String defaultHexColor = String.format("#%06X", defaultIntColor);

            if (indexOfEnabled != -1 && indexOfEnabled < configs.getColorOfEnabled().size()) {
                defaultHexColor = configs.getColorOfEnabled().get(indexOfEnabled);
                try {
                    defaultIntColor = (int) Long.parseLong(defaultHexColor.replace("#", ""), 16); // use Long to avoid overflow
                } catch (NumberFormatException ignored) {}
            }

            final int intColor = defaultIntColor;
            final String hexColor = defaultHexColor;

            var toggleEntry = entryBuilder.startBooleanToggle(Text.literal(entityName), indexOfEnabled != -1)
                    .setDefaultValue(indexOfEnabled != -1)
                    .setSaveConsumer(toggled -> {
                        if (toggled) {
                            if (configs.getEnabledEntities().add(entityName)){
                                final int newIndex = configs.getEnabledEntities().indexOf(entityName);
                                LOGGER.info(newIndex + " " + configs.getColorOfEnabled().size());
                                if (configs.getColorOfEnabled().size() <= newIndex){
                                    configs.getColorOfEnabled().forceAdd(hexColor);
                                }
                                else {
                                    configs.getColorOfEnabled().set(newIndex, hexColor);
                                }
                            }
                        } else if (!toggled && indexOfEnabled != -1) {
                            if (indexOfEnabled >= 0 && indexOfEnabled < configs.getColorOfEnabled().size()) {
                                configs.getColorOfEnabled().remove(indexOfEnabled);
                                configs.getEnabledEntities().remove(indexOfEnabled);
                            }
                        }
                    })
                    .setTooltip(Text.literal("Toggle tracking for this entity"))
                    .build();

            category.addEntry(toggleEntry);

            final int currentIndex = configs.getEnabledEntities().indexOf(entityName);

            var colorEntry = entryBuilder.startColorField(Text.literal(entityName + " Color"), intColor)
                    .setSaveConsumer(color -> {
                        if (currentIndex >= 0 && currentIndex < configs.getColorOfEnabled().size()) configs.getColorOfEnabled().set(currentIndex, String.format("#%06X", color));
                    })
                    .setTooltip(Text.literal("Current Color: " + hexColor))
                    .build();

            var renderModeEntry = entryBuilder.startDropdownMenu(
                            Text.literal(entityName + "Render Mode"),
                            DropdownMenuBuilder.TopCellElementBuilder.of("Hitbox", s -> {
                                if (s.equalsIgnoreCase("Hitbox") || s.equalsIgnoreCase("Fill Hitbox") || s.equalsIgnoreCase("Fill Entity")) {
                                    return s;
                                }
                                return null;
                            }))
                    .setDefaultValue(configs.getRenderModes().getOrDefault(entityName, "Hitbox"))
                    .setSelections(Lists.newArrayList("Hitbox", "Fill Hitbox", "Fill Entity"))
                    .setTooltip(Text.literal("Render with: 'Hitbox', 'Fill Hitbox', or 'Fill Entity'"))
                    .setSaveConsumer(selected -> {
                        configs.getRenderModes().put(entityName, selected);
                    })
                    .build();

            //wrapping into subCategory
            var subCategoryBuilder = entryBuilder.startSubCategory(Text.literal(entityName + " Settings"))
                    .setExpanded(false);

            subCategoryBuilder.add(colorEntry);
            subCategoryBuilder.add(renderModeEntry);
            category.addEntry(subCategoryBuilder.build());

        }
    }

//    public static void createPlayerSelectionScreen(ConfigCategory category, ConfigEntryBuilder entryBuilder){
//        for (int i = 0; i < configs.getPlayersList().size(); i++) {
//            final String playerName = configs.getPlayersList().get(i);
//            final int indexOfEnabled = configs.getEnabledEntities().indexOf(playerName);
//
//            int defaultIntColor = 0xFFFFFF; // Default white
//            String defaultHexColor = String.format("#%06X", defaultIntColor);
//
//            if (indexOfEnabled != -1 && indexOfEnabled < configs.getColorOfEnabled().size()) {
//                defaultHexColor = configs.getColorOfEnabled().get(indexOfEnabled);
//                try {
//                    defaultIntColor = (int) Long.parseLong(defaultHexColor.replace("#", ""), 16); // use Long to avoid overflow
//                } catch (NumberFormatException ignored) {}
//            }
//
//            final int intColor = defaultIntColor;
//            final String hexColor = defaultHexColor;
//
//            var toggleEntry = entryBuilder.startBooleanToggle(Text.literal(playerName), indexOfEnabled != -1)
//                    .setDefaultValue(indexOfEnabled != -1)
//                    .setSaveConsumer(toggled -> {
//                        if (toggled) {
//                            if (configs.getEnabledEntities().add(playerName)){
//                                final int newIndex = configs.getEnabledEntities().indexOf(playerName);
//                                LOGGER.info(newIndex + " " + configs.getColorOfEnabled().size());
//                                if (configs.getColorOfEnabled().size() <= newIndex){
//                                    configs.getColorOfEnabled().forceAdd(hexColor);
//                                }
//                                else {
//                                    configs.getColorOfEnabled().set(newIndex, hexColor);
//                                }
//                            }
//                        } else if (!toggled && indexOfEnabled != -1) {
//                            if (indexOfEnabled >= 0 && indexOfEnabled < configs.getColorOfEnabled().size()) {
//                                configs.getColorOfEnabled().remove(indexOfEnabled);
//                                configs.getEnabledEntities().remove(indexOfEnabled);
//                            }
//                        }
//                    })
//                    .setTooltip(Text.literal("Toggle tracking for this player"))
//                    .build();
//
//            category.addEntry(toggleEntry);
//
//            final int currentIndex = configs.getEnabledEntities().indexOf(playerName);
//
//            var colorEntry = entryBuilder.startColorField(Text.literal(playerName + " Color"), intColor)
//                    .setSaveConsumer(color -> {
//                        if (currentIndex >= 0 && currentIndex < configs.getColorOfEnabled().size()) configs.getColorOfEnabled().set(currentIndex, String.format("#%06X", color));
//                    })
//                    .setTooltip(Text.literal("Current Color: " + hexColor))
//                    .build();
//
//            var renderModeEntry = entryBuilder.startDropdownMenu(
//                            Text.literal(playerName + "Render Mode"),
//                            DropdownMenuBuilder.TopCellElementBuilder.of("Hitbox", s -> {
//                                if (s.equalsIgnoreCase("Hitbox") || s.equalsIgnoreCase("Fill Hitbox") || s.equalsIgnoreCase("Fill Entity")) {
//                                    return s;
//                                }
//                                return null;
//                            }))
//                    .setDefaultValue(configs.getRenderModes().getOrDefault(playerName, "Hitbox"))
//                    .setSelections(Lists.newArrayList("Hitbox", "Fill Hitbox", "Fill Entity"))
//                    .setTooltip(Text.literal("Render with: 'Hitbox', 'Fill Hitbox', or 'Fill Entity'"))
//                    .setSaveConsumer(selected -> {
//                        configs.getRenderModes().put(playerName, selected);
//                    })
//                    .build();
//
//            //wrapping into subCategory
//            var subCategoryBuilder = entryBuilder.startSubCategory(Text.literal(playerName + " Settings"))
//                    .setExpanded(false);
//
//            subCategoryBuilder.add(colorEntry);
//            subCategoryBuilder.add(renderModeEntry);
//            category.addEntry(subCategoryBuilder.build());
//
//        }
//    }
}
