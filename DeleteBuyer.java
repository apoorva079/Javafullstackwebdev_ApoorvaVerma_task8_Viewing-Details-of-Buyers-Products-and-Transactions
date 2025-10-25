import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class DeleteBuyer extends JFrame {

    private JTextField searchField, nameField, emailField, addressField;
    private JButton searchButton;

    public DeleteBuyer() {
        setTitle("Delete Buyer Record");
        setSize(500, 400);
        setLayout(null);
        setLocationRelativeTo(null);
        
        // --- UI Setup ---
        JLabel title = new JLabel("Search & Delete Buyer");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setBounds(150, 20, 250, 30);
        add(title);

        // Search Section
        add(new JLabel("Contact No:")).setBounds(50, 70, 100, 30);
        searchField = new JTextField();
        searchField.setBounds(150, 70, 180, 30);
        add(searchField);
        
        searchButton = new JButton("Search");
        searchButton.setBounds(340, 70, 100, 30);
        add(searchButton);

        // Data Display Fields (Non-editable)
        int y = 120;
        nameField = createDataField("Name:", y, false); 
        emailField = createDataField("Email:", y += 40, false);
        addressField = createDataField("Address:", y += 40, false);
        
        // Buttons
        JButton deleteButton = new JButton("Delete");
        JButton closeButton = new JButton("Close");
        deleteButton.setBounds(150, y + 60, 100, 30);
        closeButton.setBounds(260, y + 60, 100, 30);
        add(deleteButton);
        add(closeButton);

        // --- Logic ---
        searchButton.addActionListener(this::searchBuyer);
        deleteButton.addActionListener(this::deleteBuyer);
        closeButton.addActionListener(e -> dispose());
        
        setVisible(true);
    }
    
    // Helper method for clean UI component creation
    private JTextField createDataField(String label, int y, boolean editable) {
        add(new JLabel(label)).setBounds(50, y, 100, 30);
        JTextField field = new JTextField();
        field.setBounds(150, y, 250, 30);
        field.setEditable(editable);
        add(field);
        return field;
    }

    // --- Search Buyer Logic (SQL SELECT) ---
    private void searchBuyer(ActionEvent e) {
        // Reset fields first
        nameField.setText("");
        emailField.setText("");
        addressField.setText("");

        String contactNumber = searchField.getText();
        Connection con = DatabaseConnection.getConnection();
        if (con != null && !contactNumber.isEmpty()) {
            try {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM buyer WHERE contactNumber = '" + contactNumber + "'");
                
                if (rs.next()) {
                    // Display found record data
                    nameField.setText(rs.getString("name"));
                    emailField.setText(rs.getString("email"));
                    addressField.setText(rs.getString("address"));
                } else {
                    JOptionPane.showMessageDialog(this, "Contact Number does not exist!");
                }
                con.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Search Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // --- Delete Buyer Logic (SQL DELETE) ---
    private void deleteBuyer(ActionEvent e) {
        String contactNumber = searchField.getText();
        if (contactNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please search for a buyer first.");
            return;
        }

        int confirmation = JOptionPane.showConfirmDialog(this, "Do you really want to delete this buyer?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            Connection con = DatabaseConnection.getConnection();
            if (con != null) {
                try {
                    Statement st = con.createStatement();
                    // Execute the DELETE query
                    st.executeUpdate("DELETE FROM buyer WHERE contactNumber = '" + contactNumber + "'");
                    
                    JOptionPane.showMessageDialog(this, "Record Successfully Deleted!");
                    
                    // Refresh form
                    this.dispose();
                    new DeleteBuyer().setVisible(true);
                    con.close();

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error during deletion: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}