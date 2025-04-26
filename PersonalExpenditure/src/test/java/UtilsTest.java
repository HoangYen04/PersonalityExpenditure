/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import com.kym.services.Utils;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import static org.mockito.Mockito.*;
import javafx.scene.control.TextFormatter;
import static org.junit.jupiter.api.Assertions.*;
import javafx.scene.control.Alert;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.testfx.framework.junit5.ApplicationTest;
/**
 *
 * @author ADMIN
 */
public class UtilsTest  {
    

    @Test
    public void testParseCurrency() {
        // Test parseCurrency cho đầu vào hợp lệ
        String validInput = "1,234,567";
        double expected = 1234567;

        double result = Utils.parseCurrency(validInput);

        Assertions.assertEquals(expected, result);
    }

    @Test
    public void testParseCurrency_InvalidInput() {
        // Test parseCurrency với đầu vào không hợp lệ
        String invalidInput = "123abc";

        Assertions.assertThrows(NumberFormatException.class, () -> {
            Utils.parseCurrency(invalidInput);
        });
    }
    

   
}
