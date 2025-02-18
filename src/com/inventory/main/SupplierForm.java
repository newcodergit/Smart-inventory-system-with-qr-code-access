package com.inventory.main;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SupplierForm extends JFrame {
    private JTextField nameField;
    private JTextField contactField;
    private JButton saveButton;
    private JTable supplierTable;
    private DefaultTableModel tableModel;

    public SupplierForm() {
        setTitle("Manage Suppliers");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        nameField = new JTextField();
        contactField = new JTextField();
        saveButton = new JButton("Save");

        add(new JLabel("Name:"));
        add(nameField);
        add(new JLabel("Contact Info:"));
        add(contactField);
        add(saveButton);

        String[] columnNames = {"ID", "Name", "Contact Info"};
        tableModel = new DefaultTableModel(columnNames, 0);
        supplierTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(supplierTable);
        add(scrollPane);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveSupplier();
            }
        });

        loadSuppliers();
    }

    private void saveSupplier() {
        String name = nameField.getText();
        String contact = contactField.getText();

        Connection connection = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO suppliers (name, contact_info) VALUES (?, ?)");
            ps.setString(1, name);
            ps.setString(2, contact);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Supplier saved successfully!");
            loadSuppliers();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadSuppliers() {
        Connection connection = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM suppliers");
            ResultSet rs = ps.executeQuery();
            tableModel.setRowCount(0);
            while (rs.next()) {
                Object[] row = {rs.getInt("id"), rs.getString("name"), rs.getString("contact_info")};
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
