package lk.ijse.pos.util;

import com.jfoenix.controls.JFXButton;
import javafx.scene.control.TextField;

import java.util.LinkedHashMap;
import java.util.regex.Pattern;

public class ValidationUtil {
        public static Object validate(LinkedHashMap<TextField, Pattern> map) {
            for (TextField key : map.keySet()) {
                Pattern pattern = map.get(key);
                if (!pattern.matcher(key.getText()).matches()){
                    //if the input is not matching
                    addError(key);
                    return key;
                }
                removeError(key);
            }
            return true;
        }

        private static void removeError(TextField txtField) {
            txtField.setStyle("-fx-border-color: green");

        }

        private static void addError(TextField txtField) {
            if (txtField.getText().length() > 0) {
                txtField.setStyle("-fx-border-color: red");
            }
        }
}
