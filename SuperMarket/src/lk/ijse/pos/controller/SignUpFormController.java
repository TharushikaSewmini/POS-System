package lk.ijse.pos.controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.pos.util.ValidationUtil;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

public class SignUpFormController {
    public TextField txtFullName;
    public TextField txtEmail;
    public TextField txtUserName;
    public PasswordField pwdPassword;
    public JFXButton btnCreateAccount;
    public JFXButton btnLogin;
    public AnchorPane context2;

    LinkedHashMap<TextField, Pattern> map = new LinkedHashMap<>();

    public void initialize() {

        //add pattern and text to the map
        //Create a pattern and compile it to use
        Pattern fullNamePattern = Pattern.compile("^[A-Z][A-z ]{3,30}$");
        Pattern emailPattern = Pattern.compile("^[A-z]{3,30}@gmail.com$");
        Pattern userNamePattern = Pattern.compile("^[A-Z][A-z ]{3,15}$");
        Pattern passwordPattern = Pattern.compile("^.*[A-z].*[0-9].*[!@#$%^&*()_]$");

        map.put(txtFullName, fullNamePattern);
        map.put(txtEmail, emailPattern);
        map.put(txtUserName, userNamePattern);
        map.put(pwdPassword, passwordPattern);
    }


    public void textFields_Key_Released(KeyEvent keyEvent) throws IOException {
        ValidationUtil.validate(map);
        //TextField = error
        //boolean // validation ok

        //if the enter key pressed
        if (keyEvent.getCode() == KeyCode.ENTER) {
            Object response =  ValidationUtil.validate(map);
            //if the response is a text field
            //that means there is a error
            if (response instanceof TextField) {
                TextField textField = (TextField) response;
                textField.requestFocus();// if there is a error just focus it
            }
        }
    }

    public void createAccountOnAction(ActionEvent actionEvent) throws IOException {
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
