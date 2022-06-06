package lk.ijse.pos.dao.custom.impl;

import lk.ijse.pos.dao.SQLUtil;
import lk.ijse.pos.dao.custom.ItemDAO;
import lk.ijse.pos.entity.Item;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ItemDAOImpl implements ItemDAO {
    @Override
    public ArrayList<Item> getAll() throws SQLException, ClassNotFoundException {
        ResultSet rst = SQLUtil.executeQuery("SELECT * FROM Item");
        ArrayList<Item> allItems = new ArrayList<>();
        while (rst.next()) {
            allItems.add(new Item(rst.getString(1), rst.getString(2), rst.getString(3), rst.getDouble(4), rst.getInt(5)));
        }
        return allItems;
    }

    @Override
    public boolean save(Item entity) throws SQLException, ClassNotFoundException {
        return SQLUtil.executeUpdate("INSERT INTO Item (itemCode, description, packSize, unitPrice, qtyOnHand) VALUES (?,?,?,?,?)", entity.getItemCode(),entity.getDescription(),entity.getPackSize(),entity.getUnitPrice(),entity.getQtyOnHand());
    }

    @Override
    public boolean update(Item entity) throws SQLException, ClassNotFoundException {
        return SQLUtil.executeUpdate("UPDATE Item SET description=?, packSize=?, unitPrice=?, qtyOnHand=? WHERE itemCode=?", entity.getDescription(),entity.getPackSize(),entity.getUnitPrice(),entity.getQtyOnHand(),entity.getItemCode());
    }

    @Override
    public Item search(String itemCode) throws SQLException, ClassNotFoundException {
        ResultSet rst = SQLUtil.executeQuery("SELECT * FROM Item WHERE itemCode=?",itemCode);
        if (rst.next()) {
            return new Item(rst.getString(1),rst.getString(2),rst.getString(3),rst.getDouble(4),rst.getInt(5));
        }
        return null;
    }

    @Override
    public boolean exist(String itemCode) throws SQLException, ClassNotFoundException {
        return SQLUtil.executeQuery("SELECT itemCode FROM Item WHERE itemCode=?",itemCode).next();
    }

    @Override
    public boolean delete(String itemCode) throws SQLException, ClassNotFoundException {
        return SQLUtil.executeUpdate("DELETE FROM Item WHERE itemCode=?",itemCode);
    }

    @Override
    public String generateNewID() throws SQLException, ClassNotFoundException {
        ResultSet rst = SQLUtil.executeQuery("SELECT itemCode FROM Item ORDER BY itemCode DESC LIMIT 1;");
        if (rst.next()) {
            String itemCode = rst.getString("itemCode");
            int newCustomerId = Integer.parseInt(itemCode.replace("I", "")) + 1;
            return String.format("I%03d", newCustomerId);
        } else {
            return "I001";
        }
    }

    @Override
    public ArrayList<Item> getMostMovableItems() throws SQLException, ClassNotFoundException {
        ResultSet rst = SQLUtil.executeQuery("SELECT * FROM Item ORDER BY qtyOnHand ASC");
        ArrayList<Item> allItems = new ArrayList<>();
        while (rst.next()) {
            allItems.add(new Item(rst.getString(1), rst.getString(2), rst.getString(3), rst.getDouble(4), rst.getInt(5)));
        }
        return allItems;
    }

    @Override
    public ArrayList<Item> getLeastMovableItems() throws SQLException, ClassNotFoundException {
        ResultSet rst = SQLUtil.executeQuery("SELECT * FROM Item ORDER BY qtyOnHand DESC");
        ArrayList<Item> allItems = new ArrayList<>();
        while (rst.next()) {
            allItems.add(new Item(rst.getString(1), rst.getString(2), rst.getString(3), rst.getDouble(4), rst.getInt(5)));
        }
        return allItems;
    }

}
