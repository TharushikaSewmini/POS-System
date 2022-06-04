package lk.ijse.pos.view.tm;

import java.time.LocalDate;
import java.util.Date;

public class OrderTM {
    private String orderID;
    private LocalDate orderDate;
    private String custID;
    //private double total;

    public OrderTM() {
    }

    public OrderTM(String orderID, LocalDate orderDate, String custID) {
        this.orderID = orderID;
        this.orderDate = orderDate;
        this.custID = custID;
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

    public String getCustID() {
        return custID;
    }

    public void setCustID(String custID) {
        this.custID = custID;
    }

    @Override
    public String toString() {
        return "OrderTM{" +
                "orderID='" + orderID + '\'' +
                ", orderDate='" + orderDate + '\'' +
                ", custID='" + custID + '\'' +
                '}';
    }

    /*public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }*/
}
