package com.example.satisotomasyonu;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.net.URL;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

public class AccountSettingController implements Initializable {
    private MainController mainController;
    private GridPane passwordItems, cellPhoneItems, twoFactorItems;
    private Integer sec = 90;
    private String valCode, key;
    private String phoneResponse;
    private boolean phoneValidation;
    private SwitchButton sw;
    @FXML
    Label lblUserName, lblPhone, lblTFsuccess;
    @FXML
    private BorderPane panePassword, paneCellPhone, paneTwoFactor;

    @Override
    public void initialize(URL location, ResourceBundle resources) {


    }

    protected void setMainController(MainController mainController) {
        this.mainController = mainController;
        main();
    }

    protected void main() {
        lblUserName.setText(mainController.getLc().getUser().getUsername());
        lblPhone.setText("+" + mainController.getLc().getUser().getCellPhone());
        preparePasswordItems();
        prepareCellPhoneItems();
        sw = new SwitchButton();
        paneTwoFactor.setRight(sw);
        prepareTwoFactorItems(sw);


    }

    @FXML
    private void onChangePass(ActionEvent event) {
        panePassword.setBottom(passwordItems);
        BorderPane.setAlignment(panePassword, Pos.CENTER);
        paneCellPhone.setBottom(null);
        if (paneTwoFactor.getBottom() != null)
            sw.changeState();
        paneTwoFactor.setBottom(null);
    }

    @FXML
    private void onChangePhone(ActionEvent event) {
        paneCellPhone.setBottom(cellPhoneItems);
        BorderPane.setAlignment(paneCellPhone, Pos.CENTER);
        panePassword.setBottom(null);
        if (paneTwoFactor.getBottom() != null)
            sw.changeState();
        paneTwoFactor.setBottom(null);

    }

