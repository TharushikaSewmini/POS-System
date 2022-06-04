package lk.ijse.pos.bo;

import lk.ijse.pos.bo.custom.impl.CustomerBOImpl;
import lk.ijse.pos.bo.custom.impl.ItemBOImpl;
import lk.ijse.pos.bo.custom.impl.PlaceOrderBOImpl;

public class BOFactory {
    private static BOFactory boFactory;

    private BOFactory() {

    }

    public static BOFactory getBoFactory() {
        if(boFactory == null) {
            boFactory = new BOFactory();
        }
        return boFactory;
    }

    public enum BOTypes {
        CUSTOMER, ITEM, PURCHASE_ORDER
    }

    public SuperBO getBO(BOFactory.BOTypes types) {
        switch(types) {
            case CUSTOMER:
                return new CustomerBOImpl();

            case ITEM:
                return new ItemBOImpl();

            case PURCHASE_ORDER:
                return new PlaceOrderBOImpl();

            default:
                return null;
        }
    }
}
