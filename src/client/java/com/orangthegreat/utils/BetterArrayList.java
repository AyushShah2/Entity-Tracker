package com.orangthegreat.utils;

import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class BetterArrayList {

    private List<String> betterList = new ArrayList<>();

    public List<String> getList(){
        return betterList;
    }

    public boolean add(String s){
        if (!betterList.contains(s)) {
            betterList.add(s);
            return true;
        }
        return false;
    }

    public boolean remove(String s) {
        return betterList.remove(s);
    }

    public boolean contains(String s) {
        return betterList.contains(s);
    }

    public int size() {
        return betterList.size();
    }

    public void clear() {
        betterList.clear();
    }

    public void loadListFromFile(Path path){
        try {
            File file = path.toFile();
            if (!file.exists()) return;

            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String s;

            while((s = bufferedReader.readLine()) != null){
                this.add(s);
            }
            bufferedReader.close();
        } catch (Exception e) {
            throw new RuntimeException("ERROR WHILE TRYING TO LOAD FILE: " + e);
        }
    }

    public void saveListToFile(Path path){
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path.toFile()));

            for (String s: getList()){
                bufferedWriter.write(s + "\n");
            }
            bufferedWriter.close();
        } catch (Exception e) {
            throw new RuntimeException("ERROR WHILE TRYING TO SAVE FILE: " + e);
        }
    }

}
