package com.kym.personalexpenditure;

import com.kym.pojo.Category;
import com.kym.pojo.Transaction;
import com.kym.pojo.Utils;
import com.kym.services.CategoryService;
import com.kym.services.TransactionServices;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class SecondaryController implements Initializable{

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }
    
    @FXML private ComboBox <Category> categories;
    @FXML private TextField tfAmount;
    @FXML private DatePicker dpDate;
    @FXML private TextField tfDesc;
    @FXML private Button btnSave;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        CategoryService c = new CategoryService();
        try {
            List<Category> cates = c.getCates();
            this.categories.setItems(FXCollections.observableList(cates));
            Utils.setupCurrencyTextField(tfAmount);
            dpDate.setValue(LocalDate.now()); 
        } catch (SQLException ex) {
            Logger.getLogger(SecondaryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
   
    
}