package com.example.satisotomasyonu;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class start extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        stage.initStyle(StageStyle.TRANSPARENT);
        new CreateLogin(stage);


    }

    public static void main(String[] args) {
        launch();
    }


}