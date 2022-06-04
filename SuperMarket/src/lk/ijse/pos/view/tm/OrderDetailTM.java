package lk.ijse.pos.view.tm;

public class OrderDetailTM {
    private String orderId;
    private String itemCode;
    private int orderQty;
    private double discount;
    private double total;

    public OrderDetailTM() {
    }

    public OrderDetailTM(String orderId, String itemCode, int orderQty, double discount) {
        this.orderId = orderId;
        this.itemCode = itemCode;
        this.orderQty = orderQty;
        this.discount = discount;
    }

    public OrderDetailTM(String itemCode, int orderQty, double discount, double total) {
        this.itemCode = itemCode;
        this.orderQty = orderQty;
        this.discount = discount;
        this.total = total;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
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
        return "OrderDetailTM{" +
                "orderId='" + orderId + '\'' +
                ", itemCode='" + itemCode + '\'' +
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
