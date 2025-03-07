package com.example.satisotomasyonu;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.ArrayList;


public class BusDesignView {
    @FXML
    GridPane gridBusSeats, chosenSeats;
    @FXML
    Button btnAccept;
    Label totalPrice;
    ArrayList<Button> arrButton = new ArrayList<>();
    ArrayList<Customer> arrCustomers = new ArrayList<>();
    ArrayList<Ticket> arrTickets = new ArrayList<>();
    private DepAndRet depAndRet;

    void main() {
        for (String a : depAndRet.seats) {
            System.out.println(a);
            String seatNumber = a.split(",")[0], gender = a.split(",")[1];
            Button b = (Button) gridBusSeats.lookup("#" + seatNumber);
            b.getStyleClass().remove(b.getStyleClass().size() - 1);
            b.getStyleClass().add(("0".equals(gender) ? "buttonMan" : "buttonWoman"));
            b.setDisable(true);
        }
    }

    void setDepAndRet(DepAndRet depAndRet) {
        this.depAndRet = depAndRet;
        main();
    }

    ArrayList<Button> getArrSeat() {
        return this.arrButton;
    }

    @FXML
    protected void onSeatClick(ActionEvent event) {
        Button seat = (Button) event.getSource();

        // added.setId(seat.getId());
        if (arrButton.contains(seat)) {
            arrButton.remove(seat);
            seat.getStyleClass().remove(seat.getStyleClass().size() - 1);
            seat.getStyleClass().add("button");

        } else if (arrButton.size() < 6) {
            arrButton.add(seat);
            seat.getStyleClass().remove(seat.getStyleClass().size() - 1);
            seat.getStyleClass().add("buttonChosen");

        }
        chosenSeats.getChildren().remove(1, chosenSeats.getChildren().size());
        int row = 1;
        for (int i = 0; i < arrButton.size(); i++) {

            Button added = new Button();
            added.setText(arrButton.get(i).getText());
            added.getStyleClass().remove(added.getStyleClass().size() - 1);
            added.getStyleClass().add("buttonChosen");
            added.setPrefHeight(arrButton.get(i).getPrefHeight());
            added.setPrefWidth(arrButton.get(i).getPrefWidth());
            added.alignmentProperty().bind(arrButton.get(i).alignmentProperty());
            chosenSeats.add(added, i % 2, row);
            if (i % 2 == 1)
                row++;
        }
        if (depAndRet.br.getAccessibleText().toLowerCase().contains("gidiş")) {
            if (depAndRet.getBc().getReturnWay() != null) {

                btnAccept.setText("Dönüş Seferi Seç");
            } else
                btnAccept.setText("Ödemeye Geç");
        } else {
            btnAccept.setText("Ödemeye Geç");
        }
        if (arrButton.size() == 0)
            btnAccept.setVisible(false);
        else
            btnAccept.setVisible(true);

    }

    @FXML
    protected void onBtnAcceptClick(ActionEvent event) {
        GridPane bottom = (GridPane) depAndRet.getBc().getDep().chosen.getBottom();
        AnchorPane bottomOfBottom = (AnchorPane) bottom.getChildren().get(0);
        GridPane x = (GridPane) bottomOfBottom.getChildren().get(0);
        GridPane y = (GridPane) x.getChildren().get(2);
        if (!"Ödemeye Geç".equals(btnAccept.getText())) {
            depAndRet.getBc().content.setVvalue(1.0);
        } else {
            if (y.getChildren().size() == 1) {
                depAndRet.getBc().content.setVvalue(0.0);
            } else {
                String DepId = depAndRet.getBc().getDep().chosen.getId();
                String retId = "null";
                if (depAndRet.getBc().getReturnWay() != null)
                    retId = depAndRet.getBc().getRet().chosen.getId();

                if ("null".equals(retId)) {
                    prepareScene(depAndRet.getBc().getDep().getRoutes().get(Integer.parseInt(DepId)), depAndRet.getBc().getDep().getChosenBusController().getArrSeat(), null, null);
                } else {
                    prepareScene(depAndRet.getBc().getDep().getRoutes().get(Integer.parseInt(DepId)), depAndRet.getBc().getDep().getChosenBusController().getArrSeat(), depAndRet.getBc().getRet().getRoutes().get(Integer.parseInt(retId)), depAndRet.getBc().getRet().getChosenBusController().getArrSeat());
                }


            }

        }


    }

