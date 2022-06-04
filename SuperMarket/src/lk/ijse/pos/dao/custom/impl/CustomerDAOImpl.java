package lk.ijse.pos.dao.custom.impl;

import lk.ijse.pos.dao.SQLUtil;
import lk.ijse.pos.dao.custom.CustomerDAO;
import lk.ijse.pos.entity.Customer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CustomerDAOImpl implements CustomerDAO {
    @Override
    public ArrayList<Customer> getAll() throws SQLException, ClassNotFoundException {
        ResultSet rst = SQLUtil.executeQuery("SELECT * FROM Customer");
        ArrayList<Customer> allCustomers = new ArrayList<>();
        while (rst.next()) {
            allCustomers.add(new Customer(rst.getString(1), rst.getString(2), rst.getString(3), rst.getString(4), rst.getString(5), rst.getString(6), rst.getString(7)));
        }
        return allCustomers;
    }

    @Override
    public boolean save(Customer entity) throws SQLException, ClassNotFoundException {
        return SQLUtil.executeUpdate("INSERT INTO Customer (custID, custTitle, custName, custAddress, city, province, postalCode) VALUES(?,?,?,?,?,?,?)");
    }

    @Override
    public boolean update(Customer entity) throws SQLException, ClassNotFoundException {
        return SQLUtil.executeUpdate("UPDATE Customer SET custTitle=?, custName=?, custAddress=?, city=?, province=?, postalCode=? WHERE custID=?", entity.getCustTitle(),entity.getCustName(),entity.getCustAddress(),entity.getCity(),entity.getProvince(),entity.getPostalCode(),entity.getCustID());
    }

    @Override
    public Customer search(String id) throws SQLException, ClassNotFoundException {
        ResultSet rst = SQLUtil.executeQuery("SELECT * FROM Customer WHERE custID=?",id);
        if (rst.next()) {
            return new Customer(rst.getString(1),rst.getString(2),rst.getString(3), rst.getString(4), rst.getString(5), rst.getString(6), rst.getString(7));
        }
        return null;
    }

    @Override
    public boolean exist(String id) throws SQLException, ClassNotFoundException {
        return SQLUtil.executeQuery("SELECT custID FROM Customer WHERE custID=?",id).next();
    }

    @Override
    public boolean delete(String id) throws SQLException, ClassNotFoundException {
        return SQLUtil.executeUpdate("DELETE FROM Customer WHERE custID=?",id);
    }

    @Override
    public String generateNewID() throws SQLException, ClassNotFoundException {
        ResultSet rst = SQLUtil.executeQuery("SELECT custId FROM Customer ORDER BY custId DESC LIMIT 1;");
        if (rst.next()) {
            String id = rst.getString("custId");
            int newCustomerId = Integer.parseInt(id.replace("C", "")) + 1;
            return String.format("C%03d", newCustomerId);
        } else {
            return "C001";
        }
    }

    /*@Override
    public ArrayList<Customer> getAllCustomersByAddress(String address) {
        return null;
    }*/
}
