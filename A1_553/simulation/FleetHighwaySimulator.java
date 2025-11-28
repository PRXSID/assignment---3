package simulation;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Fleet Highway Simulator - Main GUI Application
 * 
 * A graphical user interface for simulating vehicles traveling on a highway.
 * Demonstrates multithreading, race conditions, and synchronization in Java.
 * 
 * Features:
 * - Start, Pause, Resume, and Stop simulation controls
 * - Display of vehicle states (ID, Mileage, Fuel, Status)
 * - Shared Highway Distance Counter
 * - Race condition demonstration and fix toggle
 * - Refueling capability for out-of-fuel vehicles
 */
public class FleetHighwaySimulator extends JFrame {
    // Simulation components
    private final HighwayDistanceCounter highwayCounter;
    private final List<SimulatedVehicle> vehicles;
    private final List<VehicleThread> vehicleThreads;
    private boolean simulationRunning = false;
    
    // GUI Components
    private JLabel counterLabel;
    private JLabel expectedLabel;
    private JLabel syncStatusLabel;
    private JLabel raceConditionIndicator;
    private JPanel vehiclePanel;
    private JButton startButton;
    private JButton pauseButton;
    private JButton resumeButton;
    private JButton stopButton;
    private JCheckBox syncCheckbox;
    private Timer updateTimer;
    
    // Vehicle display labels
    private List<VehicleDisplayPanel> vehicleDisplays;
    
    // Constants
    private static final int NUM_VEHICLES = 3;
    private static final double INITIAL_FUEL = 20.0;  // Initial fuel in liters
    private static final double MAX_FUEL = 50.0;      // Max fuel capacity
    private static final double FUEL_RATE = 1.0;      // Fuel per km
    private static final int THREAD_JOIN_TIMEOUT_MS = 2000;  // Timeout for thread join
    
