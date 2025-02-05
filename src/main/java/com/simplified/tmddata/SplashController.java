package com.simplified.tmddata;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SplashController {
    @FXML
    private Label welcomeText;


    @FXML
    protected void onHelloButtonClick() {
        switchScene();
    }

    protected static void switchScene(){
        if(SceneMap.getTitle().equals("splash")) {
            SceneMap.setScenes("data");
        }
    }


}