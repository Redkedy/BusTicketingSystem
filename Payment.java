package com.example.satisotomasyonu;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class Payment implements Initializable {
    private BusDesignView bdv;
    ObservableList<TicketType> types;
    @FXML
    Label lblSeatNumber, lblPrice;
    @FXML
    TextField trId, name, surname;
    @FXML
    ComboBox<String> genderType;
    @FXML
    ComboBox<String> ticketType;
    Route route;
    String seatNo;
    Double discountedPrice = 0.0, price;

    protected void main(Route route, String seatNo) {
        try {
            this.route = route;
            this.seatNo = seatNo;
            types = Login.getDb().getPersonTypes();
            ObservableList<String> genders = FXCollections.observableArrayList();
            genders.addAll("Erkek", "Kadın");
            genderType.setItems(genders);
            genderType.setValue(genderType.getItems().get(0));
            ObservableList<String> x = FXCollections.observableArrayList();
            for (int i = 0; i < types.size(); i++) {
                x.add(types.get(i).getTitle());
            }
            ticketType.setItems(x);
            ticketType.getSelectionModel().select(0);
            lblSeatNumber.setText(seatNo);
            lblPrice.setText(Double.valueOf(route.getTicketPrice()).toString() + " TL");
            price = Double.valueOf(route.getTicketPrice());
            Integer index = ticketType.getItems().indexOf(ticketType.getValue());
            discountedPrice = Double.valueOf(this.price * (100 - types.get(index).getDiscountRate()) / 100);

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

    }

    @FXML
    protected void onTicketTypeChange(ActionEvent event) {
        Integer index = ticketType.getItems().indexOf(ticketType.getValue());
        discountedPrice = Double.valueOf(this.price * (100 - types.get(index).getDiscountRate()) / 100);
        lblPrice.setText(discountedPrice + " TL");
        bdv.totalPrice.setText(" ");
    }

    protected int getTicketType() {
        return types.get(ticketType.getItems().indexOf(ticketType.getValue())).getId();
    }

    protected boolean checkFields() {
        return (trId.getText().length() == 11 && !name.getText().isEmpty() && !surname.getText().isEmpty());
    }

    protected Double getPrice() {
        return price;
    }

    protected Double getDiscountedPrice() {
        return this.discountedPrice;
    }

    protected Integer getSeatNumber() {
        return Integer.parseInt(lblSeatNumber.getText());
    }

    protected String getName() {
        return this.name.getText();
    }

    protected String getSurname() {
        return this.surname.getText();
    }

    protected Long getTrId() {
        return Long.parseLong(this.trId.getText());
    }

    protected void setBdv(BusDesignView bdv) {
        this.bdv = bdv;
    }

    protected int getGender() {
        return (genderType.getValue() == "Erkek" ? 0 : 1);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        trId.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    trId.setText(newValue.replaceAll("[^\\d]", ""));

                }
                if (trId.getText().length() > 11) {
                    trId.setText(oldValue);
                }
                if (trId.getText().length() != 11)
                    trId.setStyle("  -fx-text-box-border: red ;  -fx-focus-color: red ;");
                else
                    trId.setStyle("  -fx-text-box-border: green ;  -fx-focus-color: green ;");

            }
        });
    }
}
