package ui;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import Database.DatabaseManager;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class VehicleExit extends JFrame {
    private DatabaseManager dbManager;
    private JTextField searchField;
    private JPanel resultsPanel;

    public VehicleExit() {
        dbManager = DatabaseManager.getInstance();
        
        setTitle("Vehicle Exit");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(255, 240, 245));
        setLayout(new BorderLayout(20, 20));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(255, 240, 245));
        
        JButton backButton = new JButton("← Back to Dashboard");
        backButton.setFocusPainted(false);
        backButton.setBackground(new Color(230, 240, 230));
        backButton.setFont(new Font("SansSerif", Font.PLAIN, 16));
        backButton.setPreferredSize(new Dimension(200, 40));
        backButton.addActionListener(e -> {
            dispose();
            new SmartParkingDashboard();
        });
        headerPanel.add(backButton, BorderLayout.WEST);

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(255, 240, 245));
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Vehicle Exit");
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel subtitle = new JLabel("Process vehicle exit and calculate parking charges");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 18));
        subtitle.setForeground(Color.DARK_GRAY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(8));
        titlePanel.add(subtitle);
        headerPanel.add(titlePanel, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // Main Panel with ScrollPane
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(255, 240, 245));

        // Find Vehicle Card
        JPanel findPanel = new JPanel();
        findPanel.setBackground(Color.WHITE);
        findPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
            BorderFactory.createEmptyBorder(24, 24, 24, 24)));
        findPanel.setLayout(new BoxLayout(findPanel, BoxLayout.Y_AXIS));
        findPanel.setMaximumSize(new Dimension(800, 140));
        findPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel findLabel = new JLabel("Find Vehicle");
        findLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        findLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        findPanel.add(findLabel);
        findPanel.add(Box.createVerticalStrut(8));
        
        JLabel findDesc = new JLabel("Search by vehicle number or slot number");
        findDesc.setFont(new Font("SansSerif", Font.PLAIN, 15));
        findDesc.setAlignmentX(Component.CENTER_ALIGNMENT);
        findPanel.add(findDesc);
        findPanel.add(Box.createVerticalStrut(16));

        JPanel searchBarPanel = new JPanel(new BorderLayout(10, 0));
        searchBarPanel.setOpaque(false);
        searchField = new JTextField();
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        searchField.setPreferredSize(new Dimension(500, 40));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
            BorderFactory.createEmptyBorder(8, 16, 8, 16)));
        searchBarPanel.add(searchField, BorderLayout.CENTER);
        
        JButton searchButton = new JButton("Search");
        searchButton.setBackground(new Color(255, 120, 120));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        searchButton.setFocusPainted(false);
        searchButton.setPreferredSize(new Dimension(100, 40));
        searchButton.addActionListener(e -> searchVehicle());
        searchBarPanel.add(searchButton, BorderLayout.EAST);
        findPanel.add(searchBarPanel);

        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(findPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Results Panel
        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBackground(new Color(255, 240, 245));
        resultsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(resultsPanel);

        // Load all parked vehicles initially
        loadAllParkedVehicles();

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadAllParkedVehicles() {
        resultsPanel.removeAll();
        
        JLabel quickLabel = new JLabel("Currently Parked Vehicles");
        quickLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        quickLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultsPanel.add(quickLabel);
        resultsPanel.add(Box.createVerticalStrut(20));

        try {
            ResultSet rs = dbManager.getAllParkedVehicles();
            if (rs != null) {
                boolean hasVehicles = false;
                while (rs.next()) {
                    hasVehicles = true;
                    String vehicleNumber = rs.getString("vehicle_number");
                    String ownerName = rs.getString("owner_name");
                    String vehicleType = rs.getString("vehicle_type");
                    String slot = rs.getString("slot");
                    Timestamp entryTime = rs.getTimestamp("entry_time");
                    
                    resultsPanel.add(createVehicleCard(vehicleNumber, ownerName, vehicleType, 
                                                       slot, entryTime));
                    resultsPanel.add(Box.createVerticalStrut(15));
                }
                
                if (!hasVehicles) {
                    JLabel noVehicles = new JLabel("No vehicles currently parked");
                    noVehicles.setFont(new Font("SansSerif", Font.PLAIN, 16));
                    noVehicles.setForeground(Color.GRAY);
                    noVehicles.setAlignmentX(Component.CENTER_ALIGNMENT);
                    resultsPanel.add(noVehicles);
                }
                rs.close();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading vehicles: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    private void searchVehicle() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            loadAllParkedVehicles();
            return;
        }

        resultsPanel.removeAll();
        
        JLabel searchLabel = new JLabel("Search Results for: " + query);
        searchLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        searchLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultsPanel.add(searchLabel);
        resultsPanel.add(Box.createVerticalStrut(20));

        try {
            ResultSet rs = dbManager.getVehicleByNumber(query);
            
            // Also try by slot if not found by vehicle number
            if (rs == null || !rs.next()) {
                rs = searchBySlot(query);
            }
            
            if (rs != null && rs.next()) {
                String vehicleNumber = rs.getString("vehicle_number");
                String ownerName = rs.getString("owner_name");
                String vehicleType = rs.getString("vehicle_type");
                String slot = rs.getString("slot");
                Timestamp entryTime = rs.getTimestamp("entry_time");
                
                resultsPanel.add(createVehicleCard(vehicleNumber, ownerName, vehicleType, 
                                                   slot, entryTime));
            } else {
                JLabel noResults = new JLabel("No vehicle found with number or slot: " + query);
                noResults.setFont(new Font("SansSerif", Font.PLAIN, 16));
                noResults.setForeground(Color.RED);
                noResults.setAlignmentX(Component.CENTER_ALIGNMENT);
                resultsPanel.add(noResults);
            }
            
            if (rs != null) rs.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Search failed: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    private ResultSet searchBySlot(String slot) {
        try {
            String sql = "SELECT * FROM entries WHERE slot = ? AND status = 'PARKED'";
            PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql);
            pstmt.setString(1, slot);
            return pstmt.executeQuery();
        } catch (SQLException e) {
            return null;
        }
    }

    private JPanel createVehicleCard(String vehicleNumber, String ownerName, 
                                    String vehicleType, String slot, Timestamp entryTime) {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)));
        card.setLayout(new BorderLayout(15, 15));
        card.setMaximumSize(new Dimension(800, 180));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Left Panel - Vehicle Icon
        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        
        String icon = getVehicleIcon(vehicleType);
        Color iconColor = getVehicleColor(vehicleType);
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconLabel.setForeground(iconColor);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(iconLabel);
        
        JLabel typeLabel = new JLabel(vehicleType);
        typeLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        typeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(typeLabel);
        
        card.add(leftPanel, BorderLayout.WEST);

        // Center Panel - Vehicle Details
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new GridLayout(4, 1, 5, 5));
        
        JLabel numLabel = new JLabel("Vehicle Number: " + vehicleNumber);
        numLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        centerPanel.add(numLabel);
        
        JLabel ownerLabel = new JLabel("Owner: " + ownerName);
        ownerLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        centerPanel.add(ownerLabel);
        
        JLabel slotLabel = new JLabel("Slot: " + slot);
        slotLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        centerPanel.add(slotLabel);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
        JLabel entryLabel = new JLabel("Entry Time: " + entryTime.toLocalDateTime().format(formatter));
        entryLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        centerPanel.add(entryLabel);
        
        card.add(centerPanel, BorderLayout.CENTER);

        // Right Panel - Duration and Exit Button
        JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(false);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        
        long hours = calculateParkingHours(entryTime);
        double charge = calculateCharge(hours);
        
        JLabel durationLabel = new JLabel("Duration: " + hours + " hrs");
        durationLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        durationLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(durationLabel);
        
        rightPanel.add(Box.createVerticalStrut(5));
        
        JLabel chargeLabel = new JLabel("Charge: ₹" + charge);
        chargeLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        chargeLabel.setForeground(new Color(0, 153, 51));
        chargeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(chargeLabel);
        
        rightPanel.add(Box.createVerticalStrut(10));
        
        JButton exitButton = new JButton("Process Exit");
        exitButton.setBackground(new Color(255, 69, 69));
        exitButton.setForeground(Color.WHITE);
        exitButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        exitButton.setFocusPainted(false);
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.setPreferredSize(new Dimension(140, 40));
        exitButton.addActionListener(e -> processExit(vehicleNumber, slot, hours, charge));
        rightPanel.add(exitButton);
        
        card.add(rightPanel, BorderLayout.EAST);

        return card;
    }

    private String getVehicleIcon(String type) {
        switch (type.toLowerCase()) {
            case "car": return "🚗";
            case "bike": return "🏍️";
            case "truck": return "🚚";
            default: return "🚗";
        }
    }

    private Color getVehicleColor(String type) {
        switch (type.toLowerCase()) {
            case "car": return new Color(33, 150, 243);
            case "bike": return new Color(76, 175, 80);
            case "truck": return new Color(156, 39, 176);
            default: return new Color(33, 150, 243);
        }
    }

    private long calculateParkingHours(Timestamp entryTime) {
        LocalDateTime entry = entryTime.toLocalDateTime();
        LocalDateTime now = LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(entry, now);
        return Math.max(1, (minutes + 59) / 60); // Round up to nearest hour, minimum 1 hour
    }

    private double calculateCharge(long hours) {
        // Simple pricing: ₹20 per hour
        return hours * 20.0;
    }

    private void processExit(String vehicleNumber, String slot, long hours, double charge) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Proceed to payment for vehicle: " + vehicleNumber + "?\n\n" +
            "Parking Duration: " + hours + " hours\n" +
            "Total Charge: ₹" + charge,
            "Proceed to Payment", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Get owner name for payment page
            String ownerName = "";
            try {
                ResultSet rs = dbManager.getVehicleByNumber(vehicleNumber);
                if (rs != null && rs.next()) {
                    ownerName = rs.getString("owner_name");
                    rs.close();
                }
            } catch (SQLException e) {
                ownerName = "Customer";
            }
            
            // Close current window and open payment page
            dispose();
            new PaymentPage(vehicleNumber, ownerName, slot, hours, charge);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VehicleExit().setVisible(true));
    }
}