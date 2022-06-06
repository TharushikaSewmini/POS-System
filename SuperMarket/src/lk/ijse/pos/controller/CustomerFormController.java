package lk.ijse.pos.controller;

import lk.ijse.pos.bo.BOFactory;
import lk.ijse.pos.bo.custom.CustomerBO;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import lk.ijse.pos.dto.CustomerDTO;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import lk.ijse.pos.view.tm.CustomerTM;

import java.sql.*;
import java.util.ArrayList;

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
    public AnchorPane root;
    public JFXButton btnAddNewCustomer;

    // Property Injection(DI)
    private final CustomerBO customerBO = (CustomerBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.CUSTOMER);

    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("custID"));
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
            ArrayList<CustomerDTO> allCustomers = customerBO.getAllCustomers();

            for (CustomerDTO customer : allCustomers) {
                tblCustomer.getItems().add(new CustomerTM(customer.getCustID(), customer.getCustTitle(), customer.getCustName(), customer.getCustAddress(), customer.getCity(), customer.getProvince(), customer.getPostalCode()));
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

    public void addNewCustomerOnAction(ActionEvent actionEvent) {
        txtCustomerId.setDisable(false);
        txtCustomerTitle.setDisable(false);
        txtCustomerName.setDisable(false);
        txtCustomerAddress.setDisable(false);
        txtCity.setDisable(false);
        txtProvince.setDisable(false);
        txtPostalCode.setDisable(false);
        txtCustomerId.clear();
        txtCustomerId.setText(generateNewId());
        txtCustomerTitle.clear();
        txtCustomerName.clear();
        txtCustomerAddress.clear();
        txtCity.clear();
        txtProvince.clear();
        txtPostalCode.clear();
        txtCustomerTitle.requestFocus();
        btnSaveCustomer.setDisable(false);
        btnSaveCustomer.setText("Save");
        tblCustomer.getSelectionModel().clearSelection();
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
        } else if (!custAddress.matches("[A-Za-z ]+")) {
            new Alert(Alert.AlertType.ERROR, "Address should be at least 3 characters long").show();
            txtCustomerAddress.requestFocus();
            return;
        } else if (!city.matches("[A-Za-z ]+")) {
            new Alert(Alert.AlertType.ERROR, "City should be at least 3 characters long").show();
            txtCity.requestFocus();
            return;
        } else if (!province.matches("[A-Za-z ]+")) {
            new Alert(Alert.AlertType.ERROR, "Province should be at least 3 characters long").show();
            txtProvince.requestFocus();
            return;
        } else if (!postalCode.matches("[A-Za-z ]+")) {
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
                customerBO.saveCustomer(new CustomerDTO(custID, custTitle, custName, custAddress, city, province, postalCode));

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
                customerBO.updateCustomer(new CustomerDTO(custID, custTitle, custName, custAddress, city, province, postalCode));

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

        btnAddNewCustomer.fire();
    }

    public void deleteCustomerOnAction(ActionEvent actionEvent) {
        /*Delete Customer*/
        String custID = tblCustomer.getSelectionModel().getSelectedItem().getCustID();
        try {
            if (!existCustomer(custID)) {
                new Alert(Alert.AlertType.ERROR, "There is no such customer associated with the id " + custID).show();
            }

            customerBO.deleteCustomer(custID);

            tblCustomer.getItems().remove(tblCustomer.getSelectionModel().getSelectedItem());
            tblCustomer.getSelectionModel().clearSelection();
            initUI();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to delete the customer " + custID).show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean existCustomer(String custID) throws SQLException, ClassNotFoundException {
        return customerBO.customerExist(custID);
    }

    private String generateNewId() {
        try {
            return customerBO.generateNewCustomerId();

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to generate a new id " + e.getMessage()).show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return "C001";
    }

}