    private void prepareTwoFactorItems(SwitchButton sw) {
        TwoFactor tf = new TwoFactor();
        EventHandler<Event> click = new EventHandler<Event>() {
            @Override
            public void handle(Event e) {
                if (sw.isState()) {
                    boolean statement = createDialogToSave();
                    if (statement) {
                        sw.changeState();
                        //emin misin diye uyarı verip kapatacak
                        paneTwoFactor.setBottom(null);
                        try {
                            Login.getDb().setValData(mainController.getLc().getUser(), "0");
                        } catch (SQLException ex) {
                            lblTFsuccess.setText("Kaydedilemedi!");
                            lblTFsuccess.setTextFill(Color.RED);
                        }
                    }

                } else {
                    sw.changeState();
                    twoFactorItems = new GridPane();
                    paneTwoFactor.setBottom(twoFactorItems);
                    BorderPane.setAlignment(paneTwoFactor, Pos.CENTER);
                    //karekod olacak karekod yenileme olacak. aşağıda kaydet seçeneği olacak.
                    //generate secret_key
                    Label lblWarn = new Label("yandaki kare kodu Google Authenticator uygulamanızdan okutun!".toUpperCase(Locale.ROOT));
                    lblWarn.getStyleClass().add("labelTwoFactor");
                    ColumnConstraints c1 = new ColumnConstraints();
                    c1.setPercentWidth(60);
                    ColumnConstraints c2 = new ColumnConstraints();
                    c2.setPercentWidth(40);
                    twoFactorItems.getColumnConstraints().addAll(c1, c2);
                    twoFactorItems.setPadding(new Insets(20, 20, 20, 20));
                    ImageView QR = new ImageView();
                    key = tf.generateSecretKey();
                    QR.setImage(tf.getQr(key, lblUserName.getText()));
                    twoFactorItems.addRow(0, lblWarn, QR);
                    twoFactorItems.setVgap(10);
                    GridPane.setHalignment(QR, HPos.RIGHT);
                    Button save = new Button("Kaydet");
                    save.getStyleClass().add("buttonSave");
                    Button cancel = new Button("İptal");
                    Button reGenerate = new Button("Yeni kod oluştur");
                    twoFactorItems.add(reGenerate, 1, 1);
                    twoFactorItems.addRow(2, save, cancel);
                    GridPane.setHalignment(cancel, HPos.RIGHT);
                    GridPane.setHalignment(reGenerate, HPos.RIGHT);
                    cancel.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            paneTwoFactor.setBottom(null);
                            sw.changeState();
                        }
                    });
                    reGenerate.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            key = tf.generateSecretKey();
                            QR.setImage(tf.getQr(key, lblUserName.getText()));
                        }
                    });
                    save.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            Thread t = new Thread(() -> {
                                Platform.runLater(() -> {
                                    try {
                                        AesEncDec enc = new AesEncDec();
                                        Login.getDb().setValData(mainController.getLc().getUser(), enc.encrypt(key, "officialsecret"));
                                        lblTFsuccess.setText("Başarıyla kaydedildi");
                                        lblTFsuccess.setTextFill(Color.valueOf("#0a6f3e"));
                                        paneTwoFactor.setBottom(null);
                                    } catch (SQLException ex) {
                                        lblTFsuccess.setTextFill(Color.RED);
                                        lblTFsuccess.setText("Kaydedilirken bir sorun oluştu.");
                                    }
                                });
                                try {

                                    Thread.sleep(1000);
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                                Platform.runLater(() -> {
                                    lblTFsuccess.setText("");
                                });

                            });
                            t.start();
                        }
                    });

                }
                panePassword.setBottom(null);
                paneCellPhone.setBottom(null);
            }
        };
        sw.setClick(click);
        //preparing

        if (!"0".equals(Login.getDb().getValData())) {
            sw.changeState();
        }
    }

    private void preparePasswordItems() {

        passwordItems = new GridPane();
        Label lblCurrent = new Label("Mevcut şifreniz:");
        Label lblNew = new Label("Yeni şifre:");
        Label lblNewAgain = new Label("Tekrar yeni şifre:");
        Label lblWarning = new Label("");
        lblCurrent.getStyleClass().add("labelItems");
        lblNew.getStyleClass().add("labelItems");
        lblNewAgain.getStyleClass().add("labelItems");
        PasswordField txtCurrentPass = new PasswordField();
        PasswordField txtNewPass = new PasswordField();
        PasswordField txtNewPassAgain = new PasswordField();
        ColumnConstraints c1 = new ColumnConstraints();
        c1.setPercentWidth(60);
        ColumnConstraints c2 = new ColumnConstraints();
        c2.setPercentWidth(40);
        passwordItems.getColumnConstraints().addAll(c1, c2);
        passwordItems.setPadding(new Insets(20, 20, 20, 20));
        passwordItems.add(lblCurrent, 0, 0);
        passwordItems.add(txtCurrentPass, 1, 0);
        passwordItems.add(lblNew, 0, 1);
        passwordItems.add(txtNewPass, 1, 1);
        passwordItems.add(lblNewAgain, 0, 2);
        passwordItems.add(txtNewPassAgain, 1, 2);
        passwordItems.setVgap(10);
        Button cancel = new Button("İptal");
        cancel.setTextFill(Color.WHITE);

        cancel.setOnAction(value -> {
            panePassword.setBottom(null);
            txtCurrentPass.setText("");
            txtNewPass.setText("");
            txtNewPassAgain.setText("");
        });

        Button change = new Button("Şifreyi Değiştir");
        change.setDisable(true);
        change.setTextFill(Color.WHITE);
        lblWarning.setStyle("-fx-font-size:15");
        change.setOnAction(value -> {
            if (!txtNewPass.getText().equals(txtNewPassAgain.getText())) {
                lblWarning.setStyle("-fx-font-size:15;-fx-text-fill:#ff0000");
                lblWarning.setText("Yeni şifre birbiriyle uyuşmuyor");
            } else {
                PBKDF2 pb = new PBKDF2();
                String hashedPass = pb.getHashedPass(txtCurrentPass.getText());
                if (!hashedPass.equals(mainController.getLc().getUser().getPassword())) {
                    lblWarning.setStyle("-fx-font-size:15;-fx-text-fill:#ff0000");
                    lblWarning.setText("Mevcut şifre doğru girilmedi");
                } else {
                    DbManager db = Login.getDb();
                    try {
                        if (db.setPassword(mainController.getLc().getUser(), pb.getHashedPass(txtNewPass.getText()))) {
                            lblWarning.setStyle("-fx-font-size:15;-fx-text-fill:#00ff00");
                            lblWarning.setText("Şifreniz başarıyla güncellendi");
                        }
                    } catch (SQLException e) {
                        lblWarning.setText("Şifreniz güncellenemedi " + e.getMessage());
                    }

                }


            }
        });

        passwordItems.add(cancel, 0, 3);
        passwordItems.add(change, 1, 3);
        passwordItems.add(lblWarning, 1, 4);
        ChangeListener<String> listener = ((observable, oldValue, newValue) -> {
            change.setDisable(txtCurrentPass.getText().equals("") || txtNewPass.getText().equals("") || txtNewPassAgain.getText().equals(""));
            lblWarning.setText("");
        });
        txtCurrentPass.textProperty().addListener(listener);
        txtNewPass.textProperty().addListener(listener);
        txtNewPassAgain.textProperty().addListener(listener);


    }

    private void prepareCellPhoneItems() {
        cellPhoneItems = new GridPane();
        Label lblCellPhone = new Label("Güncellemek istediğiniz telefon numarası:");
        Label lblWarning = new Label("");
        lblCellPhone.getStyleClass().add("labelItems");
        TextField txtNewCellPhone = new TextField();

        txtNewCellPhone.setPromptText("532xxxxxxx");
        ColumnConstraints c1 = new ColumnConstraints();
        c1.setPercentWidth(60);
        ColumnConstraints c2 = new ColumnConstraints();
        c2.setPercentWidth(10);
        ColumnConstraints c3 = new ColumnConstraints();
        c3.setPercentWidth(30);
        c3.setFillWidth(true);
        c3.setHgrow(Priority.ALWAYS);
        cellPhoneItems.getColumnConstraints().addAll(c1, c2, c3);
        cellPhoneItems.setPadding(new Insets(20, 20, 20, 20));
        cellPhoneItems.add(lblCellPhone, 0, 0);
        cellPhoneItems.add(txtNewCellPhone, 2, 0);
        cellPhoneItems.setVgap(10);
        Button cancel = new Button("İptal");
        cancel.setTextFill(Color.WHITE);


        cancel.setOnAction(value -> {
            paneCellPhone.setBottom(null);
            txtNewCellPhone.setText("");
        });


        Button change = new Button("Numarayı güncelle");

        change.setDisable(true);
        change.setTextFill(Color.WHITE);
        change.setOnAction(value -> {
            String number = txtNewCellPhone.getText();
            createDialog(number);
            if (phoneValidation) {

                try {
                    Login.getDb().setPhone(mainController.getLc().getUser(), Long.parseLong("90" + number));
                    lblWarning.setStyle("-fx-font-size:17;-fx-text-fill:#0e7a74");
                    lblWarning.setText("Telefon numaranız başarıyla güncellendi");
                    lblPhone.setText("+90" + number);
                } catch (SQLException e) {
                    lblWarning.setStyle("-fx-font-size:17;-fx-text-fill:#ff0000");
                    if (e.getMessage().contains("UNIQUE"))
                        lblWarning.setText("Telefon numarası başka bir hesapta aktif");
                    else
                        lblWarning.setText("Güncelleme başarısız");

                }

            } else {
                lblWarning.setStyle("-fx-font-size:17;-fx-text-fill:#ff0000");
                if ("".equals(phoneResponse))
                    lblWarning.setText("Güncelleme başarısız");
                else
                    lblWarning.setText(phoneResponse);


            }
        });
        cellPhoneItems.add(cancel, 0, 1);
        cellPhoneItems.add(change, 2, 1);
        cellPhoneItems.add(lblWarning, 0, 2);
        txtNewCellPhone.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() != 0) {
                if (newValue.charAt(0) != '5')
                    txtNewCellPhone.setText("");
                else {
                    if (!newValue.matches("[0-9]*") || newValue.contains(" ") || newValue.length() >= 11)
                        txtNewCellPhone.setText(oldValue);

                }


            }
            change.setDisable(txtNewCellPhone.getText().length() != 10);
        });


    }

    private void doTime(Label lbl, Button again, Button validate) {
        sec = 90;
        Timeline tm = new Timeline();
        tm.setCycleCount(Timeline.INDEFINITE);
        if (tm != null)
            tm.stop();
        KeyFrame frame = new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                sec--;
                lbl.setText(sec.toString());
                if (sec <= 0) {
                    tm.stop();
                    again.setDisable(false);
                    lbl.setText("");
                    validate.setDisable(true);

                }

            }
        });
        tm.getKeyFrames().add(frame);
        tm.playFromStart();


    }

    private boolean createDialogToSave() {
        Dialog dialog = new Dialog<>();
        AtomicBoolean statement = new AtomicBoolean(false);
        dialog.setTitle("Uyarı!");
        dialog.setTitle("");
        dialog.initStyle(StageStyle.UTILITY);
        ButtonType typeOk = new ButtonType("EVET", ButtonBar.ButtonData.OK_DONE);
        ButtonType typeCancel = new ButtonType("HAYIR", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(typeOk, typeCancel);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(50, 50, 50, 50));
        Label error = new Label("İki Faktörlü Doğrulamayı Kapatmak İstediğinizden Emin Misiniz?");
        grid.add(error, 0, 1);
        dialog.getDialogPane().setContent(grid);
        dialog.showAndWait().ifPresent(type -> {
            if (type == typeOk) {
                statement.set(true);
            } else {
                statement.set(false);
            }
        });
        return statement.get();
    }

    private void createDialog(String cellPhone) {
        phoneValidation = false;
        phoneResponse = "";

        Dialog dialog = new Dialog<>();
        dialog.setTitle("Doğrulama Kodu!");
        dialog.initStyle(StageStyle.DECORATED);
        //dialog.setResizable(true);

        ButtonType typeYes = new ButtonType("Evet", ButtonBar.ButtonData.YES);
        ButtonType typeNo = new ButtonType("Hayır", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(typeYes, typeNo);
        Button btnyes = (Button) dialog.getDialogPane().lookupButton(typeYes);
        Button btnno = (Button) dialog.getDialogPane().lookupButton(typeNo);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(50, 50, 50, 50));
        Text number = new Text("+90" + cellPhone);
        number.setStyle("-fx-font-size:17");
        number.setFill(Color.BLUE);
        TextFlow tf = new TextFlow();
        Text warning = new Text(" numarasına doğrulama kodu gönderilecektir. Onaylıyor musunuz?");
        warning.setStyle("-fx-font-size:17");
        tf.getChildren().addAll(number, warning);
        grid.add(tf, 0, 1);
        dialog.getDialogPane().setContent(grid);
        ButtonType typeValidate = new ButtonType("Doğrula", ButtonBar.ButtonData.YES);
        ButtonType typeAgain = new ButtonType("Yeniden Gönder", ButtonBar.ButtonData.LEFT);
        btnyes.addEventFilter(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                valCode = getRandomNumberString();
                String message = mainController.getLc().getUser().getUsername() + " kullanıcısı için, telefon numarası güncelleme aktivasyon kodu: " + valCode;
                API smsApi = new API("90" + cellPhone, message);
                if ("success".equals(smsApi.getResponse())) {

                    Label clock = new Label();
                    dialog.getDialogPane().getButtonTypes().removeAll(typeYes);
                    dialog.getDialogPane().getButtonTypes().addAll(typeValidate, typeAgain);
                    Button validate = (Button) dialog.getDialogPane().lookupButton(typeValidate);
                    Button again = (Button) dialog.getDialogPane().lookupButton(typeAgain);
                    grid.getChildren().clear();
                    tf.getChildren().clear();
                    Text txt = new Text(" numarasına gönderilen 6 haneli doğrulama kodunu giriniz");
                    txt.setStyle("-fx-font-size:17");
                    tf.getChildren().addAll(number, txt);
                    TextField code = new TextField();
                    code.textProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue.length() != 0) {
                            if (!newValue.matches("[0-9]*") || newValue.contains(" ") || newValue.length() >= 7)
                                code.setText(oldValue);
                        }
                        validate.setDisable(code.getText().length() != 6 || clock.getText().equals(""));
                    });
                    validate.setDisable(true);
                    clock.setStyle("-fx-text-fill:#0e7a74;-fx-font-size:17");
                    Label infoLabel = new Label();
                    grid.addRow(0, tf);
                    grid.addRow(1, code, clock);
                    grid.addRow(2, infoLabel);
                    dialog.setWidth(dialog.getWidth() + 100);
                    dialog.setHeight(dialog.getHeight() + 100);
                    btnno.setText("İptal");
                    class createValCode {
                        public createValCode() {
                            infoLabel.setText("");
                            again.setDisable(true);
                            valCode = getRandomNumberString();
                            doTime(clock, again, validate);

                        }


                    }
                    again.addEventFilter(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            new createValCode();
                            event.consume();
                        }
                    });
                    validate.addEventFilter(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            if (code.getText().equals(valCode)) {
                                phoneValidation = true;

                            } else {
                                infoLabel.setStyle("-fx-text-fill:#ff0000;-fx-font-size:15");
                                infoLabel.setText("Doğrulama kodu hatalı veya süresi geçmiş.");
                                event.consume();
                            }


                        }
                    });
                    infoLabel.setText("");
                    again.setDisable(true);
                    doTime(clock, again, validate);
                    event.consume();
                } else {

                    phoneResponse = "Doğrulama kodu gönderilirken bir sorun oluştu";

                }

            }

        });
        dialog.showAndWait();


    }

    public static String getRandomNumberString() {

        Random rnd = new Random();
        int number = rnd.nextInt(10000, 999999);
        return String.format("%06d", number);
    }


}

