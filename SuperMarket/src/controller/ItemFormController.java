package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import db.DBConnection;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import view.tm.ItemTM;

import java.io.IOException;
import java.net.URL;
import java.sql.*;

public class ItemFormController {
    public JFXTextField txtCode;
    public JFXTextField txtDescription;
    public JFXTextField txtPackSize;
    public JFXTextField txtQtyOnHand;
    public JFXTextField txtUnitPrice;
    public TableView<ItemTM> tblItem;
    public TableColumn colCode;
    public TableColumn colDescription;
    public TableColumn colPackSize;
    public TableColumn colQtyOnHand;
    public TableColumn colUnitPrice;
    public JFXButton btnSaveItem;
    public JFXButton btnDeleteItem;
    public AnchorPane root;
    public JFXButton btnAddNewItem;

    public void initialize() {
        colCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colPackSize.setCellValueFactory(new PropertyValueFactory<>("packSize"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colQtyOnHand.setCellValueFactory(new PropertyValueFactory<>("qtyOnHand"));

        initUI();

        tblItem.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            btnDeleteItem.setDisable(newValue == null);
            btnSaveItem.setText(newValue != null ? "Update" : "Save");
            btnSaveItem.setDisable(newValue == null);

            if (newValue != null) {
                txtCode.setText(newValue.getItemCode());
                txtDescription.setText(newValue.getDescription());
                txtPackSize.setText(newValue.getPackSize());
                txtUnitPrice.setText(String.valueOf(newValue.getUnitPrice()));
                txtQtyOnHand.setText(String.valueOf(newValue.getQtyOnHand()));

                txtCode.setDisable(false);
                txtDescription.setDisable(false);
                txtPackSize.setDisable(false);
                txtUnitPrice.setDisable(false);
                txtQtyOnHand.setDisable(false);
            }
        });

        txtUnitPrice.setOnAction(event -> btnSaveItem.fire());
        loadAllItems();
    }

    private void loadAllItems() {
        tblItem.getItems().clear();
        /*Get all items*/
        try {
            Connection connection = DBConnection.getDbConnection().getConnection();
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SELECT * FROM Item");

            while (rst.next()) {
                tblItem.getItems().add(new ItemTM(rst.getString("itemCode"), rst.getString("description"), rst.getString("packSize"), rst.getDouble("unitPrice"), rst.getInt("qtyOnHand")));
            }

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        } catch (ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }


    }

    private void initUI() {
        txtCode.clear();
        txtDescription.clear();
        txtPackSize.clear();
        txtUnitPrice.clear();
        txtQtyOnHand.clear();
        txtCode.setDisable(true);
        txtDescription.setDisable(true);
        txtPackSize.setDisable(true);
        txtUnitPrice.setDisable(true);
        txtQtyOnHand.setDisable(true);
        txtCode.setEditable(false);
        btnSaveItem.setDisable(true);
        btnDeleteItem.setDisable(true);
    }

