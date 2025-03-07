package com.example.satisotomasyonu;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.StageStyle;

public class CreateDialogToExit {
    public CreateDialogToExit(String message){
        createDialog(message);

    }
    private void createDialog(String message) {
        Dialog dialog = new Dialog<>();
        dialog.setHeaderText("Uyarı!");
        dialog.setTitle("");
        dialog.initStyle(StageStyle.UTILITY);
        ButtonType typeOk = new ButtonType("EVET", ButtonBar.ButtonData.OK_DONE);
        ButtonType typeCancel = new ButtonType("HAYIR", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(typeOk,typeCancel);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(50, 50, 50, 50));
        Label error = new Label(message);
        grid.add(error,0,1);
        dialog.getDialogPane().setContent(grid);
        dialog.showAndWait().ifPresent(type -> {
            if (type == typeOk) {
                Platform.exit();
            }
            else {
            }
        });
    }
}
