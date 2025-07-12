package com.orangthegreat.utils;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;


public class BetterArrayList extends ArrayList<String>{

    @Override
    public boolean add(String s){
        if (!this.contains(s)) {
            return super.add(s);
        }
        return false;
    }

    public boolean forceAdd(String s){
        return super.add(s);
    }

    @Override
    public boolean remove(Object o) {
        return super.remove(o);
    }


    @Override
    public boolean contains(Object o) {
        return super.contains(o);
    }

    @Override
    public int size() {
        return super.size();
    }

    @Override
    public void clear() {
        super.clear();
    }

    public void loadListFromFile(Path path){
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path.toFile()));
            String s;

            while((s = bufferedReader.readLine()) != null){
                this.add(s);
            }
            bufferedReader.close();
        } catch (Exception e) {
            //throw new RuntimeException("ERROR WHILE TRYING TO LOAD FILE: " + e);
        }
    }

    public void saveListToFile(Path path){
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path.toFile()));

            for (String s: this){
                bufferedWriter.write(s);
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        } catch (Exception e) {
            //throw new RuntimeException("ERROR WHILE TRYING TO SAVE FILE: " + e);
        }
    }
}
