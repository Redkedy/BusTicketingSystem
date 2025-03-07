package com.example.satisotomasyonu;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    private int numErr=0;
    private double xOffset = 0;
    private double yOffset = 0;
    private boolean validation=false;
    private final String  pattern = "^((?=[A-Za-z0-9.])(?![_\\\\-]).)*$";
    private final String patternTwoFactor= "^((?=[0-9])(?![_\\\\-]).)*$";
    private  User user;
    @FXML
    private Label labelWarning;
    @FXML
    private ImageView btn2;
    @FXML
    private Button btnLogin;
    @FXML
    private TextField txtUserName,txtPassword;

    private org.apache.commons.codec.binary.Hex Hex;

    @FXML
    protected void onExitButtonClick(MouseEvent event) {
        new CreateDialogToExit("Çıkmak istediğinize emin misiniz?");
    }
    @FXML
    protected void onMinimizeButtonClick(MouseEvent event) {
     ((Stage) btn2.getScene().getWindow()).setIconified(true);
    }
    @FXML
    protected void onLoginClick(ActionEvent event) throws WrongPassOrNameException{
        try {
            String valData="";
            validation=false;
            String username = txtUserName.getText().trim().toLowerCase();
            String password = txtPassword.getText();
            PBKDF2 pb = new PBKDF2();
            String hashedPass = pb.getHashedPass(password);
            Login login = new Login(username,hashedPass);
            valData = login.tryLogin();

               if (!"-1".equals(valData)) {
                   if(!"0".equals(valData)){
                       AesEncDec data = new AesEncDec();
                       createDialog(data.decrypt(valData, "officialsecret"));
                   }
                   else
                       validation=true;
               }
               else {
                   validation = false;
               }
           if(validation) {
                     user = new User(username,hashedPass);
                     FXMLLoader ld = new FXMLLoader(getClass().getResource("main-view.fxml"));
                     Parent mainMenu =  ld.load();
                   MainController mn = ld.<MainController>getController();
                    Scene dashboard = new Scene(mainMenu);
                    dashboard.setFill(Color.TRANSPARENT);
                    Stage window=(Stage)((Node)event.getSource()).getScene().getWindow();
                    window.setScene(dashboard);
                    window.setX(0);
                    window.setY(0);
                    window.setMaximized(true);
                    mn.setFirstController(this);
                }

            else if (numErr>=3)
                labelWarning.setText("Doğrulama kodu hatalı");
           else
               throw new WrongPassOrNameException("Kullanıcı adı veya şifre yanlış");
        }
        catch (SQLException ex)
        {
            System.out.println(ex.getMessage());
        }
        catch (WrongPassOrNameException ex){
            labelWarning.setText(ex.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DisabledUser disabledUser) {
            labelWarning.setText(disabledUser.getMessage());
        }
    }
    @FXML
    protected void onTextViewClick(MouseEvent event) {
       labelWarning.setText("");
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
            txtUserName.textProperty().addListener(inputControl());
            txtPassword.textProperty().addListener(inputControl());

    }
    User getUser(){
        return this.user;
    }
    private ChangeListener<String> inputControl() {
        ChangeListener<String> listener = ((observable, oldValue, newValue)  ->  {
            try{
                labelWarning.setText("");
                if(observable.toString().contains("txtUserName")){
                    if(txtUserName.getText().contains(" "))
                        txtUserName.setText(txtUserName.getText().trim());

                    if(!txtUserName.getText().matches(pattern))
                    {
                        txtUserName.setText(oldValue);
                        throw new InvalidCharacter("Kullanıcı adı özel karakter içeremez!");

                    }
                }
                btnLogin.setDisable(checkInvalid(txtUserName.getText())||checkEmpty(txtPassword.getText()));
            }
            catch (InvalidCharacter error){
                labelWarning.setText(error.getMessage());
                 }
            }
        );
            return listener;
    }
    private boolean checkInvalid(String txt){
        return (checkEmpty(txt));
    }
    private boolean checkEmpty(String txt){
        return (txt==null|| txt.length()==0);
    }
    private void createDialog(String key) {
        TwoFactor tw= new TwoFactor();
        tw.setSecretKey(key);
        tw.startThread();
        Dialog dialog = new Dialog<>();
        dialog.setHeaderText("Doğrulama kodunu giriniz");
        dialog.setTitle("İki Aşamalı Doğrulama");
        dialog.initStyle(StageStyle.UTILITY);
        ImageView icon =new ImageView(this.getClass().getResource("protection.png").toString());
        icon.setFitHeight(50);
        icon.setFitWidth(50);
        dialog.setGraphic(icon);
        ButtonType typeOk = new ButtonType("Doğrula", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(typeOk);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(50, 50, 50, 50));
        TextField code = new TextField();
        Label error = new Label("");
        grid.add(code, 0, 0);
        grid.add(error,0,1);
        Node btnValidate = dialog.getDialogPane().lookupButton(typeOk);
        btnValidate.setDisable(true);
        code.textProperty().addListener((observable, oldValue, newValue) -> {
            btnValidate.setDisable(newValue.trim().isEmpty());
           if( !code.getText().matches(patternTwoFactor)||code.getText().length()>6)
               code.setText(oldValue);
           error.setText("");
        });
        dialog.getDialogPane().setContent(grid);

        btnValidate.addEventFilter(ActionEvent.ACTION, event -> {
                    if(tw.checkTwoFactor(code.getText()))
                        validation=true;
                    else
                    {
                        if(numErr<3)
                        {
                            numErr++;
                            event.consume();
                            error.setText("Doğrulama kodu hatalı!");
                            error.setTextFill(Color.RED);

                        }
                    }
        });

        dialog.showAndWait();
    }

    public void initialize( Parent root, Stage stage) {


        root.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });


        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
        });
    }
}