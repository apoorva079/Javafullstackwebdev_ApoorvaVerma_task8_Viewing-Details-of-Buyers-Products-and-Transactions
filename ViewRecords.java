import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class ViewRecords extends JFrame {

    private JTabbedPane tabbedPane;

    public ViewRecords() {
        setTitle("View All Records");
        setSize(850, 500); 
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        tabbedPane = new JTabbedPane();
        
        // 1. Add Buyers Tab
        tabbedPane.addTab("Buyers Details", createBuyersPanel());
        
        // 2. Add Transactions Tab
        tabbedPane.addTab("Transactions Summary", createTransactionsPanel());
        
        // 3. Add Products Tab here if you have a product table
        
        add(tabbedPane);
        
        setVisible(true);
    }
    
    // --- Method to create the Buyers Tab (JTable) ---
    private JPanel createBuyersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Table Model Setup (Matching the buyer table columns)
        DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Name", "Contact No.", "Email", "Address", "Gender"}, 0
        );
        JTable buyerTable = new JTable(model);
        
        // --- SQL Logic to Fetch Buyer Data ---
        Connection con = DatabaseConnection.getConnection();
        if (con != null) {
            try {
                // SQL query to fetch data required 
                String sql = "SELECT name, contactNumber, email, address, gender FROM buyer";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql);
                
                // Populate the table model
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("name"),
                        rs.getString("contactNumber"),
                        rs.getString("email"),
                        rs.getString("address"),
                        rs.getString("gender")
                    });
                }
                con.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error loading buyer data: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        //  Search Bar Placeholder
        JTextField searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(800, 30));
        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Search Buyer:"));
        searchPanel.add(searchField);
        
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(buyerTable), BorderLayout.CENTER);
        return panel;
    }
    
    // --- Method to create the Transactions Tab (JTable) ---
    private JPanel createTransactionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table Model Setup (Matching the transactions table columns)
        DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Buyer Name", "Date", "Total Amount", "Paid", "Returned"}, 0
        );
        JTable transactionTable = new JTable(model);
        
        // --- SQL Logic to Fetch Transaction Data ---
        Connection con = DatabaseConnection.getConnection();
        if (con != null) {
            try {
                // SQL query to fetch data required 
                String sql = "SELECT transactionID, buyerName, date, totalAmount, paidAmount, returnedAmount FROM transactions ORDER BY transactionID DESC";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql);
                
                // Populate the table model
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("transactionID"),
                        rs.getString("buyerName"),
                        rs.getString("date"),
                        String.format("%.2f", rs.getDouble("totalAmount")),
                        String.format("%.2f", rs.getDouble("paidAmount")),
                        String.format("%.2f", rs.getDouble("returnedAmount"))
                    });
                }
                con.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error loading transaction data: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        // Search Bar Placeholder 
        JTextField searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(800, 30));
        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Search Transaction:"));
        searchPanel.add(searchField);
        
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(transactionTable), BorderLayout.CENTER);
        return panel;
    }
}