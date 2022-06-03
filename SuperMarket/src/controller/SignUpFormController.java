package controller;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
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

    public void loadUi(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getSource() instanceof Button) {
            Button button = (Button) mouseEvent.getSource();

            Parent context2 = null;

            switch (button.getId()) {
                case "btnLogin":
                    context2 = FXMLLoader.load(this.getClass().getResource("/view/Login-Form.fxml"));
                    break;
                case "btnCreateAccount":
                    context2 = FXMLLoader.load(this.getClass().getResource("/view/UserDashBoard-Form.fxml"));
                    break;
            }

            if (context2 != null) {
                Stage primaryStage = (Stage) this.context2.getScene().getWindow();
                primaryStage.setScene(new Scene(context2));
                primaryStage.centerOnScreen();

            }
        }
    }
}
