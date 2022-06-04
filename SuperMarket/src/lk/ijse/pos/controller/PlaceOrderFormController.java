package lk.ijse.pos.controller;

import lk.ijse.pos.bo.BOFactory;
import lk.ijse.pos.bo.custom.PlaceOrderBO;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import lk.ijse.pos.db.DBConnection;
import lk.ijse.pos.dto.OrderDTO;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.ijse.pos.dto.CustomerDTO;
import lk.ijse.pos.dto.ItemDTO;
import lk.ijse.pos.dto.OrderDetailDTO;
import javafx.stage.Stage;
import lk.ijse.pos.view.tm.OrderDetailTM;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
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

    // Property Injection(DI)
    private final PlaceOrderBO placeOrderBO = (PlaceOrderBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.PURCHASE_ORDER);
    public TextField txtExchangeCost;
    public JFXTextField txtCash;
    public JFXButton btnPay;

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
        txtCash.setEditable(false);
        txtExchangeCost.setEditable(false);
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

                        CustomerDTO search = placeOrderBO.searchCustomer(newValue + "");
                        txtCustomerName.setText(search.getCustName());

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
                    ItemDTO item = placeOrderBO.searchItem(newItemCode + "");

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
        return placeOrderBO.checkItemIsAvailable(itemCode);
    }

    boolean existCustomer(String id) throws SQLException, ClassNotFoundException {
        return placeOrderBO.checkCustomerIsAvailable(id);
    }

    public String generateNewOrderId() {
        try {
            return placeOrderBO.generateNewOrderID();

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to generate a new order id").show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return "O001";
    }

    private void loadAllCustomerIds() {
        try {
            ArrayList<CustomerDTO> all = placeOrderBO.getAllCustomers();
            for (CustomerDTO customer : all) {
                cmbCustomerId.getItems().add(customer.getCustID());
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
            ArrayList<ItemDTO> all = placeOrderBO.getAllItems();
            for (ItemDTO item : all) {
                cmbItemCode.getItems().add(item.getItemCode());
            }

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void newCustomerOnAction(ActionEvent actionEvent) throws IOException {
        URL resource = getClass().getResource("/lk/ijse/pos/view/Customer-Form.fxml");
        Parent load = FXMLLoader.load(resource);
        Scene scene = new Scene(load);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
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
        txtCash.setEditable(true);

        txtCash.setOnAction(event -> {
            calculateExchangeCost();
        });


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
                tblOrder.getItems().stream().map(tm -> new OrderDetailDTO(orderId, tm.getItemCode(), tm.getOrderQty(), tm.getDiscount())).collect(Collectors.toList()));

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


        //print report
        String customerID = cmbCustomerId.getValue();
        String orderId = lblOrderId.getText();
        double total = Double.parseDouble(lblTotalCost.getText());

        HashMap paramMap = new HashMap();
        paramMap.put("customerID", customerID);// id = report param name // customerID = UI typed value
        paramMap.put("orderID", orderId);
        paramMap.put("total", total);

        // get values from CartTM
        ObservableList<OrderDetailTM> tableRecords = tblOrder.getItems(); // bean collection

        try {
            JasperReport compiledReport = (JasperReport) JRLoader.loadObject(this.getClass().getResource("/lk/ijse/pos/view/reports/CustomerInvoice.jasper"));
            JasperPrint jasperPrint = JasperFillManager.fillReport(compiledReport, paramMap, new JRBeanCollectionDataSource(tableRecords));
            JasperViewer.viewReport(jasperPrint, false);

        } catch (JRException e) {
            e.printStackTrace();
        }

    }
    public boolean saveOrder(String orderId, LocalDate orderDate, String customerId, List<OrderDetailDTO> orderDetails) {
        try {
            return placeOrderBO.purchaseOrder(new OrderDTO(orderId, orderDate, customerId, orderDetails));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return  false;
    }

    public ItemDTO findItem(String itemCode) {
        try {
            return placeOrderBO.searchItem(itemCode);
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

    public void calculateExchangeCost() {
        double totalCost= Double.parseDouble(lblTotalCost.getText());
        double cash = Double.parseDouble(txtCash.getText());
        double exchangeCost = cash-totalCost;
        txtExchangeCost.setText(String.valueOf(exchangeCost));
    }

}
