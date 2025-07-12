package com.orangthegreat.utils;

public class SettingsList extends BetterArrayList{

    public boolean isEnabled(){
        return this.get(0).equals("true");
    }

    public void setEnabled(String value){
        this.set(0, value);
    }

}
