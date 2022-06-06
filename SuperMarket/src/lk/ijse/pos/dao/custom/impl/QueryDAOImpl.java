package lk.ijse.pos.dao.custom.impl;

import lk.ijse.pos.dao.SQLUtil;
import lk.ijse.pos.dao.custom.QueryDAO;
import lk.ijse.pos.entity.CustomEntity;
import lk.ijse.pos.entity.Orders;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class QueryDAOImpl implements QueryDAO {
    @Override
    public ArrayList<CustomEntity> getItemByOrderId(String orderID) throws SQLException, ClassNotFoundException {
        ResultSet rst = SQLUtil.executeQuery("SELECT Item.itemCode,Item.description,Item.unitPrice,OrderDetail.orderQty FROM ((Item INNER JOIN OrderDetail ON Item.itemCode=OrderDetail.itemCode)INNER JOIN Orders ON OrderDetail.orderId = Orders.orderID) WHERE Orders.orderID=?", orderID);
        ArrayList<CustomEntity> itemDetails = new ArrayList<>();
        while (rst.next()) {
            itemDetails.add(new CustomEntity(rst.getString(1), rst.getString(2), rst.getDouble(3), rst.getInt(4)));
        }
        return itemDetails;
    }

    @Override
    public boolean update(CustomEntity entity) throws SQLException, ClassNotFoundException {
        return SQLUtil.executeUpdate("UPDATE Orders INNER JOIN OrderDetail ON Orders.orderID= OrderDetail.orderId SET Orders.orderDate=?, OrderDetail.orderQty=? WHERE orderID=?", entity.getOrderDate(),entity.getCustID(),entity.getOrderID());
    }
}
