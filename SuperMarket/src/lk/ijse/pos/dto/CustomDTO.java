package lk.ijse.pos.dto;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class CustomDTO {
    private String custID;
    private String custTitle;
    private String custName;
    private String custAddress;
    private String city;
    private String province;
    private String postalCode;

    private String itemCode;
    private String description;
    private String packSize;
    private double unitPrice;
    private int qtyOnHand;

    private String orderID;
    private LocalDate orderDate;
    //private String custID;

    private String orderId;
    //private String itemCode;
    private int orderQty;
    private double discount;

    private double total;
    public CustomDTO() {
    }

    public CustomDTO(String itemCode, String description, double unitPrice, int orderQty) {
        this.itemCode = itemCode;
        this.description = description;
        this.unitPrice = unitPrice;
        this.orderQty = orderQty;
    }

    public CustomDTO(String custID, String custTitle, String custName, String custAddress, String city, String province, String postalCode, String itemCode, String description, String packSize, double unitPrice, int qtyOnHand, String orderID, LocalDate orderDate, String orderId, int orderQty, double discount) {
        this.custID = custID;
        this.custTitle = custTitle;
        this.custName = custName;
        this.custAddress = custAddress;
        this.city = city;
        this.province = province;
        this.postalCode = postalCode;
        this.itemCode = itemCode;
        this.description = description;
        this.packSize = packSize;
        this.unitPrice = unitPrice;
        this.qtyOnHand = qtyOnHand;
        this.orderID = orderID;
        this.orderDate = orderDate;
        this.orderId = orderId;
        this.orderQty = orderQty;
        this.discount = discount;
    }

    public CustomDTO(int orderQty) {
        this.orderQty = orderQty;
    }

    public CustomDTO(String orderId, LocalDate orderDate, String customerId, List<OrderDetailDTO> orderDetails) {
    }

    public String getCustID() {
        return custID;
    }

    public void setCustID(String custID) {
        this.custID = custID;
    }

    public String getCustTitle() {
        return custTitle;
    }

    public void setCustTitle(String custTitle) {
        this.custTitle = custTitle;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getCustAddress() {
        return custAddress;
    }

    public void setCustAddress(String custAddress) {
        this.custAddress = custAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPackSize() {
        return packSize;
    }

    public void setPackSize(String packSize) {
        this.packSize = packSize;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQtyOnHand() {
        return qtyOnHand;
    }

    public void setQtyOnHand(int qtyOnHand) {
        this.qtyOnHand = qtyOnHand;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getOrderQty() {
        return orderQty;
    }

    public void setOrderQty(int orderQty) {
        this.orderQty = orderQty;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    @Override
    public String toString() {
        return "CustomDTO{" +
                "custID='" + custID + '\'' +
                ", custTitle='" + custTitle + '\'' +
                ", custName='" + custName + '\'' +
                ", custAddress='" + custAddress + '\'' +
                ", city='" + city + '\'' +
                ", province='" + province + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", itemCode='" + itemCode + '\'' +
                ", description='" + description + '\'' +
                ", packSize='" + packSize + '\'' +
                ", unitPrice=" + unitPrice +
                ", qtyOnHand=" + qtyOnHand +
                ", orderID='" + orderID + '\'' +
                ", orderDate=" + orderDate +
                ", orderId='" + orderId + '\'' +
                ", orderQty=" + orderQty +
                ", discount=" + discount +
                '}';
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
