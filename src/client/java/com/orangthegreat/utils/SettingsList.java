package com.orangthegreat.utils;

import java.util.List;

public class SettingsList extends BetterArrayList{

    public boolean isEnabled(){
        return this.get(0).equals("true");
    }

    public void mainToggle(){
        this.set(0, this.get(0).equals("true") ? "false" : "true");
    }

    public String getHighlightMode(){
        return this.get(1);
    }

    public void setHighlightMode(String mode){
        this.set(1, mode);
    }
}
