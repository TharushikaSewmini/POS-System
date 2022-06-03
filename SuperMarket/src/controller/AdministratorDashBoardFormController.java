package controller;

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

public class AdministratorDashBoardFormController {
    public ImageView imgItem;
    public ImageView imgReport;
    public Label lblMenu;
    public Label lblDate;
    public Label lblTime;
    public AnchorPane root;
    public AnchorPane contextRoot;

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
            //lblDescription.setText("Please select one of above main operations to proceed");
        }
    }

    @FXML
    public void playMouseEnterAnimation(MouseEvent mouseEvent) {
        if (mouseEvent.getSource() instanceof ImageView) {
            ImageView icon = (ImageView) mouseEvent.getSource();

            switch (icon.getId()) {
                case "imgItem":
                    lblMenu.setText("Manage Items");
                    //lblDescription.setText("Click to add, edit, delete, search or view items");
                    break;
                case "imgReport":
                    lblMenu.setText("Manage Reports");
                    //lblDescription.setText("Click to view system reports");
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
                case "imgItem":
                    root = FXMLLoader.load(this.getClass().getResource("/view/Item-Form.fxml"));
                    break;
                case "imgReport":
                    root = FXMLLoader.load(this.getClass().getResource("/view/Report-Form.fxml"));
                    break;
            }

            if (root != null) {
                contextRoot.getChildren().clear();
                Parent parent = root;
                contextRoot.getChildren().add(parent);

                /*Scene subScene = new Scene(root);
                Stage primaryStage = (Stage) this.contextRoot.getScene().getWindow();
                primaryStage.setScene(subScene);
                primaryStage.centerOnScreen();*/

                /*TranslateTransition tt = new TranslateTransition(Duration.millis(350), Parent.getRoot());
                tt.setFromX(-Parent.getWidth());
                tt.setToX(0);
                tt.play();*/

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
        URL resource = this.getClass().getResource("/view/AdministratorDashBoard-Form.fxml");
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
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/Login-Form.fxml"))));
        stage.centerOnScreen();
    }

}
