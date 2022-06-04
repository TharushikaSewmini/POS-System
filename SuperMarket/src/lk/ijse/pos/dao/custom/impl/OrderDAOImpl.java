package lk.ijse.pos.dao.custom.impl;

import lk.ijse.pos.dao.SQLUtil;
import lk.ijse.pos.dao.custom.OrderDAO;
import lk.ijse.pos.entity.Orders;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class OrderDAOImpl implements OrderDAO {
    @Override
    public ArrayList<Orders> getAll() throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public boolean save(Orders entity) throws SQLException, ClassNotFoundException {
        return SQLUtil.executeUpdate("INSERT INTO Orders (orderID, orderDate, custID) VALUES (?,?,?)", entity.getOrderID(),entity.getOrderDate(),entity.getCustID());
    }

    @Override
    public boolean update(Orders entity) throws SQLException, ClassNotFoundException {
        return SQLUtil.executeUpdate("UPDATE Orders SET orderDate=?, custID=? WHERE orderID=?", entity.getOrderDate(),entity.getCustID(),entity.getOrderID());
    }

    @Override
    public Orders search(String orderID) throws SQLException, ClassNotFoundException {
        /*ResultSet rst = SQLUtil.executeQuery("SELECT * FROM Orders WHERE orderID=?",orderID);
        if (rst.next()) {
            return new Orders(rst.getString(1),rst.getString(2));
        }*/
        return null;
    }

    @Override
    public boolean exist(String orderID) throws SQLException, ClassNotFoundException {
        return SQLUtil.executeQuery("SELECT orderID FROM Orders WHERE orderID=?",orderID).next();
    }

    @Override
    public boolean delete(String s) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public String generateNewID() throws SQLException, ClassNotFoundException {
        ResultSet rst = SQLUtil.executeQuery("SELECT orderID FROM Orders ORDER BY orderID DESC LIMIT 1;");
        return rst.next() ? String.format("O%03d", (Integer.parseInt(rst.getString("orderID").replace("O", "")) + 1) ): "O001";
    }

    @Override
    public ArrayList<Orders> getOrderIDFromCustID(String custID) throws SQLException, ClassNotFoundException {
        ResultSet rst = SQLUtil.executeQuery("SELECT orderID FROM Orders WHERE custID=?",custID);
        ArrayList<Orders> allOrderIDs = new ArrayList<>();
        while (rst.next()) {
            allOrderIDs.add(new Orders(rst.getString(1)));
        }
        return allOrderIDs;
    }
}
