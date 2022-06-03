package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import db.DBConnection;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.CustomerDTO;
import model.ItemDTO;
import model.OrderDetailDTO;
import view.tm.OrderDetailTM;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PlaceOrderFormController {
    public Label lblOrderId;
    public Label lblOrderDate;
    public JFXComboBox<String> cmbCustomerId;
    public JFXTextField txtCustomerName;
    public JFXComboBox<String> cmbItemCode;
    public JFXTextField txtDescription;
    public JFXTextField txtPackSize;
    public JFXTextField txtQtyOnHand;
    public JFXTextField txtUnitPrice;
    public JFXTextField txtQty;
    public JFXTextField txtDiscount;
    public JFXButton btnSave;
    public TableView<OrderDetailTM> tblOrder;
    public TableColumn colCode;
    public TableColumn colQty;
    public TableColumn colDiscount;
    public TableColumn colTotalCost;
    public TableColumn colOption;
    public Label lblTotalDiscount;
    public Label lblTotalCost;
    public JFXButton btnCancelOrder;
    public JFXButton btnConfirmOrder;

    String orderId;

    public void initialize() {
        tblOrder.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        tblOrder.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("orderQty"));
        tblOrder.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("discount"));
        tblOrder.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("total"));
        //colOption.setCellValueFactory(new PropertyValueFactory<>("btn"));
        TableColumn<OrderDetailTM, Button> lastCol = (TableColumn<OrderDetailTM, Button>) tblOrder.getColumns().get(4);

        lastCol.setCellValueFactory(param -> {
            Button btnDelete = new Button("Delete");

            btnDelete.setOnAction(event -> {
                tblOrder.getItems().remove(param.getValue());
                tblOrder.getSelectionModel().clearSelection();
                calculateDiscountTotal();
                calculateTotal();
                enableOrDisablePlaceOrderButton();
            });

            return new ReadOnlyObjectWrapper<>(btnDelete);
        });

        orderId = generateNewOrderId();
        lblOrderId.setText(orderId);
        lblOrderDate.setText(LocalDate.now().toString());
        btnConfirmOrder.setDisable(true);
        txtCustomerName.setFocusTraversable(false);
        txtCustomerName.setEditable(false);
        txtDescription.setFocusTraversable(false);
        txtDescription.setEditable(false);
        txtUnitPrice.setFocusTraversable(false);
        txtUnitPrice.setEditable(false);
        txtQtyOnHand.setFocusTraversable(false);
        txtQtyOnHand.setEditable(false);
        txtDiscount.setFocusTraversable(false);
        txtDiscount.setEditable(false);
        txtQty.setOnAction(event -> btnSave.fire());
        txtQty.setEditable(false);
        btnSave.setDisable(true);

        cmbCustomerId.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            enableOrDisablePlaceOrderButton();

            if (newValue != null) {
                try {
                    /*Search Customer*/
                    Connection connection = DBConnection.getDbConnection().getConnection();
                    try {
                        if (!existCustomer(newValue + "")) {
//                            "There is no such customer associated with the id " + id
                            new Alert(Alert.AlertType.ERROR, "There is no such customer associated with the id " + newValue + "").show();
                        }

                        PreparedStatement pstm = connection.prepareStatement("SELECT * FROM Customer WHERE custID=?");
                        pstm.setString(1, newValue + "");
                        ResultSet rst = pstm.executeQuery();
                        rst.next();
                        CustomerDTO customerDTO = new CustomerDTO(newValue + "", rst.getString("custTitle"), rst.getString("custName"), rst.getString("custAddress"), rst.getString("city"), rst.getString("province"), rst.getString("postalCode"));

                        txtCustomerName.setText(customerDTO.getCustName());

                    } catch (SQLException e) {
                        new Alert(Alert.AlertType.ERROR, "Failed to find the customer " + newValue + "" + e).show();
                    }

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                txtCustomerName.clear();
            }
        });


        cmbItemCode.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newItemCode) -> {
            txtDiscount.setEditable(newItemCode != null);
            txtQty.setEditable(newItemCode != null);
            btnSave.setDisable(newItemCode == null);

            if (newItemCode != null) {

                /*Find Item*/
                try {
                    if (!existItem(newItemCode + "")) {
//                        throw new NotFoundException("There is no such item associated with the id " + code);
                    }

                    //search item
                    Connection connection = DBConnection.getDbConnection().getConnection();
                    PreparedStatement pstm = connection.prepareStatement("SELECT * FROM Item WHERE itemCode=?");
                    pstm.setString(1, newItemCode + "");
                    ResultSet rst = pstm.executeQuery();
                    rst.next();
                    ItemDTO item = new ItemDTO(newItemCode + "", rst.getString("description"), rst.getString("packSize"), rst.getDouble("unitPrice"), rst.getInt("qtyOnHand"));

                    txtDescription.setText(item.getDescription());
                    txtPackSize.setText(item.getPackSize());
                    txtUnitPrice.setText(String.valueOf(item.getUnitPrice()));

//                    txtQtyOnHand.setText(tblOrderDetails.getItems().stream().filter(detail-> detail.getCode().equals(item.getCode())).<Integer>map(detail-> item.getQtyOnHand() - detail.getQty()).findFirst().orElse(item.getQtyOnHand()) + "");
                    Optional<OrderDetailTM> optOrderDetail = tblOrder.getItems().stream().filter(detail -> detail.getItemCode().equals(newItemCode)).findFirst();
                    txtQtyOnHand.setText((optOrderDetail.isPresent() ? item.getQtyOnHand() - optOrderDetail.get().getOrderQty() : item.getQtyOnHand()) + "");

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            } else {
                txtDescription.clear();
                txtPackSize.clear();
                txtQtyOnHand.clear();
                txtUnitPrice.clear();
                txtDiscount.clear();
                txtQty.clear();
            }
        });

        tblOrder.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selectedOrderDetail) -> {

            if (selectedOrderDetail != null) {
                cmbItemCode.setDisable(true);
                cmbItemCode.setValue(selectedOrderDetail.getItemCode());
                btnSave.setText("Update");
                txtQtyOnHand.setText(Integer.parseInt(txtQtyOnHand.getText()) + selectedOrderDetail.getOrderQty() + "");
                txtDiscount.setText(selectedOrderDetail.getDiscount() + "");
                txtQty.setText(selectedOrderDetail.getOrderQty() + "");
            } else {
                btnSave.setText("Add to List");
                cmbItemCode.setDisable(false);
                cmbItemCode.getSelectionModel().clearSelection();
                txtQty.clear();
            }

        });

        loadAllCustomerIds();
        loadAllItemCodes();
    }

    private boolean existItem(String itemCode) throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getDbConnection().getConnection();
        PreparedStatement pstm = connection.prepareStatement("SELECT itemCode FROM Item WHERE itemCode=?");
        pstm.setString(1, itemCode);
        return pstm.executeQuery().next();
    }

    boolean existCustomer(String id) throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getDbConnection().getConnection();
        PreparedStatement pstm = connection.prepareStatement("SELECT custID FROM Customer WHERE custID=?");
        pstm.setString(1, id);
        return pstm.executeQuery().next();
    }

    public String generateNewOrderId() {
        try {
            Connection connection = DBConnection.getDbConnection().getConnection();
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SELECT orderID FROM Orders ORDER BY orderID DESC LIMIT 1;");
            return rst.next() ? String.format("O%03d", (Integer.parseInt(rst.getString("orderID").replace("O","")) + 1)) : "O001";

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to generate a new order id").show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return "O001";
    }

    private void loadAllCustomerIds() {
        try {
            Connection connection = DBConnection.getDbConnection().getConnection();
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SELECT * FROM Customer");

            while (rst.next()) {
                cmbCustomerId.getItems().add(rst.getString("custID"));
            }

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to load customer ids").show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadAllItemCodes() {
        try {
            /*Get all items*/
            Connection connection = DBConnection.getDbConnection().getConnection();
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SELECT * FROM Item");

            while (rst.next()) {
                cmbItemCode.getItems().add(rst.getString("itemCode"));
            }

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void newCustomerOnAction(ActionEvent actionEvent) {
    }

    public void addToListOnAction(ActionEvent actionEvent) {
        if (!txtQty.getText().matches("\\d+") || Integer.parseInt(txtQty.getText()) <= 0 ||
                Integer.parseInt(txtQty.getText()) > Integer.parseInt(txtQtyOnHand.getText())) {
            new Alert(Alert.AlertType.ERROR, "Invalid qty").show();
            txtQty.requestFocus();
            txtQty.selectAll();
            return;
        }

        String itemCode = cmbItemCode.getSelectionModel().getSelectedItem();
        //String description = txtDescription.getText();
        double unitPrice = Double.parseDouble(txtUnitPrice.getText());
        int qty = Integer.parseInt(txtQty.getText());
        double discount = Double.parseDouble(txtDiscount.getText());
        double total = (unitPrice * qty)-discount;

        boolean exists = tblOrder.getItems().stream().anyMatch(detail -> detail.getItemCode().equals(itemCode));

        if (exists) {
            OrderDetailTM orderDetailTM = tblOrder.getItems().stream().filter(detail -> detail.getItemCode().equals(itemCode)).findFirst().get();

            if (btnSave.getText().equalsIgnoreCase("Update")) {
                orderDetailTM.setOrderQty(qty);
                orderDetailTM.setDiscount(discount);
                orderDetailTM.setTotal(total);
                tblOrder.getSelectionModel().clearSelection();
            } else {
                orderDetailTM.setOrderQty(orderDetailTM.getOrderQty() + qty);
                total = (orderDetailTM.getOrderQty() * unitPrice)-discount;
                orderDetailTM.setTotal(total);
            }
            tblOrder.refresh();
        } else {
            tblOrder.getItems().add(new OrderDetailTM(itemCode, qty, discount, total));
        }
        cmbItemCode.getSelectionModel().clearSelection();
        cmbItemCode.requestFocus();
        calculateDiscountTotal();
        calculateTotal();
        enableOrDisablePlaceOrderButton();
    }

    private void calculateTotal() {
        double total = 0;

        for (OrderDetailTM detail : tblOrder.getItems()) {
            total +=detail.getTotal();
        }
        lblTotalCost.setText(String.valueOf(total));
    }

    private void calculateDiscountTotal() {
        double discountTotal = 0;

        for (OrderDetailTM detail : tblOrder.getItems()) {
            discountTotal +=detail.getDiscount();
        }
        lblTotalDiscount.setText(String.valueOf(discountTotal));
    }

    private void enableOrDisablePlaceOrderButton() {
        btnConfirmOrder.setDisable(!(cmbCustomerId.getSelectionModel().getSelectedItem() != null && !tblOrder.getItems().isEmpty()));
    }

    public void confirmOrderOnAction(ActionEvent actionEvent) {
        boolean b = saveOrder(orderId, LocalDate.now(), cmbCustomerId.getValue(),
                tblOrder.getItems().stream().map(tm -> new OrderDetailDTO(tm.getOrderId(), tm.getItemCode(), tm.getOrderQty(), tm.getDiscount())).collect(Collectors.toList()));

        if (b) {
            new Alert(Alert.AlertType.INFORMATION, "Order has been placed successfully").show();
        } else {
            new Alert(Alert.AlertType.ERROR, "Order has not been placed successfully").show();
        }

        orderId = generateNewOrderId();
        lblOrderId.setText(orderId);
        cmbCustomerId.getSelectionModel().clearSelection();
        cmbItemCode.getSelectionModel().clearSelection();
        tblOrder.getItems().clear();
        txtQty.clear();
        txtDiscount.clear();
        calculateDiscountTotal();
        calculateTotal();
    }
    public boolean saveOrder(String orderId, LocalDate orderDate, String customerId, List<OrderDetailDTO> orderDetails) {
        Connection connection = null;
        try {
            connection = DBConnection.getDbConnection().getConnection();
            PreparedStatement pstm = connection.prepareStatement("SELECT orderID FROM Orders WHERE orderID=?");
            pstm.setString(1, orderId);

            if (pstm.executeQuery().next()) {
            }

            connection.setAutoCommit(false);
            pstm = connection.prepareStatement("INSERT INTO Orders (orderID, orderDate, custID) VALUES (?,?,?)");
            pstm.setString(1, orderId);
            pstm.setDate(2, Date.valueOf(orderDate));
            pstm.setString(3, customerId);

            if(pstm.executeUpdate() !=1) {
                connection.rollback();
                connection.setAutoCommit(true);
                return false;
            }

            pstm = connection.prepareStatement("INSERT INTO OrderDetail (orderId, itemCode, orderQty, discount) VALUES (?,?,?,?)");
            for(OrderDetailDTO detail : orderDetails) {
                pstm.setString(1, orderId);
                pstm.setString(2, detail.getItemCode());
                pstm.setInt(3, detail.getOrderQty());
                pstm.setDouble(4, detail.getDiscount());

                if(pstm.executeUpdate() !=1) {
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
        cmbCustomerId.getSelectionModel().clearSelection();
        cmbItemCode.getSelectionModel().clearSelection();
        tblOrder.getItems().clear();
        txtQty.clear();
        txtDiscount.clear();
    }

}
