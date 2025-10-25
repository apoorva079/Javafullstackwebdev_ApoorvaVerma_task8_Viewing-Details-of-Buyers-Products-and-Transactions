import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.io.FileOutputStream; 
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// NECESSARY PDF IMPORTS
import com.itextpdf.text.*; 
import com.itextpdf.text.pdf.*;


public class Billing extends JFrame {

    // --- 1. DECLARATION ---
    private DefaultTableModel model;
    private JTable billingTable;
    
    // Buyer/Header Fields
    private JTextField buyerContactField;
    private JLabel buyerNameLabel, dateLabel, timeLabel;
    
    // Product Input Fields
    private JTextField itemNameField, rateField, quantityField;
    
    // Footer Fields
    private JTextField totalField, paidField, returnedField;
    
    private JButton addButton, saveButton, closeButton;

    public Billing() {
        setTitle("Billing System");
        setSize(850, 650); 
        setLayout(null); 
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Initialize Total Field to 0.00
        totalField = new JTextField("0.00");
        totalField.setEditable(false); 
        
        // --- UI SETUP & POSITIONING ---
        
        // Title
        JLabel title = new JLabel("Generate Bill");
        // FIX: Explicitly use java.awt.Font to resolve ambiguity
        title.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24)); 
        title.setBounds(350, 10, 200, 30);
        add(title);
        
        // Buyer Info Section (Top Left)
        int y = 50;
        add(new JLabel("Contact No:")).setBounds(30, y, 100, 25);
        buyerContactField = new JTextField();
        buyerContactField.setBounds(120, y, 150, 25);
        add(buyerContactField);
        
        // --- Buyer Name LABEL (Will be updated by findBuyerName method) ---
        add(new JLabel("Name:")).setBounds(300, y, 50, 25);
        buyerNameLabel = new JLabel("[Buyer Name]");
        buyerNameLabel.setBounds(350, y, 150, 25);
        add(buyerNameLabel);
        
        // Date/Time Section (Top Right)
        dateLabel = new JLabel("Date: " + DateTimeFormatter.ofPattern("dd-MM-yyyy").format(LocalDateTime.now()));
        dateLabel.setBounds(600, y, 150, 25);
        add(dateLabel);
        timeLabel = new JLabel("Time: " + DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now()));
        timeLabel.setBounds(600, y + 25, 150, 25);
        add(timeLabel);

        // Product Input Section
        y += 70;
        add(new JLabel("Item Name:")).setBounds(30, y, 80, 25);
        itemNameField = new JTextField();
        itemNameField.setBounds(120, y, 150, 25);
        add(itemNameField);

        add(new JLabel("Rate:")).setBounds(280, y, 50, 25);
        rateField = new JTextField();
        rateField.setBounds(330, y, 80, 25);
        add(rateField);

        add(new JLabel("Qty:")).setBounds(420, y, 50, 25);
        quantityField = new JTextField();
        quantityField.setBounds(470, y, 80, 25);
        add(quantityField);

        // Add Button
        addButton = new JButton("Add Item");
        addButton.setBounds(570, y, 120, 25);
        add(addButton);

        // --- JTable Setup ---
        model = new DefaultTableModel(new Object[]{"Product Name", "Rate", "Quantity", "Total"}, 0);
        billingTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(billingTable);
        scrollPane.setBounds(30, 150, 790, 250);
        add(scrollPane);
        
        // --- Footer Totals ---
        y = 420;
        add(new JLabel("Grand Total:")).setBounds(550, y, 100, 25);
        totalField.setBounds(650, y, 150, 25);
        add(totalField);

        add(new JLabel("Paid Amount:")).setBounds(550, y + 40, 100, 25);
        paidField = new JTextField();
        paidField.setBounds(650, y + 40, 150, 25);
        add(paidField);

        add(new JLabel("Returned:")).setBounds(550, y + 80, 100, 25);
        returnedField = new JTextField("0.00");
        returnedField.setEditable(false);
        returnedField.setBounds(650, y + 80, 150, 25);
        add(returnedField);

        // Final Buttons
        saveButton = new JButton("Save & Print Bill");
        closeButton = new JButton("Close");
        saveButton.setBounds(30, 500, 200, 40);
        closeButton.setBounds(250, 500, 150, 40);
        add(saveButton);
        add(closeButton);
        
        // --- LOGIC IMPLEMENTATION ---
        
        // Adding  Focus Listener for Buyer Lookup 
        buyerContactField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                findBuyerName();
            }
        });
        
        addButton.addActionListener(e -> addItemToBill());
        saveButton.addActionListener(e -> saveAndPrintBill());
        closeButton.addActionListener(e -> dispose());

        setVisible(true);
    }
    
    // --- METHOD 1: ADD ITEM TO BILL (Calculation) ---
    private void addItemToBill() {
        try {
            String itemName = itemNameField.getText().trim();
            double rate = Double.parseDouble(rateField.getText().trim());
            int quantity = Integer.parseInt(quantityField.getText().trim());

            if (itemName.isEmpty() || rate <= 0 || quantity <= 0) {
                 JOptionPane.showMessageDialog(this, "Please enter valid item details.", "Input Error", JOptionPane.ERROR_MESSAGE);
                 return;
            }

            double subtotal = rate * quantity;

            model.addRow(new Object[]{itemName, String.format("%.2f", rate), quantity, String.format("%.2f", subtotal)});

            double currentTotal = Double.parseDouble(totalField.getText());
            double newGrandTotal = currentTotal + subtotal;
            totalField.setText(String.format("%.2f", newGrandTotal));

            itemNameField.setText("");
            rateField.setText("");
            quantityField.setText("");
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Rate and Quantity must be valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // --- METHOD 2: SAVE AND PRINT BILL (DB Inserting & PDF) ---
    private void saveAndPrintBill() {
        try {
            double grandTotal = Double.parseDouble(totalField.getText());
            double paid = Double.parseDouble(paidField.getText());
            
            if (paid < grandTotal) {
                JOptionPane.showMessageDialog(this, "Paid amount must be greater than or equal to Grand Total.", "Payment Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (model.getRowCount() == 0) {
                 JOptionPane.showMessageDialog(this, "Please add items to the bill first.", "Input Error", JOptionPane.ERROR_MESSAGE);
                 return;
            }
            
            double returned = paid - grandTotal;
            returnedField.setText(String.format("%.2f", returned));
            
            // 1. DATABASE INSERT (Transaction Summary)
            Connection con = DatabaseConnection.getConnection();
            if (con != null) {
                String buyerName = buyerNameLabel.getText().replace("[", "").replace("]", "");
                String contact = buyerContactField.getText();
                
                String sql = "INSERT INTO transactions (date, buyerName, contactNumber, totalAmount, paidAmount, returnedAmount) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = con.prepareStatement(sql);
                
                ps.setString(1, LocalDateTime.now().toString());
                ps.setString(2, buyerName);
                ps.setString(3, contact);
                ps.setDouble(4, grandTotal);
                ps.setDouble(5, paid);
                ps.setDouble(6, returned);
                
                ps.executeUpdate();
                con.close();
            }

            // 2. PDF GENERATION (with the help ofiText Logic)
            String buyerName = buyerNameLabel.getText().replace("[", "").replace("]", "");
            String fileName = "Bill_" + buyerName.replace(" ", "_") + "_" + System.currentTimeMillis() + ".pdf";

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();
            
            // Add Header Info
            document.add(new Paragraph("--- BILLING MANAGEMENT SYSTEM ---"));
            document.add(new Paragraph("Date: " + dateLabel.getText() + " | " + timeLabel.getText()));
            document.add(new Paragraph("Buyer: " + buyerName + " (Contact: " + buyerContactField.getText() + ")"));
            document.add(new Paragraph("----------------------------------------------------------------------------------------------------------------------------------"));

            // Add Table Data
            PdfPTable pdfTable = new PdfPTable(model.getColumnCount());
            for (int i = 0; i < model.getColumnCount(); i++) {
                pdfTable.addCell(model.getColumnName(i));
            }
            for (int rows = 0; rows < model.getRowCount(); rows++) {
                for (int cols = 0; cols < model.getColumnCount(); cols++) {
                    pdfTable.addCell(model.getValueAt(rows, cols).toString());
                }
            }
            document.add(pdfTable);

            // Add Footer Totals
            document.add(new Paragraph("----------------------------------------------------------------------------------------------------------------------------------"));
            document.add(new Paragraph("GRAND TOTAL: Rs. " + String.format("%.2f", grandTotal)));
            document.add(new Paragraph("PAID: Rs. " + String.format("%.2f", paid)));
            document.add(new Paragraph("RETURNED: Rs. " + String.format("%.2f", returned)));
            document.add(new Paragraph("\nThank you for visiting! Please come again."));
            
            document.close();
            
            JOptionPane.showMessageDialog(this, "Bill generated and transaction recorded successfully!\nFile: " + fileName, "Success", JOptionPane.INFORMATION_MESSAGE);

            // Close and re-open to clear the form
            this.dispose();
            new Billing().setVisible(true);


        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Paid Amount must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An error occurred during saving/printing: " + ex.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace(); // Print stack trace for debugging
        }
    }
    
    // --- METHOD 3: BUYER NAME  ---
    private void findBuyerName() {
        String contactNumber = buyerContactField.getText().trim();
        if (contactNumber.isEmpty()) {
            buyerNameLabel.setText("[Buyer Name]");
            return;
        }
        
        Connection con = DatabaseConnection.getConnection();
        if (con != null) {
            try {
                String sql = "SELECT name FROM buyer WHERE contactNumber = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, contactNumber);
                
                ResultSet rs = ps.executeQuery();
                
                if (rs.next()) {
                    buyerNameLabel.setText(rs.getString("name"));
                } else {
                    buyerNameLabel.setText("Not Found");
                }
                con.close();
                
            } catch (SQLException ex) {
                System.err.println("SQL Error during buyer lookup: " + ex.getMessage());
                buyerNameLabel.setText("DB Error");
            }
        } else {
           
            JOptionPane.showMessageDialog(this, "Database connection failed during Buyer Lookup.", "DB Error", JOptionPane.ERROR_MESSAGE);
            buyerNameLabel.setText("[Connection Failed]");
        }
    }
}