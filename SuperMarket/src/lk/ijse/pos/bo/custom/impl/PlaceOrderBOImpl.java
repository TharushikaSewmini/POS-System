package lk.ijse.pos.bo.custom.impl;

import lk.ijse.pos.bo.custom.PlaceOrderBO;
import lk.ijse.pos.dao.DAOFactory;
import lk.ijse.pos.db.DBConnection;
import lk.ijse.pos.dao.custom.*;
import lk.ijse.pos.dto.*;
import lk.ijse.pos.entity.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class PlaceOrderBOImpl implements PlaceOrderBO {
    //Hiding the object creation logic using the Factory pattern
    private final CustomerDAO customerDAO = (CustomerDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.CUSTOMER);
    private final ItemDAO itemDAO = (ItemDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.ITEM);
    private final OrderDAO orderDAO = (OrderDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.ORDER);
    private final OrderDetailDAO orderDetailDAO = (OrderDetailDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.ORDERDETAIL);
    private final QueryDAO queryDAO = (QueryDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.QUERYDAO);

    @Override
    public boolean purchaseOrder(OrderDTO dto) throws SQLException, ClassNotFoundException {
        /*Transaction*/

        Connection connection = DBConnection.getDbConnection().getConnection();
        /*if order id already exist*/
        if (orderDAO.exist(dto.getOrderID())) {

        }

        connection.setAutoCommit(false);
        boolean save = orderDAO.save(new Orders(dto.getOrderID(),dto.getOrderDate(),dto.getCustID()));

        if (!save) {
            connection.rollback();
            connection.setAutoCommit(true);
            return false;
        }


        for (OrderDetailDTO detailDTO : dto.getOrderDetail()) {
            boolean save1 = orderDetailDAO.save(new OrderDetail(detailDTO.getOrderId(), detailDTO.getItemCode(), detailDTO.getOrderQty(), detailDTO.getDiscount()));
            if (!save1) {
                connection.rollback();
                connection.setAutoCommit(true);
                return false;
            }

            //Search & Update Item
            ItemDTO item = searchItem(detailDTO.getItemCode());
            item.setQtyOnHand(item.getQtyOnHand() - detailDTO.getOrderQty());

            //update item
            boolean update= itemDAO.update(new Item(item.getItemCode(), item.getDescription(), item.getPackSize(), item.getUnitPrice(), item.getQtyOnHand()));

            if (!update) {
                connection.rollback();
                connection.setAutoCommit(true);
                return false;
            }
        }
        connection.commit();
        connection.setAutoCommit(true);
        return true;

    }

    @Override
    public boolean manageOrder(OrderDTO dto) throws SQLException, ClassNotFoundException {
        /*Transaction*/

        Connection connection = DBConnection.getDbConnection().getConnection();
        /*if order id already exist*/
        if (orderDAO.exist(dto.getOrderID())) {

        }

        connection.setAutoCommit(false);
        boolean update = orderDAO.update(new Orders(dto.getOrderID(),dto.getOrderDate(),dto.getCustID()));

        if (!update) {
            connection.rollback();
            connection.setAutoCommit(true);
            return false;
        }


        for (OrderDetailDTO detailDTO : dto.getOrderDetail()) {
            boolean update1 = orderDetailDAO.update(new OrderDetail(detailDTO.getOrderId(), detailDTO.getItemCode(), detailDTO.getOrderQty(), detailDTO.getDiscount()));
            if (!update1) {
                connection.rollback();
                connection.setAutoCommit(true);
                return false;
            }

            //Search & Update Item
            ItemDTO item = searchItem(detailDTO.getItemCode());
            item.setQtyOnHand(item.getQtyOnHand() - detailDTO.getOrderQty());

            //update item
            boolean update3= itemDAO.update(new Item(item.getItemCode(), item.getDescription(), item.getPackSize(), item.getUnitPrice(), item.getQtyOnHand()));

            if (!update3) {
                connection.rollback();
                connection.setAutoCommit(true);
                return false;
            }
        }
        connection.commit();
        connection.setAutoCommit(true);
        return true;

    }

    @Override
    public CustomerDTO searchCustomer(String id) throws SQLException, ClassNotFoundException {
        Customer entity = customerDAO.search(id);
        return new CustomerDTO(entity.getCustID(),entity.getCustTitle(),entity.getCustName(),entity.getCustAddress(),entity.getCity(),entity.getProvince(),entity.getPostalCode());
    }

    @Override
    public ItemDTO searchItem(String itemCode) throws SQLException, ClassNotFoundException {
        Item entity = itemDAO.search(itemCode);
        return new ItemDTO(entity.getItemCode(),entity.getDescription(),entity.getPackSize(),entity.getUnitPrice(),entity.getQtyOnHand());
    }

    @Override
    public boolean checkItemIsAvailable(String itemCode) throws SQLException, ClassNotFoundException {
        return itemDAO.exist(itemCode);
    }

    @Override
    public boolean checkCustomerIsAvailable(String id) throws SQLException, ClassNotFoundException {
        return customerDAO.exist(id);
    }

    @Override
    public String generateNewOrderID() throws SQLException, ClassNotFoundException {
        return orderDAO.generateNewID();
    }

    @Override
    public ArrayList<CustomerDTO> getAllCustomers() throws SQLException, ClassNotFoundException {
        ArrayList<Customer> all = customerDAO.getAll();
        ArrayList<CustomerDTO> allCustomers = new ArrayList<>();
        for (Customer entity : all) {
            allCustomers.add(new CustomerDTO(entity.getCustID(),entity.getCustTitle(),entity.getCustName(),entity.getCustAddress(),entity.getCity(),entity.getProvince(),entity.getPostalCode()));
        }
        return allCustomers;
    }

    @Override
    public ArrayList<ItemDTO> getAllItems() throws SQLException, ClassNotFoundException {
        ArrayList<Item> all = itemDAO.getAll();
        ArrayList<ItemDTO> allItems = new ArrayList<>();
        for (Item entity : all) {
            allItems.add(new ItemDTO(entity.getItemCode(),entity.getDescription(),entity.getPackSize(),entity.getUnitPrice(),entity.getQtyOnHand()));
        }
        return allItems;
    }

    @Override
    public boolean checkOrderIsAvailable(String orderID) throws SQLException, ClassNotFoundException {
        return orderDAO.exist(orderID);
    }

    /*@Override
    public OrderDTO searchOrder(String orderID) throws SQLException, ClassNotFoundException {
        Orders ent = orderDAO.search(orderID);
        return new OrderDTO(ent.getOrderID(), ent.getOrderDate(), ent.getCustID());
    }*/

    @Override
    public ArrayList<OrderDTO> getAllOrderIdsByCustomerID(String custID) throws SQLException, ClassNotFoundException {
        ArrayList<Orders> all = orderDAO.getOrderIDFromCustID(custID);
        ArrayList<OrderDTO> allOrders = new ArrayList<>();
        for (Orders entity : all) {
            allOrders.add(new OrderDTO(entity.getOrderID()));
        }
        return allOrders;
    }

    @Override
    public ArrayList<CustomDTO> getItemByOrderId(String orderID) throws SQLException, ClassNotFoundException {
        ArrayList<CustomEntity> all = queryDAO.getItemByOrderId(orderID);
        ArrayList<CustomDTO> itemDetails = new ArrayList<>();
        for (CustomEntity entity : all) {
            itemDetails.add(new CustomDTO(entity.getItemCode(), entity.getDescription(), entity.getUnitPrice(), entity.getOrderQty()));
        }
        return itemDetails;
    }
}
