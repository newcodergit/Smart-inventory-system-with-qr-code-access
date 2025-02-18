package com.inventory.main;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SalesForm extends JFrame {
    private JTextField productIdField;
    private JTextField quantityField;
    private JButton saveButton;
    private JTable salesTable;
    private DefaultTableModel tableModel;

    public SalesForm() {
        setTitle("Manage Sales");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        productIdField = new JTextField();
        quantityField = new JTextField();
        saveButton = new JButton("Save");

        add(new JLabel("Product ID:"));
        add(productIdField);
        add(new JLabel("Quantity:"));
        add(quantityField);
        add(saveButton);

        String[] columnNames = {"ID", "Product ID", "Quantity", "Date"};
        tableModel = new DefaultTableModel(columnNames, 0);
        salesTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(salesTable);
        add(scrollPane);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveSale();
            }
        });

        loadSales();
    }

    private void saveSale() {
        int productId = Integer.parseInt(productIdField.getText());
        int quantity = Integer.parseInt(quantityField.getText());

        Connection connection = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO sales (product_id, quantity) VALUES (?, ?)");
            ps.setInt(1, productId);
            ps.setInt(2, quantity);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Sale recorded successfully!");
            loadSales();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadSales() {
        Connection connection = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM sales");
            ResultSet rs = ps.executeQuery();
            tableModel.setRowCount(0);
            while (rs.next()) {
                Object[] row = {rs.getInt("id"), rs.getInt("product_id"), rs.getInt("quantity"), rs.getTimestamp("date")};
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
