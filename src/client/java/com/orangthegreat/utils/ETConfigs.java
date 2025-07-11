package com.orangthegreat.utils;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ProjectileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ETConfigs {
    private static ETConfigs instance;
    private static BetterArrayList loadedEntities = new BetterArrayList(); //Consists of all entities ever loaded when playing using this mod
    private static BetterArrayList enabledEntities = new BetterArrayList(); //Consists of only entities to be tracked
    private static BetterArrayList colorOfEnabled = new BetterArrayList();
    private static SettingsList settings = new SettingsList();
    private static Path LOADED_ENTITIES_FILE, ENABLED_ENTITIES_FILE, COLOR_OF_ENABLED_FILE, SETTINGS_FILE;
    public static final String MOD_ID = "Entity Tracker";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private ETConfigs(){
        try{
            Files.createDirectories(FabricLoader.getInstance().getConfigDir().resolve("Entity Renderer"));  // Creates the 'EntityRenderer' folder in config if it doesn't exist
            LOADED_ENTITIES_FILE = FabricLoader.getInstance().getConfigDir().resolve("Entity Renderer").resolve("LoadedEntities.txt");
            ENABLED_ENTITIES_FILE = FabricLoader.getInstance().getConfigDir().resolve("Entity Renderer").resolve("EnabledEntities.txt");
            COLOR_OF_ENABLED_FILE = FabricLoader.getInstance().getConfigDir().resolve("Entity Renderer").resolve("ColorOfEnabledEntities.txt");
            SETTINGS_FILE = FabricLoader.getInstance().getConfigDir().resolve("Entity Renderer").resolve("Settings.txt");
        } catch(Exception e){
            throw new RuntimeException("ERROR WHILE CREATING FILES: " + e);
        }
    }

    public static ETConfigs getInstance(){
        if(instance == null) return new ETConfigs();
        return instance;
    }

    public void loadConfig(){
        LOGGER.info("Loading Configs...");
        loadedEntities.loadListFromFile(LOADED_ENTITIES_FILE);
        enabledEntities.loadListFromFile(ENABLED_ENTITIES_FILE);
        colorOfEnabled.loadListFromFile(COLOR_OF_ENABLED_FILE);
        settings.loadListFromFile(SETTINGS_FILE);
    }

    public void saveConfig(){
        LOGGER.info("Saving Configs...");
        loadedEntities.saveListToFile(LOADED_ENTITIES_FILE);
        enabledEntities.saveListToFile(ENABLED_ENTITIES_FILE);
        colorOfEnabled.saveListToFile(COLOR_OF_ENABLED_FILE);
        settings.saveListToFile(SETTINGS_FILE);
    }

    public BetterArrayList getLoadedEntities(){
        return loadedEntities;
    }

    public BetterArrayList getEnabledEntities(){
        return enabledEntities;
    }

    public BetterArrayList getColorOfEnabled(){
        return colorOfEnabled;
    }

    public SettingsList getSettings(){
        return settings;
    }


}
