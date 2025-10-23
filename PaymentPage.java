package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import Database.DatabaseManager;

public class PaymentPage extends JFrame implements ActionListener {
    private Container container;
    private JLabel titleLabel;
    private JLabel cardNoLabel, expiryLabel, cvvLabel, nameLabel, amountLabel;
    private JLabel vehicleInfoLabel, durationLabel;
    private JTextField cardNoField, expiryField, nameField, amountField;
    private JPasswordField cvvField;
    private JButton payButton, cancelButton;
    private JLabel msgLabel;
    
    // Payment details
    private String vehicleNumber;
    private String ownerName;
    private String slot;
    private long parkingHours;
    private double amountDue;
    private DatabaseManager dbManager;

    public PaymentPage(String vehicleNumber, String ownerName, String slot, 
                      long parkingHours, double amountDue) {
        this.vehicleNumber = vehicleNumber;
        this.ownerName = ownerName;
        this.slot = slot;
        this.parkingHours = parkingHours;
        this.amountDue = amountDue;
        this.dbManager = DatabaseManager.getInstance();
        
        setTitle("Payment - Parking Charges");
        setBounds(400, 150, 550, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        container = getContentPane();
        container.setLayout(null);
        container.setBackground(new Color(240, 255, 245));

        // Title
        titleLabel = new JLabel("Payment Details");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setBounds(180, 20, 250, 35);
        container.add(titleLabel);

        // Parking Details Section
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(null);
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 230, 200), 2),
            "Parking Details",
            0, 0,
            new Font("SansSerif", Font.BOLD, 14),
            new Color(0, 102, 51)
        ));
        detailsPanel.setBounds(40, 70, 460, 120);
        
        vehicleInfoLabel = new JLabel("Vehicle: " + vehicleNumber + " | Owner: " + ownerName);
        vehicleInfoLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        vehicleInfoLabel.setBounds(20, 25, 420, 25);
        detailsPanel.add(vehicleInfoLabel);
        
        JLabel slotLabel = new JLabel("Parking Slot: " + slot);
        slotLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        slotLabel.setBounds(20, 55, 200, 25);
        detailsPanel.add(slotLabel);
        
        durationLabel = new JLabel("Duration: " + parkingHours + " hour(s)");
        durationLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        durationLabel.setBounds(240, 55, 200, 25);
        detailsPanel.add(durationLabel);
        
        JLabel chargeLabel = new JLabel("Total Charges: ₹" + String.format("%.2f", amountDue));
        chargeLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        chargeLabel.setForeground(new Color(0, 153, 51));
        chargeLabel.setBounds(20, 85, 300, 25);
        detailsPanel.add(chargeLabel);
        
        container.add(detailsPanel);

        // Payment Form Section
        int startY = 210;
        
        // Name on Card
        nameLabel = new JLabel("Name on Card:");
        nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        nameLabel.setBounds(80, startY, 120, 25);
        container.add(nameLabel);

        nameField = new JTextField(ownerName);
        nameField.setBounds(230, startY, 220, 30);
        nameField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        nameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 220, 180), 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        container.add(nameField);

        // Card Number
        cardNoLabel = new JLabel("Card Number:");
        cardNoLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        cardNoLabel.setBounds(80, startY + 45, 120, 25);
        container.add(cardNoLabel);

        cardNoField = new JTextField();
        cardNoField.setBounds(230, startY + 45, 220, 30);
        cardNoField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        cardNoField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 220, 180), 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        container.add(cardNoField);

        // Expiry Date
        expiryLabel = new JLabel("Expiry (MM/YY):");
        expiryLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        expiryLabel.setBounds(80, startY + 90, 120, 25);
        container.add(expiryLabel);

        expiryField = new JTextField();
        expiryField.setBounds(230, startY + 90, 100, 30);
        expiryField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        expiryField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 220, 180), 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        container.add(expiryField);

        // CVV
        cvvLabel = new JLabel("CVV:");
        cvvLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        cvvLabel.setBounds(350, startY + 90, 50, 25);
        container.add(cvvLabel);

        cvvField = new JPasswordField();
        cvvField.setBounds(400, startY + 90, 50, 30);
        cvvField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        cvvField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 220, 180), 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        container.add(cvvField);

        // Amount (read-only)
        amountLabel = new JLabel("Amount to Pay:");
        amountLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        amountLabel.setBounds(80, startY + 135, 120, 25);
        container.add(amountLabel);

        amountField = new JTextField("₹ " + String.format("%.2f", amountDue));
        amountField.setBounds(230, startY + 135, 120, 30);
        amountField.setFont(new Font("SansSerif", Font.BOLD, 16));
        amountField.setForeground(new Color(0, 153, 51));
        amountField.setEditable(false);
        amountField.setBackground(new Color(240, 255, 240));
        amountField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 153, 51), 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        container.add(amountField);

        // Buttons
        payButton = new JButton("Process Payment");
        payButton.setBounds(120, startY + 190, 150, 40);
        payButton.setBackground(new Color(0, 153, 51));
        payButton.setForeground(Color.WHITE);
        payButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        payButton.setFocusPainted(false);
        payButton.addActionListener(this);
        container.add(payButton);

        cancelButton = new JButton("Cancel");
        cancelButton.setBounds(290, startY + 190, 150, 40);
        cancelButton.setBackground(new Color(220, 20, 60));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(this);
        container.add(cancelButton);

        // Message Label
        msgLabel = new JLabel("");
        msgLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        msgLabel.setForeground(Color.RED);
        msgLabel.setBounds(80, startY + 240, 400, 25);
        msgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        container.add(msgLabel);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == payButton) {
            String name = nameField.getText().trim();
            String cardNo = cardNoField.getText().trim();
            String expiry = expiryField.getText().trim();
            String cvv = new String(cvvField.getPassword()).trim();

            // Validation
            if (name.isEmpty() || cardNo.isEmpty() || expiry.isEmpty() || cvv.isEmpty()) {
                msgLabel.setForeground(Color.RED);
                msgLabel.setText("Please fill all fields.");
                return;
            }

            if (!cardNo.matches("\\d{16}")) {
                msgLabel.setForeground(Color.RED);
                msgLabel.setText("Card number must be 16 digits.");
                return;
            }

            if (!expiry.matches("(0[1-9]|1[0-2])/\\d{2}")) {
                msgLabel.setForeground(Color.RED);
                msgLabel.setText("Expiry format: MM/YY (e.g., 12/25)");
                return;
            }

            if (!cvv.matches("\\d{3}")) {
                msgLabel.setForeground(Color.RED);
                msgLabel.setText("CVV must be 3 digits.");
                return;
            }

            // Process payment
            boolean paymentSuccess = processPayment(name, cardNo, expiry, cvv);
            
            if (paymentSuccess) {
                // Mark vehicle as exited in database
                boolean exitSuccess = dbManager.markVehicleExit(vehicleNumber);
                
                if (exitSuccess) {
                    msgLabel.setForeground(new Color(34, 139, 34));
                    msgLabel.setText("Payment successful!");
                    
                    // Show success message
                    JOptionPane.showMessageDialog(this,
                        "Payment Successful!\n\n" +
                        "Vehicle: " + vehicleNumber + "\n" +
                        "Amount Paid: ₹" + String.format("%.2f", amountDue) + "\n" +
                        "Slot " + slot + " is now free.\n\n" +
                        "Thank you for using our parking service!",
                        "Payment Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    dispose();
                    // Return to dashboard
                    new SmartParkingDashboard();
                } else {
                    msgLabel.setForeground(Color.RED);
                    msgLabel.setText("Payment received but exit processing failed!");
                }
            } else {
                msgLabel.setForeground(Color.RED);
                msgLabel.setText("Payment failed. Please try again.");
            }

        } else if (e.getSource() == cancelButton) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel the payment?",
                "Cancel Payment",
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                // Return to exit form
                new VehicleExit().setVisible(true);
            }
        }
    }

    // Simulate payment processing
    private boolean processPayment(String name, String cardNo, String expiry, String cvv) {
        // Simulate processing delay
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // For demo: Payment succeeds if card is not all zeros
        // In real app, this would connect to payment gateway
        return !cardNo.equals("0000000000000000");
    }

    // For testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PaymentPage("ABC123", "John Doe", "A5", 3, 60.00);
        });
    }
}