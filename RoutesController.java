package com.example.satisotomasyonu;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class RoutesController implements Initializable {
    private MainController mainController;
    String pattern = "yyyy-MM-dd";
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
    @FXML
    Label lblInfo;
    @FXML
    TableView<Route> routesTable;
    @FXML
    Button filter,showAll;
    @FXML
    DatePicker date;
    @FXML
    ComboBox<String> from,to;
    @FXML
    TableColumn<Route,Integer> id,busId,ticketPrice;
    @FXML
    TableColumn<Route,String>departureCity,arrivalCity,driver1,driver2,driver3,departureDate,arrivalDate;

    @Override
    public void initialize(URL location, ResourceBundle resources) {


    }
    void setMainController(MainController mn){
        this.mainController=mn;
        main();
    }
    private void main(){
        showAll.setDisable(true);
        filter.setDisable(true);
        from.setDisable(true);
        to.setDisable(true);
        try{
            from.setItems(Login.getDb().getDistinct());
            if(from.getItems().size()!=0){
                from.setDisable(false);
            }
            else{
                from.setDisable(true);
                lblInfo.setText("Sefer bulunamadı!");
            }
            from.valueProperty().addListener((obs,oldVal,newVal)-> {
                try{
                    to.setDisable(false);
                    to.setItems(Login.getDb().getDistinct(newVal));
                }
                catch (SQLException ex){

                }

            });
            to.valueProperty().addListener((obs,oldVal,newVal)->{
                if(newVal!=null){
                    showAll.setDisable(false);
                    if(date.getValue()!=null)
                        filter.setDisable(false);
                }
                else{
                    showAll.setDisable(true);
                    filter.setDisable(true);
                }

            });
        }
        catch (SQLException ex){

        System.out.println(ex.getMessage());
        }


        date.setConverter(new StringConverter<LocalDate>() {

            @Override public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });
        date.valueProperty().addListener(((observable, oldValue, newValue) -> {
            if(newValue!=null){
                filter.setDisable(false);
            }
            else
                filter.setDisable(true);
        }));

        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        departureCity.setCellValueFactory(new PropertyValueFactory<>("departureCity"));
        arrivalCity.setCellValueFactory(new PropertyValueFactory<>("arrivalCity"));
        driver1.setCellValueFactory(new PropertyValueFactory<>("driver1"));
        driver2.setCellValueFactory(new PropertyValueFactory<>("driver2"));
        driver3.setCellValueFactory(new PropertyValueFactory<>("driver3"));
        departureDate.setCellValueFactory(new PropertyValueFactory<>("departureDate"));
        arrivalDate.setCellValueFactory(new PropertyValueFactory<>("arrivalDate"));
        busId.setCellValueFactory(new PropertyValueFactory<>("busPlate"));
        ticketPrice.setCellValueFactory(new PropertyValueFactory<>("ticketPrice"));

    }
    @FXML
    protected void onShowAllClick(ActionEvent event){
        try{

            lblInfo.setText((String) from.getValue()+'-'+to.getValue().toString()+"/Tüm Seferler");
            ObservableList<Route> arr=Login.getDb().getSpecifiedRoutes(from.getValue(),to.getValue(), dateFormatter.format(LocalDateTime.now()),true);
            routesTable.setItems(arr);

        }
        catch ( Exception ex) {
            System.out.println(ex.getMessage());
        }



    }
    @FXML
    protected void onFilterClick(ActionEvent event){
        try{
            ObservableList<Route> arr=Login.getDb().getSpecifiedRoutes(from.getValue(),to.getValue(), date.getValue().toString(),false);
            if(arr.size()!=0){
            lblInfo.setText(from.getValue()+"-"+to.getValue()+"/"+date.getValue().toString());
            routesTable.setItems(arr);

            }
            else{
                lblInfo.setText("Seçili Tarihte Sefer Bulunamadı");
            }
        }
        catch (SQLException ex){

        }


    }
}
