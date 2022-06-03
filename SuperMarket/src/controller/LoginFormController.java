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

public class LoginFormController {
    public TextField txtUserName;
    public PasswordField txtPassword;
    public JFXButton btnAdministrator;
    public JFXButton btnUser;
    public JFXButton btnSignUp;
    public AnchorPane context1;

    public void loadUi(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getSource() instanceof Button) {
            Button button = (Button) mouseEvent.getSource();

            Parent context1 = null;

            switch (button.getId()) {
                case "btnAdministrator":
                    context1 = FXMLLoader.load(this.getClass().getResource("/view/AdministratorDashBoard-Form.fxml"));
                    break;
                case "btnUser":
                    context1 = FXMLLoader.load(this.getClass().getResource("/view/UserDashBoard-Form.fxml"));
                    break;
                case "btnSignUp":
                    context1 = FXMLLoader.load(this.getClass().getResource("/view/SignUp-Form.fxml"));
                    break;
            }

            if (context1 != null) {
                Stage primaryStage = (Stage) this.context1.getScene().getWindow();
                primaryStage.setScene(new Scene(context1));
                primaryStage.centerOnScreen();

            }
        }
    }
}