    public void backToHomeOnAction(ActionEvent actionEvent) throws IOException {
        URL resource = this.getClass().getResource("/view/AdministratorDashBoard-Form.fxml");
        Parent root = FXMLLoader.load(resource);
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) (this.root.getScene().getWindow());
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        Platform.runLater(() -> primaryStage.sizeToScene());
    }

    public void addNewItemOnAction(ActionEvent actionEvent) {
        txtCode.setDisable(false);
        txtDescription.setDisable(false);
        txtPackSize.setDisable(false);
        txtUnitPrice.setDisable(false);
        txtQtyOnHand.setDisable(false);
        txtCode.clear();
        txtCode.setText(generateNewId());
        txtDescription.clear();
        txtPackSize.clear();
        txtUnitPrice.clear();
        txtQtyOnHand.clear();
        txtDescription.requestFocus();
        btnSaveItem.setDisable(false);
        btnSaveItem.setText("Save");
        tblItem.getSelectionModel().clearSelection();
    }

    public void saveItemOnAction(ActionEvent actionEvent) {
        String itemCode = txtCode.getText();
        String description = txtDescription.getText();
        String packSize = txtPackSize.getText();

        if (!description.matches("[A-Za-z0-9 ]+")) {
            new Alert(Alert.AlertType.ERROR, "Invalid description").show();
            txtDescription.requestFocus();
            return;
        } else if (!txtPackSize.getText().matches("[0-9A-Za-z ]+")) {
            new Alert(Alert.AlertType.ERROR, "Invalid pack size").show();
            txtUnitPrice.requestFocus();
            return;
        } else if (!txtUnitPrice.getText().matches("^[0-9]+[.]?[0-9]*$")) {
            new Alert(Alert.AlertType.ERROR, "Invalid unit price").show();
            txtUnitPrice.requestFocus();
            return;
        } else if (!txtQtyOnHand.getText().matches("^\\d+$")) {
            new Alert(Alert.AlertType.ERROR, "Invalid qty on hand").show();
            txtQtyOnHand.requestFocus();
            return;
        }

        int qtyOnHand = Integer.parseInt(txtQtyOnHand.getText());
        Double unitPrice = new Double(txtUnitPrice.getText());


        if (btnSaveItem.getText().equalsIgnoreCase("save")) {
            try {
                if (existItem(itemCode)) {
                    new Alert(Alert.AlertType.ERROR, itemCode + " already exists").show();
                }
                //Save Item
                Connection connection = DBConnection.getDbConnection().getConnection();
                PreparedStatement pstm = connection.prepareStatement("INSERT INTO Item (itemCode, description, packSize, unitPrice, qtyOnHand) VALUES(?,?,?,?,?)");
                pstm.setString(1, itemCode);
                pstm.setString(2, description);
                pstm.setString(3, packSize);
                pstm.setDouble(4, unitPrice);
                pstm.setInt(5, qtyOnHand);
                pstm.executeUpdate();

                tblItem.getItems().add(new ItemTM(itemCode, description, packSize, unitPrice, qtyOnHand));

            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            try {

                if (!existItem(itemCode)) {
                    new Alert(Alert.AlertType.ERROR, "There is no such item associated with the id " + itemCode).show();
                }
                /*Update Item*/
                Connection connection = DBConnection.getDbConnection().getConnection();
                PreparedStatement pstm = connection.prepareStatement("UPDATE Item SET description=?, packSize=?, unitPrice=?, qtyOnHand=? WHERE itemCode=?");
                pstm.setString(1, description);
                pstm.setString(2, packSize);
                pstm.setDouble(3, unitPrice);
                pstm.setInt(4, qtyOnHand);
                pstm.setString(5, itemCode);
                pstm.executeUpdate();

                ItemTM selectedItem = tblItem.getSelectionModel().getSelectedItem();
                selectedItem.setDescription(description);
                selectedItem.setPackSize(packSize);
                selectedItem.setUnitPrice(unitPrice);
                selectedItem.setQtyOnHand(qtyOnHand);
                tblItem.refresh();
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        btnAddNewItem.fire();
    }

    public void deleteItemOnAction(ActionEvent actionEvent) {
        /*Delete Item*/
        String itemCode = tblItem.getSelectionModel().getSelectedItem().getItemCode();
        try {
            if (!existItem(itemCode)) {
                new Alert(Alert.AlertType.ERROR, "There is no such item associated with the id " + itemCode).show();
            }

            Connection connection = DBConnection.getDbConnection().getConnection();
            PreparedStatement pstm = connection.prepareStatement("DELETE FROM Item WHERE itemCode=?");
            pstm.setString(1, itemCode);
            pstm.executeUpdate();

            tblItem.getItems().remove(tblItem.getSelectionModel().getSelectedItem());
            tblItem.getSelectionModel().clearSelection();
            initUI();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to delete the item " + itemCode).show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private String generateNewId() {
        try {
            Connection connection = DBConnection.getDbConnection().getConnection();
            ResultSet rst = connection.createStatement().executeQuery("SELECT itemCode FROM Item ORDER BY itemCode DESC LIMIT 1;");
            if(rst.next()) {
                String id = rst.getString("itemCode");
                int newItemId = Integer.parseInt(id.replace("I", "")) + 1;
                return String.format("I%03d", newItemId);
            }else{
                return "I001";
            }

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return "I001";
    }

    private boolean existItem(String itemCode) throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getDbConnection().getConnection();
        PreparedStatement pstm = connection.prepareStatement("SELECT itemCode FROM Item WHERE itemCode=?");
        pstm.setString(1, itemCode);
        return pstm.executeQuery().next();
    }

}
