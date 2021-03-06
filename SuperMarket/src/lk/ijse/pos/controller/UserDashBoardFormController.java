package lk.ijse.pos.controller;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;

public class UserDashBoardFormController {
    public ImageView imgPlaceOrder;
    public ImageView imgManageOrder;
    public Label lblMenu;
    public Label lblDate;
    public Label lblTime;
    public AnchorPane root;
    public AnchorPane contextRoot;
    public Label lblDescription;

    @FXML
    public void playMouseExitAnimation(MouseEvent mouseEvent) {
        if (mouseEvent.getSource() instanceof ImageView) {
            ImageView icon = (ImageView) mouseEvent.getSource();
            ScaleTransition scaleT = new ScaleTransition(Duration.millis(200), icon);
            scaleT.setToX(1);
            scaleT.setToY(1);
            scaleT.play();

            icon.setEffect(null);
            lblMenu.setText("Welcome");
            lblDescription.setText("Please select one of above main operations to proceed");
        }
    }

    @FXML
    public void playMouseEnterAnimation(MouseEvent mouseEvent) {
            if (mouseEvent.getSource() instanceof ImageView) {
                ImageView icon = (ImageView) mouseEvent.getSource();

                switch (icon.getId()) {
                    case "imgPlaceOrder":
                        lblMenu.setText("Place Orders");
                        lblDescription.setText("Click to add, edit, delete, search or view customer and place a new order");
                        break;
                    case "imgManageOrder":
                        lblMenu.setText("Manage Orders");
                        lblDescription.setText("Click here if you want to edit order");
                        break;
                }

                ScaleTransition scaleT = new ScaleTransition(Duration.millis(200), icon);
                scaleT.setToX(1.2);
                scaleT.setToY(1.2);
                scaleT.play();

                DropShadow glow = new DropShadow();
                glow.setColor(Color.CORNFLOWERBLUE);
                glow.setWidth(20);
                glow.setHeight(20);
                glow.setRadius(20);
                icon.setEffect(glow);
            }
    }

    @FXML
    public void navigate(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getSource() instanceof ImageView) {
            ImageView icon = (ImageView) mouseEvent.getSource();

            Parent root = null;

            switch (icon.getId()) {
                case "imgPlaceOrder":
                    root = FXMLLoader.load(this.getClass().getResource("/lk/ijse/pos/view/PlaceOrder-Form.fxml"));
                    break;
                case "imgManageOrder":
                    root = FXMLLoader.load(this.getClass().getResource("/lk/ijse/pos/view/ManageOrder-Form.fxml"));
                    break;
            }

            if (root != null) {
                contextRoot.getChildren().clear();
                Parent parent = root;
                contextRoot.getChildren().add(parent);

            }
        }
    }

    public void initialize() {
        loadDateAndTime();
    }

    private void loadDateAndTime() {
        //set Date
        lblDate.setText(new SimpleDateFormat("yyy-MMMM-dd").format(new Date()));

        //set Time
        Timeline clock = new Timeline(new KeyFrame(javafx.util.Duration.ZERO, e ->{
            LocalTime currentTime = LocalTime.now();
            lblTime.setText(currentTime.getHour() + ":" +
                    currentTime.getMinute()+ ":" +
                    currentTime.getSecond());
        }),
                new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    @FXML
    private void backToHome(MouseEvent event) throws IOException {
        URL resource = this.getClass().getResource("/lk/ijse/pos/view/UserDashBoard-Form.fxml");
        Parent root = FXMLLoader.load(resource);
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) (this.root.getScene().getWindow());
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        Platform.runLater(() -> primaryStage.sizeToScene());
    }

    @FXML
    public void logout(MouseEvent mouseEvent) throws IOException {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/lk/ijse/pos/view/Login-Form.fxml"))));
        stage.centerOnScreen();
    }

}
