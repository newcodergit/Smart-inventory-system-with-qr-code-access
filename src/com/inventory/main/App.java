package com.inventory.main;

import javax.swing.*;
import java.awt.*;

public class App {
    public static void main(String[] args) {
        DatabaseConnection.setConfig("jdbc:mysql://localhost:3306/inventory", "root", "ishan403");
        
        EventQueue.invokeLater(() -> {
            try {
                LoginForm frame = new LoginForm();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
