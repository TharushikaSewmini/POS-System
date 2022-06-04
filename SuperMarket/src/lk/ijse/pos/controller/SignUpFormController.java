package lk.ijse.pos.controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class SignUpFormController {
    public TextField txtFullName;
    public TextField txtEmail;
    public TextField txtUserName;
    public PasswordField txtPassword;
    public JFXButton btnCreateAccount;
    public JFXButton btnLogin;
    public AnchorPane context2;


    public void createAccountOnAction(ActionEvent actionEvent) throws IOException {
        String userName = txtUserName.getText();
        String password = txtPassword.getText();

        if (!userName.matches("^[A-Z][A-z ]{3,15}$")) {
            new Alert(Alert.AlertType.ERROR, "Invalid user name").show();
            txtUserName.requestFocus();
            return;
        } else if (!password.matches("^.*[A-z].*[0-9].*[!@#$%^&*()_]$")) {
            new Alert(Alert.AlertType.ERROR, "password should be at least 3 characters long").show();
            txtPassword.requestFocus();
            return;
        }
        loadUi("UserDashBoard-Form");
    }

    public void loginOnAction(ActionEvent actionEvent) throws IOException {
        loadUi("Login-Form");
    }

    public void loadUi(String location) throws IOException {
        Stage stage = (Stage) context2.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/lk/ijse/pos/view/"+location+".fxml"))));
        stage.centerOnScreen();
    }
}
