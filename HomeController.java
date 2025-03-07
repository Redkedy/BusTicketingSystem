package com.example.satisotomasyonu;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

public class HomeController implements Initializable {
    @FXML
    private Label lblName,date;
    @FXML
    private AnchorPane dateAnchor;
    private MainController mainController;
    private DatePicker dt = new DatePicker(LocalDate.now());
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Locale.setDefault(new Locale("tr"));


    }
    private void main(){
    lblName.setText(mainController.getLc().getUser().getName());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime now = LocalDateTime.now();
        date.setText(dtf.format(now));
        dateAnchor.getChildren().add(dt);
        dt.setVisible(false);



    }
    void setMainController(MainController mn){
        this.mainController=mn;
        main();
    }
    @FXML
    protected void onButtonClick(MouseEvent event){
        String id=((AnchorPane)(event.getSource())).getId();
        if("account".equals(id))
        {
            mainController.callView("account-setting");
        }
        else if("date".equals(id)){

            if(!dt.isShowing()){
                dt.show();
            }
            else
                dt.hide();
        }
        else if("reports".equals(id)){


        }
        else if("routes".equals(id)){
            mainController.callView("routes");
        }
    }
}
