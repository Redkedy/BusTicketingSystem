package com.example.satisotomasyonu;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.StageStyle;

public class ThrowDialog extends Exception{

    public ThrowDialog(String message){
        super(message);
        createDialog(message);

    }
    private void createDialog(String message) {
        Dialog dialog = new Dialog<>();
        dialog.setHeaderText("HATA");
        dialog.setTitle("");
        dialog.initStyle(StageStyle.UTILITY);
        ButtonType typeOk = new ButtonType("ÇIKIŞ", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(typeOk);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(50, 50, 50, 50));
        Label error = new Label(message);
        grid.add(error,0,1);
        dialog.getDialogPane().setContent(grid);
        dialog.showAndWait();
    }
}
