package com.orangthegreat.utils;

public class EntitySettings {
    public String color;
    public String renderMode;
    public boolean isPlayer;
    public boolean isEnabled;

    public EntitySettings(){} //Required for Gson

    public EntitySettings(String color, String renderMode, boolean isPlayer, boolean isEnabled){
        this.color = color;
        this.renderMode = renderMode;
        this.isPlayer = isPlayer;
        this.isEnabled = isEnabled;
    }
}
