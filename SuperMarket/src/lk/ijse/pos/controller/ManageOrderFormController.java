package lk.ijse.pos.controller;

import lk.ijse.pos.bo.BOFactory;
import lk.ijse.pos.bo.custom.PlaceOrderBO;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import lk.ijse.pos.db.DBConnection;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.ijse.pos.dto.CustomDTO;
import lk.ijse.pos.dto.ItemDTO;
import lk.ijse.pos.dto.OrderDTO;
import lk.ijse.pos.dto.OrderDetailDTO;
import lk.ijse.pos.view.tm.CustomTM;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ManageOrderFormController {
    public JFXTextField txtCustomerId;
    public JFXButton btnSearchOrder;
    public ListView<String> lstOrderIds;
    public JFXTextField txtItemCode;
    public JFXTextField txtDescription;
    public JFXTextField txtUnitPrice;
    public JFXTextField txtQty;
    public JFXButton btnUpdate;
    public TableView<CustomTM> tblOrderDetails;
    public TableColumn colCode;
    public TableColumn colDescription;
    public TableColumn colUnitPrice;
    public TableColumn colQty;
    public TableColumn colOption;
    public JFXButton btnConfirmOrderEdits;
    public Label lblTotalDiscount;
    public Label lblTotalCost;
    public JFXButton btnCancelOrder;

    // Property Injection(DI)
    private final PlaceOrderBO placeOrderBO = (PlaceOrderBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.PURCHASE_ORDER);

    public void initialize() {
        tblOrderDetails.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        tblOrderDetails.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("description"));
        tblOrderDetails.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        tblOrderDetails.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("orderQty"));
        TableColumn<CustomTM, Button> lastCol = (TableColumn<CustomTM, Button>) tblOrderDetails.getColumns().get(4);

        lastCol.setCellValueFactory(param -> {
            Button btnDelete = new Button("Delete");

            btnDelete.setOnAction(event -> {
                tblOrderDetails.getItems().remove(param.getValue());
                tblOrderDetails.getSelectionModel().clearSelection();
                calculateDiscountTotal();
                calculateTotal();
                enableOrDisableConfirmOrderEditButton();
            });

            return new ReadOnlyObjectWrapper<>(btnDelete);
        });

        btnConfirmOrderEdits.setDisable(true);
        txtItemCode.setFocusTraversable(false);
        txtItemCode.setEditable(false);
        txtDescription.setFocusTraversable(false);
        txtDescription.setEditable(false);
        txtUnitPrice.setFocusTraversable(false);
        txtUnitPrice.setEditable(false);
        txtQty.setOnAction(event -> btnUpdate.fire());
        txtQty.setEditable(false);
        btnUpdate.setDisable(true);
        tblOrderDetails.getSelectionModel().clearSelection();

        btnSearchOrder.setOnMouseClicked(event -> {
            if (txtCustomerId.getText() != null) {
                try {
                    /*Search Order*/
                    Connection connection = DBConnection.getDbConnection().getConnection();
                    try {
                        ArrayList<OrderDTO> search = placeOrderBO.getAllOrderIdsByCustomerID(txtCustomerId.getText());
                        for (OrderDTO order : search) {
                            lstOrderIds.getItems().add(order.getOrderID());
                        }

                    } catch (SQLException e) {
                        new Alert(Alert.AlertType.ERROR, "Failed to find the customer " + txtCustomerId.getText() + e).show();
                    }

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                lstOrderIds.setItems(null);
            }
        });


        lstOrderIds.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newOrderID) -> {
            if (newOrderID != null) {
                try {
                    Connection connection = DBConnection.getDbConnection().getConnection();
                    try {

                        //load order items
                        ArrayList<CustomDTO> itemDetails = placeOrderBO.getItemByOrderId(newOrderID + "");

                        for (CustomDTO details : itemDetails) {
                            tblOrderDetails.getItems().add(new CustomTM(details.getItemCode(), details.getDescription(), details.getUnitPrice(), details.getOrderQty()));
                        }


                        //Optional<CustomTM> optOrderDetail = tblOrderDetails.getItems().stream().filter(detail -> detail.getOrderID().equals(newOrderID)).findFirst();
                        //txtQty.setText((optOrderDetail.isPresent() ? customDTO.getQtyOnHand() - optOrderDetail.get().getOrderQty() : customDTO.getQtyOnHand()) + "");

                    } catch (SQLException e) {
                        new Alert(Alert.AlertType.ERROR, "Failed to find the customer " + newOrderID + "" + e).show();
                    }

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                tblOrderDetails.refresh();
                txtItemCode.clear();
                txtDescription.clear();
                txtUnitPrice.clear();
                txtQty.clear();
            }
        });


        tblOrderDetails.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selectedOrderDetail) -> {
            lstOrderIds.setDisable(true);
            txtQty.setEditable(selectedOrderDetail != null);

            btnUpdate.setDisable(selectedOrderDetail == null);

            if (selectedOrderDetail != null) {
                txtItemCode.setText(selectedOrderDetail.getItemCode());
                txtDescription.setText(selectedOrderDetail.getDescription());
                txtUnitPrice.setText(String.valueOf(selectedOrderDetail.getUnitPrice()));
                txtQty.setText(String.valueOf(selectedOrderDetail.getOrderQty()));

                lstOrderIds.setDisable(false);
                txtItemCode.setDisable(true);
                txtDescription.setDisable(true);
                txtUnitPrice.setDisable(true);
                txtQty.setDisable(false);
            }
            enableOrDisableConfirmOrderEditButton();
        });

    }

    private boolean existOrder(String orderID) throws SQLException, ClassNotFoundException {
        return placeOrderBO.checkOrderIsAvailable(orderID);
    }

    private void calculateTotal() {
        double total = 0;

        for (CustomTM detail : tblOrderDetails.getItems()) {
            total +=detail.getTotal();
        }
        lblTotalCost.setText(String.valueOf(total));
    }

    private void calculateDiscountTotal() {
        double discountTotal = 0;

        for (CustomTM detail : tblOrderDetails.getItems()) {
            discountTotal +=detail.getDiscount();
        }
        lblTotalDiscount.setText(String.valueOf(discountTotal));
    }

    private void enableOrDisableConfirmOrderEditButton() {
        btnConfirmOrderEdits.setDisable(tblOrderDetails.getItems().isEmpty());
    }

    public void addToListOnAction(ActionEvent actionEvent) {
        if (!txtQty.getText().matches("\\d+") || Integer.parseInt(txtQty.getText()) <= 0) {
            new Alert(Alert.AlertType.ERROR, "Invalid qty").show();
            txtQty.requestFocus();
            txtQty.selectAll();
            return;
        }

        String itemCode = txtItemCode.getText();
        String description = txtDescription.getText();
        double unitPrice = Double.parseDouble(txtUnitPrice.getText());
        int qty = Integer.parseInt(txtQty.getText());
        double discount = 1;
        double total = (unitPrice * qty)-discount;

        boolean exists = tblOrderDetails.getItems().stream().anyMatch(detail -> detail.getItemCode().equals(itemCode));

        if (exists) {
            CustomTM customTM = tblOrderDetails.getItems().stream().filter(detail -> detail.getItemCode().equals(itemCode)).findFirst().get();

            if (btnUpdate.getText().equalsIgnoreCase("Edit Order")) {
                customTM.setOrderQty(qty);
                customTM.setDescription(description);
                customTM.setUnitPrice(unitPrice);
                customTM.setOrderQty(qty);
                tblOrderDetails.getSelectionModel().clearSelection();
            } else {
                customTM.setOrderQty(customTM.getOrderQty() + qty);
                total = (customTM.getOrderQty() * unitPrice)-discount;
                customTM.setTotal(total);
            }
            tblOrderDetails.refresh();
        } else {
            tblOrderDetails.getItems().add(new CustomTM(itemCode, description, unitPrice, qty));
        }
        lstOrderIds.setDisable(false);
        txtItemCode.clear();
        txtDescription.clear();
        txtUnitPrice.clear();
        txtQty.clear();
        calculateDiscountTotal();
        calculateTotal();
        enableOrDisableConfirmOrderEditButton();
    }

    public void confirmOrderEditsOnAction(ActionEvent actionEvent) {
        boolean b = updateOrder(lstOrderIds.getSelectionModel().selectedItemProperty().getValue(), LocalDate.now(), txtCustomerId.getText(),
                tblOrderDetails.getItems().stream().map(tm -> new OrderDetailDTO(tm.getOrderId(), tm.getItemCode(), tm.getOrderQty(), tm.getDiscount())).collect(Collectors.toList()));

        if (b) {
            new Alert(Alert.AlertType.INFORMATION, "Order has been placed successfully").show();
        } else {
            new Alert(Alert.AlertType.ERROR, "Order has not been placed successfully").show();
        }

        txtCustomerId.clear();
        lstOrderIds.getSelectionModel().clearSelection();
        tblOrderDetails.getItems().clear();
        txtItemCode.clear();
        txtDescription.clear();
        txtUnitPrice.clear();
        txtQty.clear();
        calculateDiscountTotal();
        calculateTotal();

    }

    public boolean updateOrder(String orderId, LocalDate orderDate, String customerId, List<OrderDetailDTO> orderDetails) {
        try {
            return placeOrderBO.manageOrder(new OrderDTO(orderId, orderDate, customerId, orderDetails));

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return  false;
    }

    public ItemDTO findItem(String itemCode) {
        try {
            Connection connection = DBConnection.getDbConnection().getConnection();
            PreparedStatement pstm = connection.prepareStatement("SELECT * FROM Item WHERE itemCode=?");
            pstm.setString(1, itemCode);
            ResultSet rst = pstm.executeQuery();
            rst.next();

            return new ItemDTO(itemCode, rst.getString("description"), rst.getString("packSize"), rst.getDouble("unitPrice"), rst.getInt("qtyOnHand"));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find the Item " + itemCode, e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void cancelOrderOnAction(ActionEvent actionEvent) {
        txtCustomerId.clear();
        lstOrderIds.getSelectionModel().clearSelection();
        tblOrderDetails.getItems().clear();
        txtItemCode.clear();
        txtDescription.clear();
        txtUnitPrice.clear();
        txtQty.clear();
        calculateDiscountTotal();
        calculateTotal();
    }

}
