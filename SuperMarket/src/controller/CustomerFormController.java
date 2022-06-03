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
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import view.tm.CustomerTM;

import java.io.IOException;
import java.net.URL;
import java.sql.*;

public class CustomerFormController {
    public JFXTextField txtCustomerId;
    public JFXTextField txtCustomerTitle;
    public JFXTextField txtCustomerName;
    public JFXTextField txtCustomerAddress;
    public JFXTextField txtCity;
    public JFXTextField txtProvince;
    public JFXTextField txtPostalCode;
    public JFXButton btnSaveCustomer;
    public JFXButton btnDeleteCustomer;
    public TableView<CustomerTM> tblCustomer;
    public TableColumn colId;
    public TableColumn colTitle;
    public TableColumn colName;
    public TableColumn colAddress;
    public TableColumn colCity;
    public TableColumn colProvince;
    public TableColumn colPostalCode;
    public JFXButton btnBackToHome;
    public AnchorPane root;

    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("custId"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("custTitle"));
        colName.setCellValueFactory(new PropertyValueFactory<>("custName"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("custAddress"));
        colCity.setCellValueFactory(new PropertyValueFactory<>("city"));
        colProvince.setCellValueFactory(new PropertyValueFactory<>("province"));
        colPostalCode.setCellValueFactory(new PropertyValueFactory<>("postalCode"));

        initUI();

        tblCustomer.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            btnDeleteCustomer.setDisable(newValue == null);
            btnSaveCustomer.setText(newValue != null ? "Update" : "Save");
            btnSaveCustomer.setDisable(newValue == null);

            if (newValue != null) {
                txtCustomerId.setText(newValue.getCustID());
                txtCustomerTitle.setText(newValue.getCustTitle());
                txtCustomerName.setText(newValue.getCustName());
                txtCustomerAddress.setText(newValue.getCustAddress());
                txtCity.setText(newValue.getCity());
                txtProvince.setText(newValue.getProvince());
                txtPostalCode.setText(newValue.getPostalCode());

                txtCustomerId.setDisable(false);
                txtCustomerTitle.setDisable(false);
                txtCustomerName.setDisable(false);
                txtCustomerAddress.setDisable(false);
                txtCity.setDisable(false);
                txtProvince.setDisable(false);
                txtPostalCode.setDisable(false);
            }
        });

        txtPostalCode.setOnAction(event -> btnSaveCustomer.fire());
        loadAllCustomers();

    }

    private void loadAllCustomers() {
        tblCustomer.getItems().clear();
        /*Get all customers*/
        try {
            Connection connection = DBConnection.getDbConnection().getConnection();
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SELECT * FROM Customer");

            while (rst.next()) {
                tblCustomer.getItems().add(new CustomerTM(rst.getString("custID"), rst.getString("custTitle"), rst.getString("custName"), rst.getString("custAddress"), rst.getString("city"), rst.getString("province"), rst.getString("postalCode")));
            }

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        } catch (ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }


    }

    private void initUI() {
        txtCustomerId.clear();
        txtCustomerTitle.clear();
        txtCustomerName.clear();
        txtCustomerAddress.clear();
        txtCity.clear();
        txtProvince.clear();
        txtPostalCode.clear();
        txtCustomerId.setDisable(true);
        txtCustomerTitle.setDisable(true);
        txtCustomerName.setDisable(true);
        txtCustomerAddress.setDisable(true);
        txtCity.setDisable(true);
        txtProvince.setDisable(true);
        txtPostalCode.setDisable(true);
        txtCustomerId.setEditable(false);
        btnSaveCustomer.setDisable(true);
        btnDeleteCustomer.setDisable(true);
    }

    public void textFieldsKeyReleased(KeyEvent keyEvent) {
    }

    public void saveCustomerOnAction(ActionEvent actionEvent) {
        String custID = txtCustomerId.getText();
        String custTitle = txtCustomerTitle.getText();
        String custName = txtCustomerName.getText();
        String custAddress = txtCustomerAddress.getText();
        String city = txtCity.getText();
        String province = txtProvince.getText();
        String postalCode = txtPostalCode.getText();

        if (!custTitle.matches("[A-Za-z ]+")) {
            new Alert(Alert.AlertType.ERROR, "Invalid name").show();
            txtCustomerTitle.requestFocus();
            return;
        } else if (!custName.matches("[A-Za-z ]+")) {
            new Alert(Alert.AlertType.ERROR, "Name should be at least 3 characters long").show();
            txtCustomerName.requestFocus();
            return;
        } else if (!custAddress.matches(".{3,}")) {
            new Alert(Alert.AlertType.ERROR, "Address should be at least 3 characters long").show();
            txtCustomerAddress.requestFocus();
            return;
        } else if (!city.matches(".{3,}")) {
            new Alert(Alert.AlertType.ERROR, "City should be at least 3 characters long").show();
            txtCity.requestFocus();
            return;
        } else if (!province.matches(".{3,}")) {
            new Alert(Alert.AlertType.ERROR, "Province should be at least 3 characters long").show();
            txtProvince.requestFocus();
            return;
        } else if (!postalCode.matches(".{3,}")) {
            new Alert(Alert.AlertType.ERROR, "PostalCode should be at least 3 characters long").show();
            txtPostalCode.requestFocus();
            return;
        }

        if (btnSaveCustomer.getText().equalsIgnoreCase("save")) {
            try {
                if (existCustomer(custID)) {
                    new Alert(Alert.AlertType.ERROR, custID + " already exists").show();
                }
                //Save Customer
                Connection connection = DBConnection.getDbConnection().getConnection();
                PreparedStatement pstm = connection.prepareStatement("INSERT INTO Customer (custID, custTitle, custName, custAddress, city, province, postalCode) VALUES(?,?,?,?,?,?,?)");
                pstm.setString(1, custID);
                pstm.setString(2, custTitle);
                pstm.setString(3, custName);
                pstm.setString(4, custAddress);
                pstm.setString(5, city);
                pstm.setString(6, province);
                pstm.setString(7, postalCode);
                pstm.executeUpdate();

                tblCustomer.getItems().add(new CustomerTM(custID, custTitle, custName, custAddress, city, province, postalCode));

            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            try {

                if (!existCustomer(custID)) {
                    new Alert(Alert.AlertType.ERROR, "There is no such customer associated with the id " + custID).show();
                }
                /*Update Customer*/
                Connection connection = DBConnection.getDbConnection().getConnection();
                PreparedStatement pstm = connection.prepareStatement("UPDATE Customer SET custTitle=?, custName=?, custAddress=?, city=?, province=?, postalCode=? WHERE custID=?");
                pstm.setString(1, custTitle);
                pstm.setString(2, custName);
                pstm.setString(3, custAddress);
                pstm.setString(4, city);
                pstm.setString(5, province);
                pstm.setString(6, postalCode);
                pstm.setString(7, custID);
                pstm.executeUpdate();

                CustomerTM selectedItem = tblCustomer.getSelectionModel().getSelectedItem();
                selectedItem.setCustTitle(custTitle);
                selectedItem.setCustName(custName);
                selectedItem.setCustAddress(custAddress);
                selectedItem.setCity(city);
                selectedItem.setProvince(province);
                selectedItem.setPostalCode(postalCode);
                tblCustomer.refresh();
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        //btnAddNewItem.fire();
    }

    public void deleteCustomerOnAction(ActionEvent actionEvent) {
        /*Delete Customer*/
        String custID = tblCustomer.getSelectionModel().getSelectedItem().getCustID();
        try {
            if (!existCustomer(custID)) {
                new Alert(Alert.AlertType.ERROR, "There is no such customer associated with the id " + custID).show();
            }

            Connection connection = DBConnection.getDbConnection().getConnection();
            PreparedStatement pstm = connection.prepareStatement("DELETE FROM Customer WHERE custID=?");
            pstm.setString(1, custID);
            pstm.executeUpdate();

            tblCustomer.getItems().remove(tblCustomer.getSelectionModel().getSelectedItem());
            tblCustomer.getSelectionModel().clearSelection();
            initUI();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to delete the customer " + custID).show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean existCustomer(String itemCode) throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getDbConnection().getConnection();
        PreparedStatement pstm = connection.prepareStatement("SELECT custID FROM Customer WHERE custID=?");
        pstm.setString(1, itemCode);
        pstm.executeUpdate();
        return pstm.executeQuery().next();
    }

    public void backToHomeOnAction(ActionEvent actionEvent) throws IOException {
        URL resource = this.getClass().getResource("/view/UserDashBoard-Form.fxml");
        Parent root = FXMLLoader.load(resource);
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) (this.root.getScene().getWindow());
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        Platform.runLater(() -> primaryStage.sizeToScene());
    }
}