    /**
     * Creates the Fleet Highway Simulator GUI.
     */
    public FleetHighwaySimulator() {
        super("Fleet Highway Simulator - Assignment 3");
        
        // Initialize simulation components
        highwayCounter = new HighwayDistanceCounter();
        vehicles = new ArrayList<>();
        vehicleThreads = new ArrayList<>();
        vehicleDisplays = new ArrayList<>();
        
        // Create vehicles
        createVehicles();
        
        // Setup GUI
        initializeGUI();
        
        // Setup update timer for GUI refresh
        setupUpdateTimer();
        
        // Window settings
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 600);
        setLocationRelativeTo(null);
        setResizable(true);
    }
    
    /**
     * Create the simulated vehicles.
     */
    private void createVehicles() {
        vehicles.add(new SimulatedVehicle("V001", "Toyota Camry", INITIAL_FUEL, MAX_FUEL, FUEL_RATE));
        vehicles.add(new SimulatedVehicle("V002", "Honda Civic", INITIAL_FUEL, MAX_FUEL, FUEL_RATE));
        vehicles.add(new SimulatedVehicle("V003", "Ford Mustang", INITIAL_FUEL, MAX_FUEL, FUEL_RATE));
    }
    
    /**
     * Initialize all GUI components.
     */
    private void initializeGUI() {
        setLayout(new BorderLayout(10, 10));
        
        // Title Panel
        add(createTitlePanel(), BorderLayout.NORTH);
        
        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Highway Counter Panel
        mainPanel.add(createCounterPanel(), BorderLayout.NORTH);
        
        // Vehicle Status Panel
        mainPanel.add(createVehiclePanel(), BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Control Panel
        add(createControlPanel(), BorderLayout.SOUTH);
    }
    
    /**
     * Create the title panel.
     */
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(51, 102, 153));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        
        JLabel title = new JLabel("Fleet Highway Simulator");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        panel.add(title);
        
        return panel;
    }
    
    /**
     * Create the highway distance counter panel.
     */
    private JPanel createCounterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(51, 102, 153), 2),
                "Highway Distance Counter (Shared Resource)",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                new Color(51, 102, 153)
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Counter display
        JPanel counterRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        counterLabel = new JLabel("0 km");
        counterLabel.setFont(new Font("Arial", Font.BOLD, 36));
        counterLabel.setForeground(new Color(0, 128, 0));
        counterRow.add(counterLabel);
        panel.add(counterRow);
        
        // Expected increments
        JPanel expectedRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        expectedLabel = new JLabel("Expected Increments: 0");
        expectedLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        expectedRow.add(expectedLabel);
        panel.add(expectedRow);
        
        // Race condition indicator
        JPanel indicatorRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        raceConditionIndicator = new JLabel("Status: Ready");
        raceConditionIndicator.setFont(new Font("Arial", Font.BOLD, 14));
        indicatorRow.add(raceConditionIndicator);
        panel.add(indicatorRow);
        
        // Sync status
        JPanel syncRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        syncStatusLabel = new JLabel("Synchronization: DISABLED (Race Condition Mode)");
        syncStatusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        syncStatusLabel.setForeground(Color.RED);
        syncRow.add(syncStatusLabel);
        panel.add(syncRow);
        
        return panel;
    }
    
    /**
     * Create the vehicle status panel with scrolling support.
     */
    private JPanel createVehiclePanel() {
        vehiclePanel = new JPanel();
        vehiclePanel.setLayout(new BoxLayout(vehiclePanel, BoxLayout.Y_AXIS));
        vehiclePanel.setBackground(new Color(245, 248, 250));
        
        // Create display panels for each vehicle
        for (SimulatedVehicle vehicle : vehicles) {
            VehicleDisplayPanel display = new VehicleDisplayPanel(vehicle);
            vehicleDisplays.add(display);
            vehiclePanel.add(display);
            vehiclePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        
        // Wrap in a scroll pane for scrolling support
        JScrollPane scrollPane = new JScrollPane(vehiclePanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // Container panel with title border
        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(51, 102, 153), 2),
                "Vehicle Status",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                new Color(51, 102, 153)
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        containerPanel.add(scrollPane, BorderLayout.CENTER);
        
        return containerPanel;
    }
    
    /**
     * Create the control panel.
     */
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Simulation controls
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.setBorder(BorderFactory.createTitledBorder("Simulation Controls"));
        
        startButton = createStyledButton("Start", new Color(0, 128, 0));
        pauseButton = createStyledButton("Pause", new Color(255, 165, 0));
        resumeButton = createStyledButton("Resume", new Color(0, 128, 255));
        stopButton = createStyledButton("Stop", new Color(200, 0, 0));
        JButton resetButton = createStyledButton("Reset", new Color(128, 128, 128));
        
        // Initially disable pause, resume, stop
        pauseButton.setEnabled(false);
        resumeButton.setEnabled(false);
        stopButton.setEnabled(false);
        
        // Add action listeners
        startButton.addActionListener(e -> startSimulation());
        pauseButton.addActionListener(e -> pauseSimulation());
        resumeButton.addActionListener(e -> resumeSimulation());
        stopButton.addActionListener(e -> stopSimulation());
        resetButton.addActionListener(e -> resetSimulation());
        
        buttonPanel.add(startButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(resumeButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(resetButton);
        
        panel.add(buttonPanel, BorderLayout.CENTER);
        
        // Synchronization toggle
        JPanel syncPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        syncPanel.setBorder(BorderFactory.createTitledBorder("Race Condition Control"));
        
        syncCheckbox = new JCheckBox("Enable Synchronization (Fix Race Condition)");
        syncCheckbox.setFont(new Font("Arial", Font.BOLD, 12));
        syncCheckbox.addActionListener(e -> toggleSynchronization());
        
        syncPanel.add(syncCheckbox);
        panel.add(syncPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Create a styled button.
     */
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(90, 35));
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        return button;
    }
    
    /**
     * Setup the update timer for GUI refresh.
     */
    private void setupUpdateTimer() {
        updateTimer = new Timer(100, e -> SwingUtilities.invokeLater(this::updateDisplay));
    }
    
    /**
     * Update the display with current values.
     */
    private void updateDisplay() {
        // Update counter display
        int totalDistance = highwayCounter.getTotalDistance();
        int expectedIncrements = highwayCounter.getExpectedIncrements();
        
        counterLabel.setText(totalDistance + " km");
        expectedLabel.setText("Expected Increments: " + expectedIncrements);
        
        // Check for race condition
        if (simulationRunning) {
            // Calculate sum of individual mileages
            int sumMileage = 0;
            for (SimulatedVehicle v : vehicles) {
                sumMileage += (int) v.getMileage();
            }
            
            if (totalDistance != sumMileage) {
                raceConditionIndicator.setText("⚠ RACE CONDITION DETECTED! Counter: " + totalDistance + ", Sum: " + sumMileage);
                raceConditionIndicator.setForeground(Color.RED);
            } else {
                raceConditionIndicator.setText("✓ Counter Consistent: " + totalDistance + " km");
                raceConditionIndicator.setForeground(new Color(0, 128, 0));
            }
        }
        
        // Update vehicle displays
        for (VehicleDisplayPanel display : vehicleDisplays) {
            display.updateDisplay();
        }
    }
    
    /**
     * Start the simulation.
     */
    private void startSimulation() {
        if (simulationRunning) return;
        
        simulationRunning = true;
        highwayCounter.reset();
        
        // Create new threads for each vehicle
        vehicleThreads.clear();
        for (SimulatedVehicle vehicle : vehicles) {
            VehicleThread thread = new VehicleThread(vehicle, highwayCounter);
            vehicleThreads.add(thread);
            thread.startSimulation();
        }
        
        // Start update timer
        updateTimer.start();
        
        // Update button states
        startButton.setEnabled(false);
        pauseButton.setEnabled(true);
        stopButton.setEnabled(true);
        syncCheckbox.setEnabled(false);
        
        raceConditionIndicator.setText("Status: Simulation Running...");
        raceConditionIndicator.setForeground(new Color(0, 128, 255));
    }
    
    /**
     * Pause the simulation.
     */
    private void pauseSimulation() {
        for (VehicleThread thread : vehicleThreads) {
            thread.pauseSimulation();
        }
        
        pauseButton.setEnabled(false);
        resumeButton.setEnabled(true);
        
        raceConditionIndicator.setText("Status: Simulation Paused");
        raceConditionIndicator.setForeground(new Color(255, 165, 0));
    }
    
    /**
     * Resume the simulation.
     */
    private void resumeSimulation() {
        for (VehicleThread thread : vehicleThreads) {
            thread.resumeSimulation();
        }
        
        pauseButton.setEnabled(true);
        resumeButton.setEnabled(false);
        
        raceConditionIndicator.setText("Status: Simulation Running...");
        raceConditionIndicator.setForeground(new Color(0, 128, 255));
    }
    
    /**
     * Stop the simulation.
     */
    private void stopSimulation() {
        simulationRunning = false;
        
        // Stop all vehicle threads
        for (VehicleThread thread : vehicleThreads) {
            thread.stopSimulation();
        }
        
        // Wait for threads to finish
        for (VehicleThread thread : vehicleThreads) {
            try {
                thread.join(THREAD_JOIN_TIMEOUT_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        vehicleThreads.clear();
        updateTimer.stop();
        
        // Final update
        updateDisplay();
        
        // Update button states
        startButton.setEnabled(true);
        pauseButton.setEnabled(false);
        resumeButton.setEnabled(false);
        stopButton.setEnabled(false);
        syncCheckbox.setEnabled(true);
        
        // Show final status
        int totalDistance = highwayCounter.getTotalDistance();
        int sumMileage = 0;
        for (SimulatedVehicle v : vehicles) {
            sumMileage += (int) v.getMileage();
        }
        
        if (totalDistance != sumMileage) {
            raceConditionIndicator.setText("⚠ FINAL: Race condition occurred! Counter: " + totalDistance + ", Sum: " + sumMileage);
            raceConditionIndicator.setForeground(Color.RED);
        } else {
            raceConditionIndicator.setText("✓ FINAL: Counter Consistent: " + totalDistance + " km");
            raceConditionIndicator.setForeground(new Color(0, 128, 0));
        }
    }
    
    /**
     * Reset the simulation.
     */
    private void resetSimulation() {
        // Stop if running
        if (simulationRunning) {
            stopSimulation();
        }
        
        // Reset counter
        highwayCounter.reset();
        
        // Reset vehicles
        for (SimulatedVehicle vehicle : vehicles) {
            vehicle.reset(INITIAL_FUEL);
        }
        
        // Update display
        updateDisplay();
        
        raceConditionIndicator.setText("Status: Ready");
        raceConditionIndicator.setForeground(Color.BLACK);
    }
    
    /**
     * Toggle synchronization on/off.
     */
    private void toggleSynchronization() {
        boolean enabled = syncCheckbox.isSelected();
        highwayCounter.setSynchronizationEnabled(enabled);
        
        if (enabled) {
            syncStatusLabel.setText("Synchronization: ENABLED (Thread-Safe Mode)");
            syncStatusLabel.setForeground(new Color(0, 128, 0));
        } else {
            syncStatusLabel.setText("Synchronization: DISABLED (Race Condition Mode)");
            syncStatusLabel.setForeground(Color.RED);
        }
    }
    
    /**
     * Inner class for vehicle display panels.
     */
    private class VehicleDisplayPanel extends JPanel {
        private final SimulatedVehicle vehicle;
        private final JLabel idLabel;
        private final JLabel mileageLabel;
        private final JLabel fuelLabel;
        private final JLabel statusLabel;
        private final JProgressBar fuelBar;
        private final JButton refuelButton;
        
        public VehicleDisplayPanel(SimulatedVehicle vehicle) {
            this.vehicle = vehicle;
            setLayout(new BorderLayout(15, 5));
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 102, 153), 1),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
            ));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
            setBackground(Color.WHITE);
            
            // Left panel - vehicle info (non-opaque to inherit parent background color for status)
            JPanel infoPanel = new JPanel(new GridLayout(2, 2, 15, 5));
            infoPanel.setOpaque(false);
            
            idLabel = new JLabel("\uD83D\uDE97 " + vehicle.getName() + " (" + vehicle.getId() + ")");
            idLabel.setFont(new Font("Arial", Font.BOLD, 14));
            idLabel.setForeground(new Color(51, 102, 153));
            
            mileageLabel = new JLabel("\uD83D\uDCCF Mileage: 0 km");
            mileageLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            
            fuelLabel = new JLabel(String.format("\u26FD Fuel: %.1f / %.1f L", vehicle.getFuelLevel(), vehicle.getMaxFuel()));
            fuelLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            
            statusLabel = new JLabel("\u2022 Status: " + vehicle.getStatus());
            statusLabel.setFont(new Font("Arial", Font.BOLD, 12));
            
            infoPanel.add(idLabel);
            infoPanel.add(mileageLabel);
            infoPanel.add(statusLabel);
            infoPanel.add(fuelLabel);
            
            add(infoPanel, BorderLayout.CENTER);
            
            // Right panel - fuel bar and refuel button (non-opaque to inherit parent background color)
            JPanel rightPanel = new JPanel(new BorderLayout(5, 8));
            rightPanel.setOpaque(false);
            
            fuelBar = new JProgressBar(0, (int) vehicle.getMaxFuel());
            fuelBar.setValue((int) vehicle.getFuelLevel());
            fuelBar.setStringPainted(true);
            fuelBar.setPreferredSize(new Dimension(120, 22));
            fuelBar.setForeground(new Color(0, 128, 0));
            fuelBar.setBackground(new Color(230, 230, 230));
            
            refuelButton = new JButton("\u26FD Refuel");
            refuelButton.setFont(new Font("Arial", Font.BOLD, 11));
            refuelButton.setPreferredSize(new Dimension(90, 28));
            refuelButton.setBackground(new Color(0, 128, 255));
            refuelButton.setForeground(Color.WHITE);
            refuelButton.setFocusPainted(false);
            refuelButton.setBorder(BorderFactory.createRaisedBevelBorder());
            refuelButton.addActionListener(e -> refuelVehicle());
            
            rightPanel.add(fuelBar, BorderLayout.CENTER);
            rightPanel.add(refuelButton, BorderLayout.SOUTH);
            
            add(rightPanel, BorderLayout.EAST);
        }
        
        public void updateDisplay() {
            mileageLabel.setText(String.format("\uD83D\uDCCF Mileage: %.0f km", vehicle.getMileage()));
            fuelLabel.setText(String.format("\u26FD Fuel: %.1f / %.1f L", vehicle.getFuelLevel(), vehicle.getMaxFuel()));
            fuelBar.setValue((int) vehicle.getFuelLevel());
            
            // Update fuel bar color based on level (guard against division by zero)
            double maxFuel = vehicle.getMaxFuel();
            double fuelPercent = maxFuel > 0 ? vehicle.getFuelLevel() / maxFuel : 0;
            if (fuelPercent > 0.5) {
                fuelBar.setForeground(new Color(0, 128, 0));
            } else if (fuelPercent > 0.2) {
                fuelBar.setForeground(new Color(255, 165, 0));
            } else {
                fuelBar.setForeground(Color.RED);
            }
            
            SimulatedVehicle.VehicleStatus status = vehicle.getStatus();
            statusLabel.setText("\u2022 Status: " + status);
            
            // Color code status
            switch (status) {
                case RUNNING:
                    statusLabel.setForeground(new Color(0, 128, 0));
                    setBackground(new Color(232, 245, 233));
                    break;
                case PAUSED:
                    statusLabel.setForeground(new Color(255, 165, 0));
                    setBackground(new Color(255, 248, 225));
                    break;
                case OUT_OF_FUEL:
                    statusLabel.setForeground(Color.RED);
                    setBackground(new Color(255, 235, 238));
                    break;
                case STOPPED:
                    statusLabel.setForeground(Color.GRAY);
                    setBackground(Color.WHITE);
                    break;
            }
        }
        
        private void refuelVehicle() {
            vehicle.refuel();
            updateDisplay();
        }
    }
    
    /**
     * Main entry point for the application.
     */
    public static void main(String[] args) {
        // Ensure GUI updates happen on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                // Set look and feel to system default
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Use default look and feel
            }
            
            FleetHighwaySimulator simulator = new FleetHighwaySimulator();
            simulator.setVisible(true);
        });
    }
}
