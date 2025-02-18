package com.inventory.main;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;
import javax.imageio.ImageIO;

public class Dashboard extends JFrame {
    private JButton productButton;
    private JButton supplierButton;
    private JButton salesButton;
    private JButton qrScanButton;
    private JButton logoutButton;

    public Dashboard() {
        setTitle("Dashboard");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        productButton = new JButton("Manage Products");
        supplierButton = new JButton("Manage Suppliers");
        salesButton = new JButton("Manage Sales");
        qrScanButton = new JButton("Scan QR Code");
        logoutButton = new JButton("Logout");

        add(productButton);
        add(supplierButton);
        add(salesButton);
        add(qrScanButton);
        add(logoutButton);

        productButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ProductForm().setVisible(true);
            }
        });

        supplierButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SupplierForm().setVisible(true);
            }
        });

        salesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SalesForm().setVisible(true);
            }
        });

        qrScanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scanQRCode();
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new LoginForm().setVisible(true);
                dispose();
            }
        });
    }

    private void scanQRCode() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                BufferedImage image = ImageIO.read(file);
                LuminanceSource source = new BufferedImageLuminanceSource(image);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                
                Hashtable<DecodeHintType, Object> hints = new Hashtable<>();
                hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
                
                Result result = new MultiFormatReader().decode(bitmap, hints);
                processQRCodeResult(result.getText());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error scanning QR code: " + e.getMessage());
            }
        }
    }

    private void processQRCodeResult(String qrData) {
        String[] parts = qrData.split("\\|");
        if (parts.length == 2) {
            String productName = parts[0];
            String sku = parts[1];
            JOptionPane.showMessageDialog(this, 
                "Scanned Product:\nName: " + productName + "\nSKU: " + sku);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid QR code format");
        }
    }
}
