module com.kym.personalexpenditure {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.base;

    opens com.kym.personalexpenditure to javafx.fxml;
    exports com.kym.personalexpenditure;

    exports com.kym.pojo; 

  

    exports com.kym.services;
}
