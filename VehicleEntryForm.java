package ui;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import Database.DatabaseManager;

public class VehicleEntryForm extends JFrame {
    private DatabaseManager dbManager;

    private final JTextField vehicleNumberField, ownerNameField, phoneNumberField;
    private final JComboBox<String> vehicleTypeBox;
    private final JButton registerButton;
    private JButton[] slotButtons;
    private boolean[] slotOccupied;
    private String selectedSlot = "";
    private String[] allSlots; // Array to hold all 50 slots

    public VehicleEntryForm() {
        // Get database manager instance
        dbManager = DatabaseManager.getInstance();
        
        // Initialize all 50 slots
        initializeAllSlots();
        
        // Load occupied slots from database
        loadOccupiedSlots();
        
        setTitle("Vehicle Entry");
        setSize(900, 750); // Increased size to accommodate all slots
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(20, 20));
        getContentPane().setBackground(new Color(240, 255, 245));

        // Back Button + Title Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(240, 255, 245));

        JButton backButton = new JButton("← Back to Dashboard");
        backButton.setFocusPainted(false);
        backButton.setBackground(new Color(230, 240, 230));
        backButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        backButton.setPreferredSize(new Dimension(180, 40));
        backButton.addActionListener(e -> {
            dispose(); // Close this window
            new SmartParkingDashboard(); // Open dashboard
        });
        headerPanel.add(backButton, BorderLayout.WEST);

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(240, 255, 245));
        titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        JLabel title = new JLabel("Vehicle Entry");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(title);

        JLabel subtitle = new JLabel("Register a new vehicle entering the parking lot");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(Color.DARK_GRAY);
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel subtitlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        subtitlePanel.setBackground(new Color(240, 255, 245));
        subtitlePanel.add(subtitle);

        JPanel titleContainer = new JPanel(new GridLayout(2, 1));
        titleContainer.setBackground(new Color(240, 255, 245));
        titleContainer.add(titlePanel);
        titleContainer.add(subtitlePanel);

        headerPanel.add(titleContainer, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);

        // Create wrapper panel with scroll for the entire form
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(new Color(240, 255, 245));
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 230, 200), 1, true),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel sectionLabel = new JLabel("Vehicle Information");
        sectionLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(sectionLabel, gbc);

        JLabel descLabel = new JLabel("Enter the details of the vehicle entering the parking lot");
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        descLabel.setForeground(Color.GRAY);
        gbc.gridy++;
        formPanel.add(descLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++; gbc.gridx = 0;
        formPanel.add(new JLabel("Vehicle Number *"), gbc);
        gbc.gridx = 1;
        vehicleNumberField = new JTextField();
        vehicleNumberField.setPreferredSize(new Dimension(200, 32));
        vehicleNumberField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        vehicleNumberField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 220, 180), 2, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        formPanel.add(vehicleNumberField, gbc);

        gbc.gridy++; gbc.gridx = 0;
        formPanel.add(new JLabel("Vehicle Type *"), gbc);
        gbc.gridx = 1;
        vehicleTypeBox = new JComboBox<>(new String[]{"Car", "Bike", "Truck"});
        vehicleTypeBox.setPreferredSize(new Dimension(200, 32));
        vehicleTypeBox.setFont(new Font("SansSerif", Font.PLAIN, 15));
        vehicleTypeBox.setBackground(new Color(245, 255, 245));
        formPanel.add(vehicleTypeBox, gbc);

        gbc.gridy++; gbc.gridx = 0;
        formPanel.add(new JLabel("Owner Name *"), gbc);
        gbc.gridx = 1;
        ownerNameField = new JTextField();
        ownerNameField.setPreferredSize(new Dimension(200, 32));
        ownerNameField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        ownerNameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 220, 180), 2, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        formPanel.add(ownerNameField, gbc);

        gbc.gridy++; gbc.gridx = 0;
        formPanel.add(new JLabel("Phone Number (10 digits) *"), gbc);
        gbc.gridx = 1;
        phoneNumberField = new JTextField();
        phoneNumberField.setPreferredSize(new Dimension(200, 32));
        phoneNumberField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        phoneNumberField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 220, 180), 2, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        
        // Add input verification for phone number - ONLY digits and max 10
        phoneNumberField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!Character.isDigit(c) || phoneNumberField.getText().length() >= 10) {
                    evt.consume();
                }
            }
        });
        
        formPanel.add(phoneNumberField, gbc);

        gbc.gridy++; gbc.gridx = 0; gbc.gridwidth = 2;
        JLabel slotLabel = new JLabel("Select Parking Slot *");
        slotLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        formPanel.add(slotLabel, gbc);

        gbc.gridy++; gbc.gridx = 0; gbc.gridwidth = 2;
        
        // Create scrollable slot panel for all 50 slots - FIXED LAYOUT
        JPanel slotPanel = new JPanel(new GridLayout(10, 5, 8, 8)); // 10 rows x 5 columns = 50 slots
        slotPanel.setBackground(Color.WHITE);
        slotPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        slotButtons = new JButton[allSlots.length];
        
        for (int i = 0; i < allSlots.length; i++) {
            slotButtons[i] = new JButton(allSlots[i]);
            slotButtons[i].setFocusPainted(false);
            slotButtons[i].setFont(new Font("SansSerif", Font.BOLD, 11));
            slotButtons[i].setPreferredSize(new Dimension(65, 35));
            slotButtons[i].setMinimumSize(new Dimension(65, 35));
            
            // Set initial button state
            updateSlotButtonAppearance(i);
            
            int index = i;
            slotButtons[i].addActionListener(e -> selectSlot(index));
            slotPanel.add(slotButtons[i]);
        }
        
        // Wrap slot panel in scroll pane with proper size
        JScrollPane slotScrollPane = new JScrollPane(slotPanel);
        slotScrollPane.setPreferredSize(new Dimension(480, 220));
        slotScrollPane.setMinimumSize(new Dimension(480, 220));
        slotScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        slotScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        slotScrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(180, 220, 180), 2),
            "Available Slots (Green = Available, Red = Occupied, Blue = Selected)"
        ));
        slotScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.3;
        formPanel.add(slotScrollPane, gbc);

        gbc.gridy++; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;
        gbc.weighty = 0;
        registerButton = new JButton("Register Vehicle Entry");
        registerButton.setBackground(new Color(0, 153, 51));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFont(new Font("SansSerif", Font.BOLD, 17));
        registerButton.setFocusPainted(false);
        registerButton.setPreferredSize(new Dimension(220, 44));
        registerButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 153, 51), 2, true),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        registerButton.addActionListener(e -> registerVehicle());
        formPanel.add(registerButton, gbc);

        wrapperPanel.add(formPanel, BorderLayout.CENTER);
        add(wrapperPanel, BorderLayout.CENTER);
    }

    // Initialize all 50 slots
    private void initializeAllSlots() {
        allSlots = new String[50];
        int index = 0;
        
        // Section A: Slots 1-15
        for (int i = 1; i <= 15; i++) {
            allSlots[index++] = "A" + i;
        }
        
        // Section B: Slots 16-35
        for (int i = 16; i <= 35; i++) {
            allSlots[index++] = "B" + i;
        }
        
        // Section C: Slots 36-50
        for (int i = 36; i <= 50; i++) {
            allSlots[index++] = "C" + i;
        }
    }

    // Load occupied slots from database
    private void loadOccupiedSlots() {
        try {
            ResultSet rs = dbManager.getOccupiedSlots();
            if (rs != null) {
                // Initialize all slots as unoccupied
                slotOccupied = new boolean[allSlots.length];
                
                while (rs.next()) {
                    String occupiedSlot = rs.getString("slot");
                    for (int i = 0; i < allSlots.length; i++) {
                        if (allSlots[i].equals(occupiedSlot)) {
                            slotOccupied[i] = true;
                            break;
                        }
                    }
                }
                rs.close();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading occupied slots: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Update slot button appearance based on occupancy
    private void updateSlotButtonAppearance(int index) {
        if (slotOccupied[index]) {
            // Occupied slot - red (DISABLED)
            slotButtons[index].setBackground(new Color(255, 100, 100));
            slotButtons[index].setForeground(Color.WHITE);
            slotButtons[index].setEnabled(false);
            slotButtons[index].setToolTipText("This slot is occupied");
        } else if (allSlots[index].equals(selectedSlot)) {
            // Selected available slot - blue
            slotButtons[index].setBackground(new Color(100, 150, 255));
            slotButtons[index].setForeground(Color.WHITE);
            slotButtons[index].setEnabled(true);
            slotButtons[index].setToolTipText("Currently selected");
        } else {
            // Available slot - green
            slotButtons[index].setBackground(new Color(180, 255, 180));
            slotButtons[index].setForeground(Color.BLACK);
            slotButtons[index].setEnabled(true);
            slotButtons[index].setToolTipText("Click to select this slot");
        }
    }

    private void selectSlot(int index) {
        // FIXED: Check if slot is occupied (should never happen due to disabled button, but safety check)
        if (slotOccupied[index]) {
            JOptionPane.showMessageDialog(this, 
                "This slot is already occupied!\nPlease select an available slot.", 
                "Slot Occupied", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Clear previous selection
        String previousSlot = selectedSlot;
        selectedSlot = allSlots[index];
        
        // Update all button appearances
        for (int i = 0; i < slotButtons.length; i++) {
            updateSlotButtonAppearance(i);
        }
        
        System.out.println("Selected slot: " + selectedSlot);
    }

    private void registerVehicle() {
        String number = vehicleNumberField.getText().trim().toUpperCase();
        String owner = ownerNameField.getText().trim();
        String phone = phoneNumberField.getText().trim();
        String type = (String) vehicleTypeBox.getSelectedItem();

        // Validation
        if (number.isEmpty() || owner.isEmpty() || phone.isEmpty() || selectedSlot.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please fill all required fields marked with *!", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Phone number validation - MUST be exactly 10 digits
        if (phone.length() != 10) {
            JOptionPane.showMessageDialog(this, 
                "Phone number must be exactly 10 digits!", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        System.out.println("Attempting to register vehicle:");
        System.out.println("  Number: " + number);
        System.out.println("  Owner: " + owner);
        System.out.println("  Phone: " + phone);
        System.out.println("  Type: " + type);
        System.out.println("  Slot: " + selectedSlot);

        // Save to database using DatabaseManager
        boolean success = dbManager.insertVehicleEntry(number, owner, phone, type, selectedSlot);
        
        if (success) {
            System.out.println("✓ Registration successful!");
            
            // Update the slot as occupied
            for (int i = 0; i < allSlots.length; i++) {
                if (allSlots[i].equals(selectedSlot)) {
                    slotOccupied[i] = true;
                    updateSlotButtonAppearance(i);
                    break;
                }
            }
            
            JOptionPane.showMessageDialog(this,
                "Vehicle Registered Successfully!\n\n" +
                "Vehicle: " + number + "\n" +
                "Owner: " + owner + "\n" +
                "Phone: " + phone + "\n" +
                "Type: " + type + "\n" +
                "Slot: " + selectedSlot,
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Clear fields for next entry
            vehicleNumberField.setText("");
            ownerNameField.setText("");
            phoneNumberField.setText("");
            vehicleTypeBox.setSelectedIndex(0);
            selectedSlot = "";
            
            // Reset all available slots to green
            for (int i = 0; i < slotButtons.length; i++) {
                updateSlotButtonAppearance(i);
            }
            
        } else {
            System.err.println("✗ Registration failed!");
            JOptionPane.showMessageDialog(this, 
                "Failed to register vehicle!\nThe slot might already be occupied or vehicle number already exists.", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new VehicleEntryForm().setVisible(true);
        });
    }
}