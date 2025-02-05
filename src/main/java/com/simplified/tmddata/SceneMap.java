package com.simplified.tmddata;

import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashMap;

public class SceneMap {
    private static HashMap<String, Scene> scenes =  new HashMap<>();
    private static Stage stage;

    public SceneMap(Stage stage){
        this.stage = stage;
    }

    protected static void addScene(String name, Scene scene){
        scenes.put(name,scene);
    }

    protected static void removeScreen(String name){
        scenes.remove(name);
    }

    protected static Stage getStage(){
        return stage;
    }

    protected static String getTitle(){
        return stage.getTitle();
    }

    protected static void setScenes(String name){
        stage.setScene(scenes.get(name));
        stage.setTitle(name);
    }
}
