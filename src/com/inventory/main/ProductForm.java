package com.inventory.main;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Base64;

public class ProductForm extends JFrame {
    private JTextField nameField;
    private JTextField skuField;
    private JTextField categoryField;
    private JTextField priceField;
    private JTextField stockLevelField;
    private JButton saveButton;
    private JButton generateQRButton;
    private JLabel qrCodeLabel;
    private String generatedQRCode;

    public ProductForm() {
        setTitle("Add Product");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        nameField = new JTextField();
        skuField = new JTextField();
        categoryField = new JTextField();
        priceField = new JTextField();
        stockLevelField = new JTextField();
        saveButton = new JButton("Save");
        generateQRButton = new JButton("Generate QR Code");
        qrCodeLabel = new JLabel("QR Code will appear here");

        add(new JLabel("Name:"));
        add(nameField);
        add(new JLabel("SKU:"));
        add(skuField);
        add(new JLabel("Category:"));
        add(categoryField);
        add(new JLabel("Price:"));
        add(priceField);
        add(new JLabel("Stock Level:"));
        add(stockLevelField);
        add(generateQRButton);
        add(qrCodeLabel);
        JButton saveQRButton = new JButton("Save QR Code");
        add(saveQRButton);
        add(saveButton);


        generateQRButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateQRCode();
            }
        });

        saveQRButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (generatedQRCode == null) {
                    JOptionPane.showMessageDialog(ProductForm.this, 
                        "Please generate a QR code first");
                    return;
                }
                
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Save QR Code");
                fileChooser.setSelectedFile(new File("qrcode.png"));
                
                int userSelection = fileChooser.showSaveDialog(ProductForm.this);
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fileChooser.getSelectedFile();
                    try {
                        byte[] imageBytes = Base64.getDecoder().decode(generatedQRCode);
                        java.nio.file.Files.write(fileToSave.toPath(), imageBytes);
                        JOptionPane.showMessageDialog(ProductForm.this, 
                            "QR code saved successfully!");
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(ProductForm.this, 
                            "Error saving QR code: " + ex.getMessage());
                    }
                }
            }
        });

        saveButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                saveProduct();
            }
        });
    }

    private void generateQRCode() {
        String productData = nameField.getText() + "|" + skuField.getText();
        if (productData.length() < 2) {
            JOptionPane.showMessageDialog(this, "Please enter product name and SKU first");
            return;
        }

        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(productData, BarcodeFormat.QR_CODE, 200, 200);

            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            byte[] pngData = pngOutputStream.toByteArray();
            generatedQRCode = Base64.getEncoder().encodeToString(pngData);

            ImageIcon icon = new ImageIcon(pngData);
            qrCodeLabel.setIcon(icon);
            qrCodeLabel.setText("");
        } catch (WriterException | IOException e) {
            JOptionPane.showMessageDialog(this, "Error generating QR code: " + e.getMessage());
        }
    }

    private void saveProduct() {
        String name = nameField.getText();
        String sku = skuField.getText();
        String category = categoryField.getText();
        double price = Double.parseDouble(priceField.getText());
        int stockLevel = Integer.parseInt(stockLevelField.getText());

        if (generatedQRCode == null) {
            JOptionPane.showMessageDialog(this, "Please generate QR code first");
            return;
        }

        Connection connection = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO products (name, sku, category, price, stock_level, qr_code) VALUES (?, ?, ?, ?, ?, ?)");
            ps.setString(1, name);
            ps.setString(2, sku);
            ps.setString(3, category);
            ps.setDouble(4, price);
            ps.setInt(5, stockLevel);
            ps.setString(6, generatedQRCode);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Product saved successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ProductForm().setVisible(true);
    }
}
