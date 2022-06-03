package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import db.DBConnection;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.ItemDTO;
import model.OrderDTO;
import model.OrderDetailDTO;
import view.tm.CustomTM;

import java.sql.*;
import java.time.LocalDate;
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

    ObservableList<CustomTM> tmList = FXCollections.observableArrayList();

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
                    /*Search Customer*/
                    Connection connection = DBConnection.getDbConnection().getConnection();
                    try {
                        if (!existOrder(txtCustomerId.getText())) {
//                            "There is no such customer associated with the id " + id
                            new Alert(Alert.AlertType.ERROR, "There is no such customer associated with the id " + txtCustomerId.getText()).show();
                        }

                        PreparedStatement pstm = connection.prepareStatement("SELECT orderID FROM Orders WHERE custID=?");
                        pstm.setString(1, txtCustomerId.getText());
                        ResultSet rst = pstm.executeQuery();
                        while (rst.next()) {
                            OrderDTO order = new OrderDTO(rst.getString("orderID"), txtCustomerId.getText());

                            lstOrderIds.getItems().addAll(order.getOrderID());
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
                    /*Search Customer*/
                    Connection connection = DBConnection.getDbConnection().getConnection();
                    try {

                        PreparedStatement pstm = connection.prepareStatement("SELECT Item.itemCode,Item.description,Item.unitPrice,OrderDetail.orderQty FROM ((Item INNER JOIN OrderDetail ON Item.itemCode=OrderDetail.itemCode)INNER JOIN Orders ON OrderDetail.orderId = Orders.orderID) WHERE Orders.orderID=?");
                        pstm.setString(1, newOrderID + "");
                        ResultSet rst = pstm.executeQuery();
                        //CustomDTO customDTO = new CustomDTO(rst.getString("itemCode"), rst.getString("description"), rst.getDouble("unitPrice"), rst.getInt("qty"));

                        while (rst.next()) {
                            CustomTM tm = new CustomTM(rst.getString("itemCode"), rst.getString("description"), rst.getDouble("unitPrice"), rst.getInt("orderQty"));


                            //lstOrderIds.getItems().addAll(customDTO.getOrderId());
                            //tblOrderDetails.getItems(customDTO);
                            tmList.add(tm);
                            tblOrderDetails.setItems(tmList);
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

        Connection connection = DBConnection.getDbConnection().getConnection();
        PreparedStatement pstm = connection.prepareStatement("SELECT orderID FROM Orders WHERE custID=?");
        pstm.setString(1, orderID);
        return pstm.executeQuery().next();
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
        Connection connection = null;
        try {
            connection = DBConnection.getDbConnection().getConnection();
            PreparedStatement pstm = connection.prepareStatement("SELECT orderID FROM Orders WHERE orderID=?");
            pstm.setString(1, orderId);

            if (pstm.executeQuery().next()) {
            }

            connection.setAutoCommit(false);
            pstm = connection.prepareStatement("UPDATE Orders SET orderDate=?, custID=? WHERE orderID=?");
            pstm.setDate(1, Date.valueOf(orderDate));
            pstm.setString(2, customerId);
            pstm.setString(3, orderId);

            if(!(pstm.executeUpdate() > 0)) {
                connection.rollback();
                connection.setAutoCommit(true);
                return false;
            }

            pstm = connection.prepareStatement("UPDATE OrderDetail SET itemCode=?, orderQty=?, discount=? WHERE orderId=?");
            for(OrderDetailDTO detail : orderDetails) {
                pstm.setString(1, detail.getItemCode());
                pstm.setInt(2, detail.getOrderQty());
                pstm.setDouble(3, detail.getDiscount());
                pstm.setString(4, orderId);

                if(!(pstm.executeUpdate() > 0)) {
                    connection.rollback();
                    connection.setAutoCommit(true);
                    return false;
                }

                ItemDTO itemDTO = findItem(detail.getItemCode());
                itemDTO.setQtyOnHand(itemDTO.getQtyOnHand() - detail.getOrderQty());

                PreparedStatement prepareStatement = connection.prepareStatement("UPDATE Item SET description=?, packSize=?, unitPrice=?, qtyOnHand=? WHERE itemCode=?");
                prepareStatement.setString(1, itemDTO.getDescription());
                prepareStatement.setString (2, itemDTO.getPackSize());
                prepareStatement.setDouble(3, itemDTO.getUnitPrice());
                prepareStatement.setInt(4, itemDTO.getQtyOnHand());
                prepareStatement.setString(5, itemDTO.getItemCode());

                if(!(prepareStatement.executeUpdate() > 0)) {
                    connection.rollback();
                    connection.setAutoCommit(true);
                    return false;
                }
            }

            /*Delete Order*/
            String orderID = tblOrderDetails.getSelectionModel().getSelectedItem().getItemCode();

            if (!existOrder(orderID)) {
                new Alert(Alert.AlertType.ERROR, "There is no such item associated with the id " + orderID).show();
            }

            PreparedStatement stm = connection.prepareStatement("DELETE FROM Orders WHERE orderID=?");
            stm.setString(1, orderID);
            stm.executeUpdate();

            stm = connection.prepareStatement("DELETE FROM OrderDetail WHERE orderId=?");
            stm.setString(1, orderId);
            stm.executeUpdate();

            connection.commit();
            connection.setAutoCommit(true);
            return true;

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
    }

}
