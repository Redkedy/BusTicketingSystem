package com.example.satisotomasyonu;

import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.ResourceBundle;

public class BookingController implements Initializable {
    private MainController mainController;
    private String pattern = "yyyy-MM-dd";
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
    private DepAndRet dep, ret;
    private ObservableList<Route> departure, returnWay;
    private GridPane contentGrid;
    private String today = dateFormatter.format(LocalDate.now());
    @FXML
    ComboBox<String> from, to;
    @FXML
    Label lblReturn, lblInfo;
    @FXML
    DatePicker returnDate, departureDate;
    @FXML
    Button list;
    @FXML
    GridPane grid;
    @FXML
    ScrollPane content;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    void setMainController(MainController mn) {
        this.mainController = mn;
        main();
    }

    protected ScrollPane getContent() {
        return this.content;
    }

    protected DepAndRet getDep() {
        return this.dep;
    }

    protected DepAndRet getRet() {
        return this.ret;
    }

    protected ObservableList<Route> getReturnWay() {
        return this.returnWay;
    }

    protected GridPane getContentGrid() {
        return this.contentGrid;
    }

    protected String getToday() {
        return this.today;
    }

    private void main() {
        list.setDisable(true);
        to.setDisable(true);
        departureDate.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();

                setDisable(empty || date.compareTo(today) < 0);
            }
        });

        departureDate.valueProperty().addListener((ov, oldValue, newValue) -> {
            returnDate.setDayCellFactory(picker -> new DateCell() {
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    setDisable(empty || date.compareTo(newValue) < 0);

                }
            });
            returnDate.setValue(newValue);
        });
        try {
            from.setItems(Login.getDb().getDistinct());
            if (from.getItems().size() != 0) {
                from.setDisable(false);
            } else {
                from.setDisable(true);
                lblInfo.setText("Sefer bulunamadı!");
            }
            from.valueProperty().addListener((obs, oldVal, newVal) -> {

                to.setDisable(false);
                try {
                    to.setItems(Login.getDb().getDistinct(newVal));
                } catch (SQLException e) {
                    e.printStackTrace();
                }


            });
            from.valueProperty().addListener(listDisabledControl());
            to.valueProperty().addListener(listDisabledControl());
            departureDate.valueProperty().addListener(listDisabledControl());
            departureDate.setConverter(date());
            returnDate.setConverter(date());

        } catch (SQLException ex) {

        }


    }

    StringConverter<LocalDate> date() {
        StringConverter converter = new StringConverter<LocalDate>() {

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        };
        return converter;

    }

    private ChangeListener listDisabledControl() {
        ChangeListener listener = ((observable, oldValue, newValue) -> {
            if (from.getValue() != null && to.getValue() != null && departureDate.getValue() != null)
                list.setDisable(false);
            else
                list.setDisable(true);
        }
        );
        return listener;
    }

    @FXML
    protected void onOneWay(ActionEvent event) {
        lblReturn.setVisible(false);
        returnDate.setVisible(false);
        GridPane.setColumnIndex(list, 3);
    }

    @FXML
    protected void onReturnWay(ActionEvent event) {
        lblReturn.setVisible(true);
        returnDate.setVisible(true);
        GridPane.setColumnIndex(list, 4);
    }

    @FXML
    protected void onListClick(ActionEvent event) {
        try {
            lblInfo.setText("");
            content.setContent(null);
            departure = Login.getDb().getSpecifiedRoutes(from.getValue(), to.getValue(), departureDate.getValue().toString(), false);
            if (departure.size() == 0)
                lblInfo.setText("Seçili tarihte gidiş seferi bulunamadı!");
            else {
                if (returnDate.isVisible()) {
                    returnWay = Login.getDb().getSpecifiedRoutes(to.getValue(), from.getValue(), returnDate.getValue().toString(), false);
                    if (returnWay.size() == 0)
                        lblInfo.setText("Seçili tarihte dönüş seferi bulunamadı!");
                    else {
                        //Dönüş bileti varsa yapılacak işlemler
                        prepareScene(departure, returnWay);
                    }
                } else {
                    returnWay = null;
                    prepareScene(departure, null);
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex);
        }
    }

    private void prepareScene(ObservableList<Route> departure, ObservableList<Route> returnWay) {
        contentGrid = new GridPane();
        ColumnConstraints cl = new ColumnConstraints();
        cl.setPercentWidth(100);
        contentGrid.getColumnConstraints().add(cl);
        dep = new DepAndRet(departure, "Gidiş Seferleri ", this);
        contentGrid.addRow(0, dep.addDatas());
        if (returnWay != null) {
            ret = new DepAndRet(returnWay, "Dönüş Seferleri ", this);
            contentGrid.addRow(1, ret.addDatas());

        }
        content.setContent(contentGrid);
        content.setStyle("-fx-background:white");
    }
}

