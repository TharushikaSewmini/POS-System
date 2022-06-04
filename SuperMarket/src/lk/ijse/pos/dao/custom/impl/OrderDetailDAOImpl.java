package lk.ijse.pos.dao.custom.impl;

import lk.ijse.pos.dao.SQLUtil;
import lk.ijse.pos.dao.custom.OrderDetailDAO;
import lk.ijse.pos.entity.OrderDetail;

import java.sql.SQLException;
import java.util.ArrayList;

public class OrderDetailDAOImpl implements OrderDetailDAO {
    @Override
    public ArrayList<OrderDetail> getAll() throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public boolean save(OrderDetail entity) throws SQLException, ClassNotFoundException {
        return SQLUtil.executeUpdate("INSERT INTO OrderDetail (orderId, itemCode, orderQty, discount) VALUES (?,?,?,?)", entity.getOrderId(),entity.getItemCode(),entity.getOrderQty(),entity.getDiscount());
    }

    @Override
    public boolean update(OrderDetail entity) throws SQLException, ClassNotFoundException {
        return SQLUtil.executeUpdate("UPDATE OrderDetail SET itemCode=?, orderQty=?, discount=? WHERE orderId=?", entity.getItemCode(),entity.getOrderQty(),entity.getDiscount(),entity.getOrderId());
    }

    @Override
    public OrderDetail search(String s) throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public boolean exist(String s) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean delete(String s) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public String generateNewID() throws SQLException, ClassNotFoundException {
        return null;
    }
}
