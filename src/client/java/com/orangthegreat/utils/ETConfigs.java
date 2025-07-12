package com.orangthegreat.utils;

import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;


public class ETConfigs {
    private static ETConfigs instance;
    private static BetterArrayList loadedEntities = new BetterArrayList(); //Consists of all entities ever loaded when playing using this mod
    private static BetterArrayList enabledEntities = new BetterArrayList(); //Consists of only entities to be tracked
    private static BetterArrayList colorOfEnabled = new BetterArrayList();
    private static BetterArrayList currentlyLoadedPlayers = new BetterArrayList();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static SettingsList settings = new SettingsList();
    private final Map<String, String> renderModes = new HashMap<>();
    private static Path LOADED_ENTITIES_FILE, ENABLED_ENTITIES_FILE, COLOR_OF_ENABLED_FILE, SETTINGS_FILE, RENDER_MODE_FILE;
    public static final String MOD_ID = "Entity Tracker";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private ETConfigs(){
        try{
            Path configDir = FabricLoader.getInstance().getConfigDir().resolve("EntityTracker");
            Files.createDirectories(configDir);

            LOADED_ENTITIES_FILE = configDir.resolve("LoadedEntities.txt");
            ENABLED_ENTITIES_FILE = configDir.resolve("EnabledEntities.txt");
            COLOR_OF_ENABLED_FILE = configDir.resolve("ColorOfEnabledEntities.txt");
            SETTINGS_FILE = configDir.resolve("Settings.txt");
            RENDER_MODE_FILE = configDir.resolve("RenderMode.json");
            Files.writeString(SETTINGS_FILE, "false\n");
        } catch(Exception e){
            LOGGER.error("Error while creating files");
        }
    }

    public static ETConfigs getInstance(){
        if(instance == null) instance = new ETConfigs();
        return instance;
    }

    public void loadConfig(){
        LOGGER.info("Loading Configs...");
        loadedEntities.loadListFromFile(LOADED_ENTITIES_FILE);
        enabledEntities.loadListFromFile(ENABLED_ENTITIES_FILE);
        colorOfEnabled.loadListFromFile(COLOR_OF_ENABLED_FILE);
        settings.loadListFromFile(SETTINGS_FILE);
        if (Files.exists(RENDER_MODE_FILE)) {
            try {
                String json = Files.readString(RENDER_MODE_FILE).trim();
                if (!json.isEmpty()) {
                    JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
                    if (jsonObject != null && jsonObject.has("renderModes") && !jsonObject.get("renderModes").isJsonNull()) {
                        Type type = new TypeToken<Map<String, String>>(){}.getType();
                        renderModes.clear();
                        renderModes.putAll(gson.fromJson(jsonObject.get("renderModes"), type));
                    }
                } else {
                    LOGGER.warn("Render mode config file is empty; skipping.");
                }
            } catch (Exception e) {
                LOGGER.error("Failed to load render modes config", e);
            }
        }

    }

    public void saveConfig(){
        LOGGER.info("Saving Configs...");
        loadedEntities.saveListToFile(LOADED_ENTITIES_FILE);
        enabledEntities.saveListToFile(ENABLED_ENTITIES_FILE);
        colorOfEnabled.saveListToFile(COLOR_OF_ENABLED_FILE);
        settings.saveListToFile(SETTINGS_FILE);
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("renderModes", gson.toJsonTree(renderModes));
        try {
            Files.writeString(RENDER_MODE_FILE, gson.toJson(jsonObject));
        } catch (Exception e) {
            LOGGER.error("Failed to save render modes", e);
        }
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

    public BetterArrayList getPlayersList(){
        return currentlyLoadedPlayers;
    }

    public Map<String, String> getRenderModes() {
        return renderModes;
    }


}
