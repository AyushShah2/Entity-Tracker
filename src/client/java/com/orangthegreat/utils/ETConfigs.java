package com.orangthegreat.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.orangthegreat.EntityTrackerClient.MC;
import static com.orangthegreat.EntityTrackerClient.entitiesToRender;

public class ETConfigs {
    private static ETConfigs instance;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Map<String, EntitySettings> entityConfigs = new HashMap<>();
    private static ModSettings modSettings = new ModSettings();
    private static Path LOADED_ENTITIES_FILE, SETTINGS_FILE;
    private final String defaultColor = "#FFFFFF";
    private final String defaultRenderMode = "Hitbox";
    private final boolean defaultState = false;
    public static final String MOD_ID = "Entity Tracker";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private ETConfigs(){
        try {
            Path configDir = FabricLoader.getInstance().getConfigDir().resolve("EntityTracker");
            Files.createDirectories(configDir);
            LOADED_ENTITIES_FILE = configDir.resolve("LoadedEntities.json");
            SETTINGS_FILE = configDir.resolve("Settings.txt");
            initializeDefaultsIfMissing();
        }
        catch(Exception e){
            LOGGER.error("Error setting up config directory", e);
        }
    }

    public static ETConfigs getInstance(){
        if(instance == null) instance = new ETConfigs();
        return instance;
    }

    public void loadConfig(){
        try{
            LOGGER.info("Loading Entity Tracker Config...");
            modSettings.loadListFromFile(SETTINGS_FILE);
            if(Files.exists(LOADED_ENTITIES_FILE)){
                Type type = new TypeToken<Map<String, EntitySettings>>() {}.getType();
                Map<String, EntitySettings> loaded = gson.fromJson(new FileReader(LOADED_ENTITIES_FILE.toFile()), type);
                if (loaded != null) {
                    entityConfigs.clear();
                    entityConfigs.putAll(loaded);
                } else {
                    LOGGER.warn("Config file was empty or invalid, using empty config.");
                }
            }
        } catch (Exception e){
            LOGGER.error("Failed to load entity config", e);
        }
    }

    public void saveConfig(){
        try{
            LOGGER.info("Saving Entity Tracker Config...");
            modSettings.saveListToFile(SETTINGS_FILE);
            FileWriter writer = new FileWriter(LOADED_ENTITIES_FILE.toFile());
            gson.toJson(entityConfigs, writer);
            writer.close();
        } catch (Exception e){
            LOGGER.error("Failed to save entity config", e);
        }
    }

    public void refresh(){
        entitiesToRender.clear();
        this.saveConfig();
        this.loadConfig();
        if(MC != null && MC.world != null) {
            for (Entity entity : MC.world.getEntities()) {
                if (this.getEnabledEntityNames().contains(entity.getName().getString()))
                    entitiesToRender.add(entity);
            }
        }
    }

    public ModSettings getSettings(){
        return modSettings;
    }

    public Map<String, EntitySettings> getEntityConfigs(){
        return entityConfigs;
    }

    public EntitySettings getOrCreateSettings(String entityName) {
        return entityConfigs.computeIfAbsent(entityName,
                k -> new EntitySettings(defaultColor, defaultRenderMode, defaultState, defaultState));
    }

    public void updateEntityColor(String entityName, String newColor){
        getOrCreateSettings(entityName).color = newColor;
    }

    public void updateEntityRenderMode(String entityName, String newRenderMode){
        getOrCreateSettings(entityName).renderMode = newRenderMode;
    }

    public void updateEntityType(String entityName, boolean isPlayer){
        getOrCreateSettings(entityName).isPlayer = isPlayer;
    }

    public void updateEntityEnabled(String entityName, boolean enabled){
        getOrCreateSettings(entityName).isEnabled = enabled;
    }

    public List<String> getAllEntityNames() {
        return new ArrayList<>(entityConfigs.keySet());
    }

    public List<String> getEnabledEntityNames() {
        return entityConfigs.entrySet().stream().filter(entry -> entry.getValue().isEnabled).map(Map.Entry::getKey).toList();
    }

    public List<String> getNonPlayerEntityNames() {
        return entityConfigs.entrySet().stream().filter(entry -> !(entry.getValue().isPlayer)).map(Map.Entry::getKey).toList();
    }

    public List<String> getPlayerEntityNames() {
        return entityConfigs.entrySet().stream().filter(entry -> entry.getValue().isPlayer).map(Map.Entry::getKey).toList();
    }

    public void removeAllPlayers() {
        entityConfigs.entrySet().removeIf(entry -> entry.getValue().isPlayer);
    }

    private void initializeDefaultsIfMissing() {
        try {
            if (!Files.exists(LOADED_ENTITIES_FILE) || !Files.exists(SETTINGS_FILE)) {
                this.getOrCreateSettings("Starred Mobs");
                saveConfig(); // Write empty file if missing
                Files.writeString(SETTINGS_FILE, "false\n");
            }
        } catch (IOException e) {
            LOGGER.error("Failed to initialize files", e);
        }
    }
}
