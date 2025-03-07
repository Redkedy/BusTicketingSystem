package com.example.satisotomasyonu;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class CreateLogin {


    public CreateLogin(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
        Parent root = (Parent)loader.load();
        LoginController controller = loader.getController();
        Scene scene = new Scene(root, 972, 661);
        scene.setFill(Color.TRANSPARENT);
        stage.setTitle("Giriş");
        stage.setMaximized(false);
        stage.setScene(scene);
        stage.show();
        controller.initialize(root,stage);
    }
}