class DepAndRet {
    ObservableList<Route> route;
    BorderPane br, chosen;
    BookingController bc;
    String way;
    GridPane gridPane;
    Locale tr = new Locale("tr");
    LocalDate date;
    Label lblHeader, lblTitleDep, lblDepDate, lblArrDate, lblRemaining, lblPrice, lblNull, lblDepCity, lblArrCity, lblEmptySeat, lblDepTime, lblArrTime, lblTicketPrice;
    ColumnConstraints twenty;
    StackPane stackHeader, stackTitle;
    GridPane inner, bottom;
    ObservableList<String> seats;
    Integer fullSeat, seatSize;
    String emptySeat;
    Select chose;
    Separator sep;
    BusDesignView busController, chosenController;

    DepAndRet(ObservableList<Route> route, String way, BookingController bc) {
        this.bc = bc;
        this.route = route;
        this.way = way;
    }

    GridPane addDatas() {
        gridPane = new GridPane();
        date = null;
        date = LocalDate.parse(route.get(0).getDepartureDate().split(" ")[0], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        lblHeader = new Label(route.get(0).getDepartureCity().toUpperCase() + ">>" + route.get(0).getArrivalCity().toUpperCase() + " Otobüs Bileti");
        lblTitleDep = new Label(way + date + " " + date.getDayOfWeek().getDisplayName(TextStyle.FULL, tr));
        lblHeader.getStyleClass().add("lblHeader");
        lblTitleDep.getStyleClass().add("lblTitle");
        twenty = new ColumnConstraints();
        twenty.setPercentWidth(20.0);
        twenty.setHalignment(HPos.CENTER);
        gridPane.getColumnConstraints().addAll(twenty, twenty, twenty, twenty, twenty);
        stackHeader = new StackPane();
        stackHeader.getChildren().add(lblHeader);
        stackTitle = new StackPane();
        stackTitle.getChildren().add(lblTitleDep);
        stackTitle.setStyle("-fx-background-color:#215a78");
        gridPane.addRow(gridPane.getRowCount(), stackHeader);
        gridPane.addRow(gridPane.getRowCount(), stackTitle);
        GridPane.setColumnSpan(stackHeader, GridPane.REMAINING);
        GridPane.setColumnSpan(stackTitle, GridPane.REMAINING);
        lblHeader.setAlignment(Pos.CENTER_LEFT);
        StackPane.setAlignment(lblHeader, Pos.CENTER_LEFT);
        StackPane.setAlignment(lblTitleDep, Pos.CENTER_LEFT);
        lblDepDate = new Label("KALKIŞ");
        lblArrDate = new Label("VARIŞ");
        lblRemaining = new Label("KALAN KOLTUK");
        lblPrice = new Label("FİYAT");
        lblNull = new Label(" ");
        Label[] arr = {lblDepDate, lblArrDate, lblRemaining, lblPrice, lblNull};
        int row = gridPane.getRowCount();
        for (Label i : arr) {
            i.getStyleClass().add("lblColumnTitle");
            gridPane.addRow(row, i);
        }
        for (int i = 0; i < route.size(); i++) {
            try {
                br = new BorderPane();
                br.setAccessibleText(way);
                inner = new GridPane();
                bottom = new GridPane();
                inner.getColumnConstraints().addAll(twenty, twenty, twenty, twenty, twenty);
                seats = Login.getDb().getSeats(Long.valueOf(route.get(i).getId()));
                br.setId(String.valueOf(i));
                fullSeat = seats.size();
                seatSize = route.get(i).getBus().getNumberOfSeats();
                emptySeat = String.valueOf(seatSize - fullSeat);
                lblDepCity = new Label(route.get(i).getDepartureCity());
                lblDepCity.getStyleClass().add("lblDepCity");
                lblArrCity = new Label(route.get(i).getArrivalCity());
                lblArrCity.getStyleClass().add("lblDepCity");
                lblEmptySeat = new Label(emptySeat);
                lblEmptySeat.getStyleClass().add("lblDepCity");
                lblDepTime = new Label(route.get(i).getDepartureDate().split(" ")[1]);
                lblDepTime.getStyleClass().add("lblDepTime");
                lblArrTime = new Label(route.get(i).getArrivalDate().split(" ")[1]);
                lblArrTime.getStyleClass().add("lblDepTime");
                lblTicketPrice = new Label(route.get(i).getTicketPrice().toString());
                lblTicketPrice.getStyleClass().add("lblDepTime");
                FXMLLoader fx = new FXMLLoader(getClass().getResource("bus-design-view-" + seatSize.toString() + ".fxml"));
                AnchorPane ap = fx.load();
                ColumnConstraints sdf = new ColumnConstraints();
                sdf.setPercentWidth(100);
                bottom.getColumnConstraints().add(sdf);
                bottom.addRow(bottom.getRowCount(), ap);
                busController = fx.getController();
                busController.setDepAndRet(this);
                chose = new Select(br, this, bottom, busController);
                chose.getStyleClass().add("chose");
                if ("0".equals(lblTicketPrice.getText())) {
                    chose.setText("DOLU");
                    chose.setDisable(true);
                } else {
                    chose.changeState();
                }
                inner.addRow(0, lblDepCity, lblArrCity, lblEmptySeat, lblTicketPrice, chose);
                inner.addRow(1, lblDepTime, lblArrTime);
                GridPane.setColumnIndex(lblDepTime, 0);
                GridPane.setColumnIndex(lblArrTime, 1);
                br.setTop(inner);
                sep = new Separator();
                gridPane.addRow(gridPane.getRowCount(), sep);
                GridPane.setColumnSpan(sep, GridPane.REMAINING);
                gridPane.addRow(gridPane.getRowCount(), br);
                GridPane.setColumnSpan(br, GridPane.REMAINING);
                GridPane.setRowSpan(lblEmptySeat, 2);
                GridPane.setRowSpan(lblTicketPrice, 2);
                GridPane.setRowSpan(chose, 2);
                br.getStyleClass().add("borderPane");
                BorderPane.setMargin(br, new Insets(5, 5, 5, 5));


            } catch (SQLException | IOException ex) {
                continue;

            }
        }
        return gridPane;
    }

    BorderPane getChosen() {
        return this.chosen;
    }

    void setChosen(BorderPane pn) {
        this.chosen = pn;
    }

    BookingController getBc() {
        return this.bc;
    }

    BusDesignView getChosenBusController() {
        return this.chosenController;
    }

    void setChosenController(BusDesignView vw) {
        this.chosenController = vw;
    }

    ObservableList<Route> getRoutes() {
        return this.route;
    }
}

class Select extends Button {
    boolean state = true;
    BorderPane br;
    DepAndRet dr;
    GridPane bottom;
    BusDesignView controller;

    Select(BorderPane br, DepAndRet dr, GridPane bottom, BusDesignView controller) {
        this.controller = controller;
        this.bottom = bottom;
        this.dr = dr;
        this.br = br;
        this.setId("select");
        this.setOnAction(event -> this.changeState());
    }

    void changeState() {
        if (dr.getChosen() != null) {
            if (dr.getChosen() != br) {
                if (dr.getChosen().getBottom() != null) {
                    Select s = (Select) dr.getChosen().lookup("#select");
                    if (s != null)
                        s.changeState();
                }
            }
        }
        if (state) {
            br.setBottom(null);
            this.setText("SEÇ");
            dr.setChosen(null);
            dr.setChosenController(null);
            state = false;
        } else {
            br.setBottom(bottom); //Burası düzeltilecek

            this.setText("KALDIR");
            if (dr.getChosen() != null)
                dr.getChosen().setBottom(null);
            dr.setChosen(br);
            dr.setChosenController(controller);
            state = true;
        }
    }
}
