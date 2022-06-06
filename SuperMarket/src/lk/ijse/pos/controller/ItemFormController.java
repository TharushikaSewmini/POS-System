package lk.ijse.pos.controller;

import lk.ijse.pos.bo.BOFactory;
import lk.ijse.pos.bo.custom.ItemBO;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import lk.ijse.pos.dto.ItemDTO;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import lk.ijse.pos.view.tm.ItemTM;

import java.sql.*;
import java.util.ArrayList;

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

    // Property Injection(DI)
    private final ItemBO itemBO = (ItemBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.ITEM);

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
            ArrayList<ItemDTO> allItems = itemBO.getAllItems();
            for (ItemDTO item : allItems) {
                tblItem.getItems().add(new ItemTM(item.getItemCode(), item.getDescription(), item.getPackSize(), item.getUnitPrice(), item.getQtyOnHand()));
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
        double unitPrice = new Double(txtUnitPrice.getText());


        if (btnSaveItem.getText().equalsIgnoreCase("save")) {
            try {
                if (existItem(itemCode)) {
                    new Alert(Alert.AlertType.ERROR, itemCode + " already exists").show();
                }
                //Save Item
                itemBO.saveItem(new ItemDTO(itemCode, description, packSize, unitPrice, qtyOnHand));

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
                itemBO.updateItem(new ItemDTO(itemCode, description, packSize, unitPrice,qtyOnHand));

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

            itemBO.deleteItem(itemCode);

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
            return itemBO.generateNewItemId();

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return "I001";
    }

    private boolean existItem(String itemCode) throws SQLException, ClassNotFoundException {
        return itemBO.itemExist(itemCode);
    }

}
