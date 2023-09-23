package lk.ijse.javafxla.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.stokemanagenew.db.DbConnection;
import lk.ijse.stokemanagenew.dto.Item;
import lk.ijse.stokemanagenew.dto.SupplierDto;
import lk.ijse.stokemanagenew.dto.tm.ItemTableModel;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemManageFormController {

    public ComboBox cmdSupplierId;
    public TextField textSupplierName;
    public TextField textSupplierTelephone;
    public TextField textSupplierShop;
    @FXML
    private TableColumn<?, ?> colCode;

    @FXML
    private TableColumn<?, ?> colDescription;

    @FXML
    private TableColumn<?, ?> colQtyOnHand;

    @FXML
    private TableColumn<?, ?> colUnitPrice;

    @FXML
    private TableView<ItemTableModel> tableItem;



    public AnchorPane root;
    public TextField textItemCode;
    public TextField textDescription;
    public TextField textUnitPrice;
    public TextField textQuantityOnHand;

    public void initialize() throws SQLException {
        System.out.println("Item Form Just Loaded");

        List<SupplierDto>supplierDtos=loadAllSupplierIds();

        setSupplierIds(supplierDtos);
        
        setSellValueFactory();
        List<Item>itemList= loadAllItems();
        setTableData(itemList);
    }

    private void setSupplierIds(List<SupplierDto> supplierDtos) {

        ObservableList<String>obList=FXCollections.observableArrayList();

        for(SupplierDto supplierDto:supplierDtos){
             obList.add(supplierDto.getSupplierId());
        }

        cmdSupplierId.setItems(obList);

    }

    private List<SupplierDto> loadAllSupplierIds() {
        List<SupplierDto>supplierList=new ArrayList<>();
        try {
            Connection con=DbConnection.getInstance().getConnection();

            String sql= "SELECT *FROM supplier";
            PreparedStatement pstm=con.prepareStatement(sql);

            ResultSet resultSet=pstm.executeQuery();

            while(resultSet.next()){
                supplierList.add(new SupplierDto(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4)
                ));

            }
        } catch (SQLException e) {
             new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }

        return supplierList;
    }

    private void setTableData(List<Item> itemList) {
        ObservableList<ItemTableModel>obList= FXCollections.observableArrayList();

        for(Item item:itemList){
           var itemTableModel= new ItemTableModel(item.getCode(), item.getDescription(), item.getUnitPrice(), item.getQuantityOnHand());
            obList.add(itemTableModel);
        }
        tableItem.setItems(obList);
    }

    private void setSellValueFactory() {
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colQtyOnHand.setCellValueFactory(new PropertyValueFactory<>("qtyOnHand"));
    }

    private List<Item> loadAllItems() throws SQLException {

            Connection con=DbConnection.getInstance().getConnection();
            String sql="SELECT*FROM item";
            Statement stm=con.createStatement();
            stm.executeQuery(sql);

            ResultSet resultSet= stm.executeQuery(sql);

        List<Item>itemList=new ArrayList<>();

        while (resultSet.next()){
            String code= resultSet.getString(1);
            String description=resultSet.getString(2);
            double unitPrice= Double.parseDouble(resultSet.getString(3));
            int quantityOnHand= Integer.parseInt(resultSet.getString(4));

            var item=new Item(code,description,unitPrice,quantityOnHand);

            itemList.add(item);
        }

         return itemList;

    }

    public void btnBackOnAction(ActionEvent actionEvent) throws IOException {
        Parent node=FXMLLoader.load(getClass().getResource("/view/dashboard_form.fxml"));
        Scene scene=new Scene(node);
        Stage stage= (Stage) this.root.getScene().getWindow();
        stage.setTitle("Dashboard Form");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    public void btnSaveOnAction(ActionEvent actionEvent){
        String code= textItemCode.getText();
        String description= textDescription.getText();
        Double unitPrice= Double.valueOf(textUnitPrice.getText());
        Integer quantyOnHand= Integer.valueOf(textUnitPrice.getText());

        Connection con= null;
        try {
            con = DbConnection.getInstance().getConnection();

        String sql= "INSERT INTO item VALUES(?,?,?,?)";
        PreparedStatement pstm=con.prepareStatement(sql);

        pstm.setString(1,code);
        pstm.setString(2,description);
        pstm.setString(3, String.valueOf(unitPrice));
        pstm.setString(4, String.valueOf(unitPrice));

        boolean isSaved= pstm.executeUpdate()>0;

        if(isSaved){
            new Alert(Alert.AlertType.CONFIRMATION,"Item Saved!").show();
        }

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }

    }

    public void itemCodeOnAction(ActionEvent actionEvent) {
        String code=textItemCode.getText();

        try {
            Connection con=DbConnection.getInstance().getConnection();

            String sql="SELECT * FROM item WHERE code=?";

            PreparedStatement pstm =con.prepareStatement(sql);

            pstm.setString(1,code);

            ResultSet resultSet=pstm.executeQuery();

            if(resultSet.next()){
                String itemCode=resultSet.getString(1);
                String itemDescription= resultSet.getString(2);
                double itemUnitPrice=resultSet.getDouble(3);
                int quantityOnHand=resultSet.getInt(4);

                var item=new Item(itemCode,itemDescription,itemUnitPrice,quantityOnHand);

                setFields(item);
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void setFields(Item item) {
        textItemCode.setText(item.getCode());
        textDescription.setText(item.getDescription());
        textUnitPrice.setText(String.valueOf(item.getUnitPrice()));
        textQuantityOnHand.setText(String.valueOf(item.getUnitPrice()));
    }

    public void cmdSupplierIdOnAction(ActionEvent actionEvent) {
        String supplierId=String.valueOf(cmdSupplierId.getValue());

        try {
            Connection con=DbConnection.getInstance().getConnection();
            String sql="SELECT * FROM supplier WHERE supplierId=?";

            PreparedStatement pstm=con.prepareStatement(sql);

            pstm.setString(1,supplierId);

            ResultSet resultSet=pstm.executeQuery();

            if(resultSet.next()){
                var supplierDto=new SupplierDto(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4)
                );

            fillSupplierFields(supplierDto);


            }else {
                new Alert(Alert.AlertType.INFORMATION,"Oops supplier did not find!").show();
            }

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }

    private void fillSupplierFields(SupplierDto supplierDto) {
        textSupplierName.setText(supplierDto.getName());
        textSupplierShop.setText(supplierDto.getShop());
        textSupplierTelephone.setText(supplierDto.getTel());
    }

    public void btnClearOnAction(ActionEvent actionEvent) {
    }



}