    protected void prepareScene(Route depRoute, ArrayList<Button> depSeats, Route retRoute, ArrayList<Button> retSeat) {
        try {
            ArrayList<Payment> arrPayment = new ArrayList<>();
            AnchorPane root = new AnchorPane();
            GridPane payment = new GridPane();
            Label passenger = new Label("Yolcu Bilgileri");
            Button btnReturnSeatView = new Button("Koltuk Seçimine Geri Dön");
            btnReturnSeatView.getStyleClass().add("btnReturn");
            passenger.getStyleClass().add("lblHeader");
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPercentWidth(25);
            payment.getColumnConstraints().addAll(columnConstraints, columnConstraints, columnConstraints, columnConstraints);
            payment.add(passenger, 0, 0);
            payment.add(btnReturnSeatView, 3, 0);
            System.out.println(depSeats.size());


            for (int i = 0; i < depSeats.size(); i++) {
                FXMLLoader item = new FXMLLoader(getClass().getResource("payment-view.fxml"));
                GridPane contentRows = (GridPane) item.load();
                Payment controller = item.getController();
                controller.main(depRoute, depSeats.get(i).getId());
                controller.setBdv(this);
                payment.addRow(payment.getRowCount(), contentRows);
                GridPane.setColumnSpan(contentRows, GridPane.REMAINING);
                arrPayment.add(controller);
            }
            int depLastRow = arrPayment.size();
            if (retRoute != null) {
                Label returnRoutes = new Label("Dönüş Seferi");
                returnRoutes.getStyleClass().add("lblColumnTitle");
                payment.add(returnRoutes, 0, payment.getRowCount());
                Separator sp = new Separator();
                payment.addRow(payment.getRowCount(), sp);
                GridPane.setColumnSpan(sp, GridPane.REMAINING);
                GridPane.setHalignment(returnRoutes, HPos.LEFT);
                for (int i = 0; i < retSeat.size(); i++) {
                    FXMLLoader item = new FXMLLoader(getClass().getResource("payment-view.fxml"));
                    GridPane contentRows = (GridPane) item.load();
                    Payment controller = item.getController();
                    controller.setBdv(this);
                    controller.main(retRoute, retSeat.get(i).getId());
                    payment.addRow(payment.getRowCount(), contentRows);
                    GridPane.setColumnSpan(contentRows, GridPane.REMAINING);
                    arrPayment.add(controller);
                }
            }
            Label lblPhoneNo = new Label("İletişim Numarası:");
            lblPhoneNo.getStyleClass().add("lblColumnTitle");
            TextField txtPhoneNo = new TextField();
            txtPhoneNo.setPromptText("905...");
            CheckBox sendSms = new CheckBox();
            sendSms.setText("Sms Gönderilsin");
            payment.addRow(payment.getRowCount(), lblPhoneNo, txtPhoneNo, sendSms);
            Label lblDetail = new Label("Toplam Fiyat:");
            lblDetail.getStyleClass().add("lblHeader");
            Label lblInfo = new Label("");
            lblInfo.getStyleClass().add("lblColumnTitle");
            totalPrice = new Label();
            totalPrice.getStyleClass().add("lblDepTime");

            totalPrice.textProperty().addListener((obs, old, newValue) -> {
                Double price = 0.0;
                for (Payment a : arrPayment) {

                    price += a.getDiscountedPrice();
                }
                totalPrice.setText(price.toString() + " TL");

            });
            totalPrice.setText(".");
            Button confirm = new Button("Onayla");
            confirm.getStyleClass().add("btnReturn");
            payment.addRow(payment.getRowCount(), lblDetail, totalPrice, lblInfo, confirm);
            confirm.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    boolean state = true;
                    for (Payment a : arrPayment) {
                        if (!a.checkFields())
                            state = false;
                    }
                    if (txtPhoneNo.getText().length() != 12)
                        state = false;
                    if (!state) {
                        lblInfo.setText("Eksik kısımları doldurun!");
                    } else {
                        int depGroupId, retGroupId;
                        if (depLastRow == 1)
                            depGroupId = -1;
                        else
                            depGroupId = arrPayment.get(0).getSeatNumber();
                        if (arrPayment.size() - depLastRow <= 1)
                            retGroupId = -1;
                        else
                            retGroupId = arrPayment.get(arrPayment.size() - 1).getSeatNumber();

                        for (Payment a : arrPayment) {
                            if (arrPayment.indexOf(a) >= depLastRow) {
                                arrCustomers.add(new Customer(a.getName(), a.getSurname(), a.getTrId(), a.getGender()));
                                arrCustomers.get(arrCustomers.size() - 1).addCustomer();
                                arrTickets.add(new Ticket(retRoute, Long.parseLong(txtPhoneNo.getText()), depAndRet.getBc().getToday(), a.getSeatNumber(),
                                        retGroupId, a.getTicketType(), a.getDiscountedPrice(), arrCustomers.get(arrCustomers.size() - 1)));
                                arrTickets.get(arrTickets.size() - 1).addTicket();

                            } else {
                                arrCustomers.add(new Customer(a.getName(), a.getSurname(), a.getTrId(), a.getGender()));
                                arrCustomers.get(arrCustomers.size() - 1).addCustomer();
                                arrTickets.add(new Ticket(depRoute, Long.parseLong(txtPhoneNo.getText()), depAndRet.getBc().getToday(), a.getSeatNumber(),
                                        depGroupId, a.getTicketType(), a.getDiscountedPrice(), arrCustomers.get(arrCustomers.size() - 1)));
                                arrTickets.get(arrTickets.size() - 1).setCustomerId(arrCustomers.get(arrCustomers.size() - 1));
                                arrTickets.get(arrTickets.size() - 1).addTicket();

                            }
                        }
                        if (sendSms.isSelected()) {
                            String message = "Degerli müsterimiz, " + depRoute.getDepartureCity() + "->" + depRoute.getArrivalCity() + " seferiniz icin " +
                                    "bilet numaraniz:" + arrTickets.get(arrTickets.size() - 1).getId().toString();
                            API sms = new API(txtPhoneNo.getText(), message);
                        }
                        depAndRet.getBc().getContent().setContent(null);
                    }
                }
            });


            root.getChildren().add(payment);
            AnchorPane.setLeftAnchor(payment, 20.0);
            AnchorPane.setRightAnchor(payment, 20.0);
            AnchorPane.setTopAnchor(payment, 20.0);
            depAndRet.getBc().getContent().setContent(root);

            btnReturnSeatView.setOnAction(e -> {
                depAndRet.getBc().getContent().setContent(depAndRet.getBc().getContentGrid());

            });
            txtPhoneNo.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue,
                                    String newValue) {
                    if (!newValue.matches("\\d*")) {
                        txtPhoneNo.setText(newValue.replaceAll("[^\\d]", ""));

                    }
                    if (txtPhoneNo.getText().length() > 12) {
                        txtPhoneNo.setText(oldValue);
                    }
                    if (txtPhoneNo.getText().length() != 12)
                        txtPhoneNo.setStyle("  -fx-text-box-border: red ;  -fx-focus-color: red ;");
                    else
                        txtPhoneNo.setStyle("  -fx-text-box-border: green ;  -fx-focus-color: green ;");

                }
            });


        } catch (IOException ex) {

            System.out.println(ex.getMessage());
        }


    }
}

