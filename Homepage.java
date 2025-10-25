import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;

public class Homepage extends JFrame {

    private Image backgroundImage;
    private final String IMAGE_PATH = "images/images.jpeg"; 
    
    public Homepage() {
        setTitle("Welcome Home");
      
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 600); 
        setLocationRelativeTo(null); 
        

        try {
            backgroundImage = ImageIO.read(new File(IMAGE_PATH)); 
        } catch (IOException e) {
            System.err.println("Homepage background image not found at: " + IMAGE_PATH);
        }

        JPanel contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this); 
                }
            }
        };
        contentPane.setLayout(null);
        setContentPane(contentPane);

       
        
        JLabel titleLabel = new JLabel("Welcome to the Homepage");
        titleLabel.setBounds(100, 30, 300, 30);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.YELLOW); 
        contentPane.add(titleLabel);

        // Declare all 8 buttons
        JButton viewProfileButton = new JButton("View Profile");
        JButton settingsButton = new JButton("Settings");
        JButton logoutButton = new JButton("Logout"); 
        JButton billingButton = new JButton("Billing / Generate Bill"); 
        JButton addBuyerButton = new JButton("Add New Buyer");
        JButton deleteBuyerButton = new JButton("Delete Buyer"); 
        JButton viewPastBillsButton = new JButton("View Past Bills"); 
        
      
        JButton viewAllRecordsButton = new JButton("View All Records"); 
        
      
        int btnWidth = 200; 
        int xPos = 150;
        int yOffset = 45; // Vertical spacing

       
        viewProfileButton.setBounds(xPos, 80, btnWidth, 35);
        settingsButton.setBounds(xPos, 80 + yOffset, btnWidth, 35);
        
        billingButton.setBounds(xPos, 80 + 2*yOffset, btnWidth, 35); 
        
        logoutButton.setBounds(xPos, 80 + 3*yOffset, btnWidth, 35); 
        
        addBuyerButton.setBounds(xPos, 80 + 4*yOffset, btnWidth, 35); 
        deleteBuyerButton.setBounds(xPos, 80 + 5*yOffset, btnWidth, 35); 
        
        viewPastBillsButton.setBounds(xPos, 80 + 6*yOffset, btnWidth, 35); 
        
       
        viewAllRecordsButton.setBounds(xPos, 80 + 7*yOffset, btnWidth, 35); 

        contentPane.add(viewProfileButton);
        contentPane.add(settingsButton);
        contentPane.add(logoutButton);
        contentPane.add(billingButton); 
        contentPane.add(addBuyerButton);
        contentPane.add(deleteBuyerButton);
        contentPane.add(viewPastBillsButton);
        contentPane.add(viewAllRecordsButton);

      
        
      
        viewAllRecordsButton.addActionListener(e -> {
            new ViewRecords().setVisible(true); // Opens the new ViewRecords Form
        });
        
     
        billingButton.addActionListener(e -> new Billing().setVisible(true));
        viewPastBillsButton.addActionListener(e -> new BillPreview().setVisible(true)); 

      
        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int a = JOptionPane.showConfirmDialog(Homepage.this, "Do you really want to log out?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
                if (a == JOptionPane.YES_OPTION) { 
                    dispose(); 
                    new LoginForm().setVisible(true); 
                }
            }
        });
        
       
        addBuyerButton.addActionListener(e -> new AddBuyer().setVisible(true));
        deleteBuyerButton.addActionListener(e -> new DeleteBuyer().setVisible(true));

        viewProfileButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Opening View Profile Page...", "Navigation", JOptionPane.INFORMATION_MESSAGE));
        settingsButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Opening Settings Page...", "Navigation", JOptionPane.INFORMATION_MESSAGE));


        setVisible(true);
    }
}