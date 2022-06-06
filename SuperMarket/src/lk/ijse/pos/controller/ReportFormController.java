package lk.ijse.pos.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.ijse.pos.bo.BOFactory;
import lk.ijse.pos.bo.custom.ItemBO;
import lk.ijse.pos.dto.ItemDTO;
import lk.ijse.pos.view.tm.ItemTM;

import java.sql.SQLException;
import java.util.ArrayList;

public class ReportFormController {
    public TableView<ItemTM> tblMostItems;
    public TableColumn colMostCode;
    public TableColumn colMostDescription;
    public TableColumn colMostPackSize;
    public TableColumn colMostQtyOnHand;
    public TableColumn colMostUnitPrice;
    public TableView<ItemTM> tblLeastItems;
    public TableColumn colLeastCode;
    public TableColumn colLeastDescription;
    public TableColumn colLeastPackSize;
    public TableColumn colLeastQtyOnHand;
    public TableColumn colLeastUnitPrice;

    // Property Injection(DI)
    private final ItemBO itemBO = (ItemBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.ITEM);

    public void initialize() {
        colMostCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colMostDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colMostPackSize.setCellValueFactory(new PropertyValueFactory<>("packSize"));
        colMostUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colMostQtyOnHand.setCellValueFactory(new PropertyValueFactory<>("qtyOnHand"));

        colLeastCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colLeastDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colLeastPackSize.setCellValueFactory(new PropertyValueFactory<>("packSize"));
        colLeastUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colLeastQtyOnHand.setCellValueFactory(new PropertyValueFactory<>("qtyOnHand"));

        loadMostMovableItems();
        loadLeastMovableItems();
    }

    private void loadMostMovableItems() {
        tblMostItems.getItems().clear();
        /*Get all items*/
        try {
            ArrayList<ItemDTO> mostMovableItems = itemBO.getMostMovableItems();
            for (ItemDTO item : mostMovableItems) {
                tblMostItems.getItems().add(new ItemTM(item.getItemCode(), item.getDescription(), item.getPackSize(), item.getUnitPrice(), item.getQtyOnHand()));
            }

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        } catch (ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }


    }

    private void loadLeastMovableItems() {
        tblLeastItems.getItems().clear();
        /*Get all items*/
        try {
            ArrayList<ItemDTO> leastMovableItems = itemBO.getLeastMovableItems();
            for (ItemDTO item : leastMovableItems) {
                tblLeastItems.getItems().add(new ItemTM(item.getItemCode(), item.getDescription(), item.getPackSize(), item.getUnitPrice(), item.getQtyOnHand()));
            }

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        } catch (ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }


    }

}
