package com.example.satisotomasyonu;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private boolean authButton,roleButton,routeButton;
    private LoginController lc;
    private final ArrayList<Permission> perms =Role.getPermission();;
    private Pane selected;
    @FXML
    private ImageView close ,imgRoutes;
    @FXML
    private MenuButton menu;
    @FXML
    private Pane home,reports;
    @FXML
    private VBox vboxMenu;
    @FXML
    private AnchorPane secondView;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
    public void main(){

        selected=home;
        select(home);

        ImageView menuIcon = new ImageView(this.getClass().getResource("account.png").toString());
        menuIcon.setEffect(imgRoutes.getEffect());
        menuIcon.setFitHeight(25);
        menuIcon.setFitWidth(33);
        menu.setGraphic(menuIcon);
        menu.setStyle("-fx-background-color: rgba(33, 40, 69, 0.5); -fx-background-radius: 10; -fx-mark-color: white");
        //lblName.setText(user.getName());
        addPermissions(perms);
        callView("home");



    }
     void setFirstController(final LoginController lc){
        this.lc=lc;
            main();
    }
    LoginController getLc(){
        return lc;
    }
    private void addPermissions(final ArrayList<Permission> perms){

        for (Permission p : perms){



                if(!authButton){
                    if(p.getPermissionId()==1 ||p.getPermissionId()==4){
                        createButton(getClass().getResource("authorize.png").toString(),"Yetkili Ayarları",this::onAuthSetClick,"auth-setting");
                        authButton=true;
                    }
                }
                else if(!roleButton){
                    if(p.getPermissionId()==2||p.getPermissionId()==3){
                        createButton(getClass().getResource("roles.png").toString(),"Rol Ayarları",this::onRoleSetClick,"role-setting");
                        roleButton=true;
                    }


                }
                else if(!routeButton){
                    if(p.getPermissionId()==5||p.getPermissionId()==6){
                        createButton(getClass().getResource("route-settings.png").toString(),"Sefer Ayarları",this::onRouteSetClick,"route-setting");
                        routeButton=true;
                    }


                }





        }
    }
    private void createButton(String img, String lbl, final EventHandler<? super MouseEvent> value,String id){
        Pane pane = new Pane();
        Label label = new Label();
        Separator sep = new Separator();
        Image image = new Image(img);
        ImageView vw = new ImageView(image);
        vw.setEffect(imgRoutes.getEffect());
        pane.setStyle(reports.getStyle());
        pane.setPrefWidth(reports.getPrefWidth());
        pane.setPrefHeight(reports.getPrefHeight());
        pane.setOnMouseClicked(value);
        pane.getStyleClass().add("menuItem");
        pane.setLayoutX(10);
        label.setText(lbl);
        label.setTextFill(Color.color(1,1,1));
        label.setFont(new Font("Roboto Light",21));
        label.setLayoutX(30);
        label.setLayoutY(17);
        label.setPadding(new Insets(5,5,5,5));
        label.setAlignment(Pos.CENTER_RIGHT);
        pane.getChildren().addAll(label,vw);
        pane.setId(id);
        vw.setFitWidth(30);
        vw.setFitHeight(52);
        vw.setLayoutY(21);
        vw.setPickOnBounds(true);
        vw.setPreserveRatio(true);
        sep.setPrefHeight(13);
        sep.setPrefWidth(200);
        vboxMenu.getChildren().addAll(pane,sep);

    }

    @FXML
    protected void onExitButtonClick(MouseEvent event) {
        new CreateDialogToExit("Çıkmak istediğinize emin misiniz?");
    }
    @FXML
    protected void onMinimizeButtonClick(MouseEvent event) {
        ((Stage) close.getScene().getWindow()).setIconified(true);

    }
    @FXML
    protected void onItemClick(MouseEvent event) {
        Pane pn = ((Pane)event.getSource());
         select(pn);
         if(pn.getId()!=null){
             callView(pn.getId().trim());
         }

    }
    @FXML
    protected void onLogOutClick(ActionEvent event) throws IOException {
        new CreateLogin((Stage) home.getScene().getWindow());

    }
    protected void select(Pane item){
        if(selected!=null)
             selected.setStyle(null);
        item.setStyle("-fx-background-color:#5668b3");
        selected=item;

    }
    protected void onAuthSetClick(MouseEvent event)  {
        select((Pane)event.getSource());

    }
    protected void callView(String id){
        try {

            FXMLLoader ld = new FXMLLoader(getClass().getResource(id+"-view.fxml"));
            AnchorPane newLoadedPane =  ld.load();
            Object hc = ld.getController();
            prepareScene(newLoadedPane);
            if(ld.getController()instanceof HomeController)
                ((HomeController)hc).setMainController(this);
            else if(ld.getController()instanceof AccountSettingController)
                ((AccountSettingController)hc).setMainController(this);
            else if(ld.getController()instanceof RoutesController)
                ((RoutesController)hc).setMainController(this);
            else if(ld.getController()instanceof BookingController)
                ((BookingController)hc).setMainController(this);

            if(selected!=null){
                selected.setStyle(null);
                selected=null;
            }
        }
        catch (IOException e){
            System.out.println(e.getMessage());

        }
    }
    @FXML protected void onSettingsClick(ActionEvent event){
      callView("account-setting");
    }
    private void prepareScene(AnchorPane pane){
        AnchorPane.setTopAnchor(pane, 50.0);
        AnchorPane.setBottomAnchor(pane, 50.0);
        AnchorPane.setRightAnchor(pane, 50.0);
        AnchorPane.setLeftAnchor(pane, 50.0);
        secondView.getChildren().clear();
        secondView.getChildren().add(pane);
    }
    protected void onRoleSetClick(MouseEvent event) {

        select((Pane)event.getSource());

    }
    protected void onRouteSetClick(MouseEvent event) {

        select((Pane)event.getSource());

    }




    }

