import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.io.FileOutputStream;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class BillPreview extends JFrame {

    // --- 1. DECLARATION ---
    private JTextField transactionIdField;
    private JLabel buyerNameLabel, contactLabel, dateLabel, totalLabel, paidLabel, returnedLabel;
    private JButton searchButton, exportPdfButton, closeButton;

    public BillPreview() {
        setTitle("Search & Preview Bill");
        setSize(550, 400); 
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // --- UI SETUP & POSITIONING ---
        
        JLabel title = new JLabel("Search Past Transactions");
        title.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 20));
        title.setBounds(150, 20, 250, 30);
        add(title);
        
        // Search Input
        add(new JLabel("Transaction ID:")).setBounds(50, 70, 100, 25);
        transactionIdField = new JTextField();
        transactionIdField.setBounds(160, 70, 120, 25);
        add(transactionIdField);
        
        searchButton = new JButton("Search");
        searchButton.setBounds(290, 70, 100, 25);
        add(searchButton);

        // Data Display Area
        int y = 120;
        buyerNameLabel = createReadOnlyLabel("Buyer Name:", y);
        contactLabel = createReadOnlyLabel("Contact:", y += 30);
        dateLabel = createReadOnlyLabel("Date:", y += 30);
        
        totalLabel = createReadOnlyLabel("Grand Total:", y += 50);
        paidLabel = createReadOnlyLabel("Paid Amount:", y += 30);
        returnedLabel = createReadOnlyLabel("Returned:", y += 30);
        
        // Final Buttons
        exportPdfButton = new JButton("Export to PDF");
        closeButton = new JButton("Close");
        
        exportPdfButton.setBounds(50, 300, 150, 35);
        closeButton.setBounds(210, 300, 100, 35);
        
        add(exportPdfButton);
        add(closeButton);

        // --- LOGIC IMPLEMENTATION ---
        
        searchButton.addActionListener(e -> searchTransaction());
        exportPdfButton.addActionListener(e -> exportBillToPdf());
        closeButton.addActionListener(e -> dispose());
        
        setVisible(true);
    }
    
    // Helper method to create static data display fields
    private JLabel createReadOnlyLabel(String labelText, int y) {
        add(new JLabel(labelText)).setBounds(50, y, 100, 20);
        JLabel valueLabel = new JLabel("---");
        valueLabel.setBounds(160, y, 300, 20);
        add(valueLabel);
        return valueLabel;
    }

    // --- METHOD 1: SEARCH TRANSACTION (SQL SELECT) ---
    private void searchTransaction() {
        String transactionId = transactionIdField.getText().trim();
        if (transactionId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Transaction ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Connection con = DatabaseConnection.getConnection();
        if (con != null) {
            try {
                String sql = "SELECT * FROM transactions WHERE transactionID = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, transactionId);
                
                ResultSet rs = ps.executeQuery();
                
                if (rs.next()) {
                    // Update display labels with retrieved data
                    buyerNameLabel.setText(rs.getString("buyerName"));
                    contactLabel.setText(rs.getString("contactNumber"));
                    dateLabel.setText(rs.getString("date"));
                    totalLabel.setText("Rs. " + String.format("%.2f", rs.getDouble("totalAmount")));
                    paidLabel.setText("Rs. " + String.format("%.2f", rs.getDouble("paidAmount")));
                    returnedLabel.setText("Rs. " + String.format("%.2f", rs.getDouble("returnedAmount")));
                } else {
                    JOptionPane.showMessageDialog(this, "Transaction ID not found.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
                    // Clear fields if not found
                    buyerNameLabel.setText("---"); totalLabel.setText("---"); 
                }
                con.close();
                
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "SQL Error during search: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // --- METHOD 2: EXPORT TO PDF (Reusing Billing Logic) ---
    private void exportBillToPdf() {
     
        if (totalLabel.getText().equals("---")) {
            JOptionPane.showMessageDialog(this, "Please search and load a transaction first.", "Export Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String buyerName = buyerNameLabel.getText();
            String transactionId = transactionIdField.getText();
            String fileName = "Bill_Ref_" + transactionId + "_" + System.currentTimeMillis() + ".pdf";

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();
            
            // Add Header Info
            document.add(new Paragraph("--- TASK 7: TRANSACTION BILL PREVIEW ---"));
            document.add(new Paragraph("Transaction ID: " + transactionId));
            document.add(new Paragraph("Date: " + dateLabel.getText()));
            document.add(new Paragraph("Buyer: " + buyerName));
            document.add(new Paragraph("Contact: " + contactLabel.getText()));
            document.add(new Paragraph("\n--------------------------------------------------------------"));
            
            // Add Financial Summary
            document.add(new Paragraph("\nFINAL SUMMARY:"));
            document.add(new Paragraph("GRAND TOTAL: " + totalLabel.getText()));
            document.add(new Paragraph("PAID AMOUNT: " + paidLabel.getText()));
            document.add(new Paragraph("RETURNED: " + returnedLabel.getText()));
            document.add(new Paragraph("\nThank you."));
            
            document.close();
            
            JOptionPane.showMessageDialog(this, "Bill summary exported successfully!\nFile: " + fileName, "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An error occurred during PDF export: " + ex.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}