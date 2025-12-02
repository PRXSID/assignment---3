package simulation;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

import transportation.abstractclasses.Vehicle;

public class FleetHighwaySimulator extends JFrame {
    private final HighwayDistanceCounter highwayCounter;
    private final List<SimulatedVehicle> vehicles;
    private final List<VehicleThread> vehicleThreads;
    private boolean simulationRunning = false;
    
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
    
    private List<VehicleDisplayPanel> vehicleDisplays;
    
    private static final int NUM_VEHICLES = 3;
    private static final double INITIAL_FUEL = 20.0;
    private static final double MAX_FUEL = 50.0;
    private static final double FUEL_RATE = 1.0;
    private static final int THREAD_JOIN_TIMEOUT_MS = 2000;
    
    public FleetHighwaySimulator() {
        this(null);
    }
    
    public FleetHighwaySimulator(List<Vehicle> fleetVehicles) {
        super("Fleet Highway Simulator - Assignment 3");
        
        highwayCounter = new HighwayDistanceCounter();
        vehicles = new ArrayList<>();
        vehicleThreads = new ArrayList<>();
        vehicleDisplays = new ArrayList<>();
        
        if (fleetVehicles != null && !fleetVehicles.isEmpty()) {
            createVehiclesFromFleet(fleetVehicles);
        } else {
            createVehicles();
        }
        
        initializeGUI();
        
        setupUpdateTimer();
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 600);
        setLocationRelativeTo(null);
        setResizable(true);
    }
    
    private void createVehiclesFromFleet(List<Vehicle> fleetVehicles) {
        for (Vehicle vehicle : fleetVehicles) {
            FleetVehicleAdapter adapter = new FleetVehicleAdapter(vehicle, MAX_FUEL, FUEL_RATE);
            vehicles.add(adapter);
        }
        
        if (vehicles.isEmpty()) {
            createVehicles();
        }
    }
    
    private void createVehicles() {
        vehicles.add(new SimulatedVehicle("V001", "Toyota Camry", INITIAL_FUEL, MAX_FUEL, FUEL_RATE));
        vehicles.add(new SimulatedVehicle("V002", "Honda Civic", INITIAL_FUEL, MAX_FUEL, FUEL_RATE));
        vehicles.add(new SimulatedVehicle("V003", "Ford Mustang", INITIAL_FUEL, MAX_FUEL, FUEL_RATE));
    }
    
    private void initializeGUI() {
        setLayout(new BorderLayout(10, 10));
        
        add(createTitlePanel(), BorderLayout.NORTH);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        mainPanel.add(createCounterPanel(), BorderLayout.NORTH);
        
        mainPanel.add(createVehiclePanel(), BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
        
        add(createControlPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        
        JLabel title = new JLabel("Fleet Highway Simulator");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(title);
        
        return panel;
    }
    
    private JPanel createCounterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                "Highway Distance Counter (Shared Resource)",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14)
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JPanel counterRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        counterLabel = new JLabel("0 km");
        counterLabel.setFont(new Font("Arial", Font.BOLD, 36));
        counterRow.add(counterLabel);
        panel.add(counterRow);
        
        JPanel expectedRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        expectedLabel = new JLabel("Expected Increments: 0");
        expectedLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        expectedRow.add(expectedLabel);
        panel.add(expectedRow);
        
        JPanel indicatorRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        raceConditionIndicator = new JLabel("Status: Ready");
        raceConditionIndicator.setFont(new Font("Arial", Font.BOLD, 14));
        indicatorRow.add(raceConditionIndicator);
        panel.add(indicatorRow);
        
        JPanel syncRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        syncStatusLabel = new JLabel("Synchronization: DISABLED (Race Condition Mode)");
        syncStatusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        syncRow.add(syncStatusLabel);
        panel.add(syncRow);
        
        return panel;
    }
    
    private JPanel createVehiclePanel() {
        vehiclePanel = new JPanel();
        vehiclePanel.setLayout(new BoxLayout(vehiclePanel, BoxLayout.Y_AXIS));
        
        for (SimulatedVehicle vehicle : vehicles) {
            VehicleDisplayPanel display = new VehicleDisplayPanel(vehicle);
            vehicleDisplays.add(display);
            vehiclePanel.add(display);
            vehiclePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        
        JScrollPane scrollPane = new JScrollPane(vehiclePanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                "Vehicle Status",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14)
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        containerPanel.add(scrollPane, BorderLayout.CENTER);
        
        return containerPanel;
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.setBorder(BorderFactory.createTitledBorder("Simulation Controls"));
        
        startButton = createStyledButton("Start");
        pauseButton = createStyledButton("Pause");
        resumeButton = createStyledButton("Resume");
        stopButton = createStyledButton("Stop");
        JButton resetButton = createStyledButton("Reset");
        
        pauseButton.setEnabled(false);
        resumeButton.setEnabled(false);
        stopButton.setEnabled(false);
        
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
        
        JPanel syncPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        syncPanel.setBorder(BorderFactory.createTitledBorder("Race Condition Control"));
        
        syncCheckbox = new JCheckBox("Enable Synchronization (Fix Race Condition)");
        syncCheckbox.setFont(new Font("Arial", Font.BOLD, 12));
        syncCheckbox.addActionListener(e -> toggleSynchronization());
        
        syncPanel.add(syncCheckbox);
        panel.add(syncPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(90, 35));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        return button;
    }
    
    private void setupUpdateTimer() {
        updateTimer = new Timer(100, e -> SwingUtilities.invokeLater(this::updateDisplay));
    }
    
    private void updateDisplay() {
        int totalDistance = highwayCounter.getTotalDistance();
        int expectedIncrements = highwayCounter.getExpectedIncrements();
        
        counterLabel.setText(totalDistance + " km");
        expectedLabel.setText("Expected Increments: " + expectedIncrements);
        
        if (simulationRunning) {
            int sumMileage = 0;
            for (SimulatedVehicle v : vehicles) {
                sumMileage += (int) v.getMileage();
            }
            
            if (totalDistance != sumMileage) {
                raceConditionIndicator.setText("RACE CONDITION DETECTED! Counter: " + totalDistance + ", Sum: " + sumMileage);
            } else {
                raceConditionIndicator.setText("Counter Consistent: " + totalDistance + " km");
            }
        }
        
        for (VehicleDisplayPanel display : vehicleDisplays) {
            display.updateDisplay();
        }
    }
    
    private void startSimulation() {
        if (simulationRunning) return;
        
        simulationRunning = true;
        highwayCounter.reset();
        
        vehicleThreads.clear();
        for (SimulatedVehicle vehicle : vehicles) {
            VehicleThread thread = new VehicleThread(vehicle, highwayCounter);
            vehicleThreads.add(thread);
            thread.startSimulation();
        }
        
        updateTimer.start();
        
        startButton.setEnabled(false);
        pauseButton.setEnabled(true);
        stopButton.setEnabled(true);
        syncCheckbox.setEnabled(false);
        
        raceConditionIndicator.setText("Status: Simulation Running...");
    }
    
    private void pauseSimulation() {
        for (VehicleThread thread : vehicleThreads) {
            thread.pauseSimulation();
        }
        
        pauseButton.setEnabled(false);
        resumeButton.setEnabled(true);
        
        raceConditionIndicator.setText("Status: Simulation Paused");
    }
    
    private void resumeSimulation() {
        for (VehicleThread thread : vehicleThreads) {
            thread.resumeSimulation();
        }
        
        pauseButton.setEnabled(true);
        resumeButton.setEnabled(false);
        
        raceConditionIndicator.setText("Status: Simulation Running...");
    }
    
    private void stopSimulation() {
        simulationRunning = false;
        
        for (VehicleThread thread : vehicleThreads) {
            thread.stopSimulation();
        }
        
        for (VehicleThread thread : vehicleThreads) {
            try {
                thread.join(THREAD_JOIN_TIMEOUT_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        vehicleThreads.clear();
        updateTimer.stop();
        
        updateDisplay();
        
        startButton.setEnabled(true);
        pauseButton.setEnabled(false);
        resumeButton.setEnabled(false);
        stopButton.setEnabled(false);
        syncCheckbox.setEnabled(true);
        
        int totalDistance = highwayCounter.getTotalDistance();
        int sumMileage = 0;
        for (SimulatedVehicle v : vehicles) {
            sumMileage += (int) v.getMileage();
        }
        
        if (totalDistance != sumMileage) {
            raceConditionIndicator.setText("FINAL: Race condition occurred! Counter: " + totalDistance + ", Sum: " + sumMileage);
        } else {
            raceConditionIndicator.setText("FINAL: Counter Consistent: " + totalDistance + " km");
        }
    }
    
    private void resetSimulation() {
        if (simulationRunning) {
            stopSimulation();
        }
        
        highwayCounter.reset();
        
        for (SimulatedVehicle vehicle : vehicles) {
            vehicle.reset(INITIAL_FUEL);
        }
        
        updateDisplay();
        
        raceConditionIndicator.setText("Status: Ready");
    }
    
    private void toggleSynchronization() {
        boolean enabled = syncCheckbox.isSelected();
        highwayCounter.setSynchronizationEnabled(enabled);
        
        if (enabled) {
            syncStatusLabel.setText("Synchronization: ENABLED (Thread-Safe Mode)");
        } else {
            syncStatusLabel.setText("Synchronization: DISABLED (Race Condition Mode)");
        }
    }
    
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
                BorderFactory.createLineBorder(Color.BLACK, 1),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
            ));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
            
            JPanel infoPanel = new JPanel(new GridLayout(2, 2, 15, 5));
            infoPanel.setOpaque(false);
            
            idLabel = new JLabel(vehicle.getName() + " (" + vehicle.getId() + ")");
            idLabel.setFont(new Font("Arial", Font.BOLD, 14));
            
            mileageLabel = new JLabel("Mileage: 0 km");
            mileageLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            
            fuelLabel = new JLabel(String.format("Fuel: %.1f / %.1f L", vehicle.getFuelLevel(), vehicle.getMaxFuel()));
            fuelLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            
            statusLabel = new JLabel("Status: " + vehicle.getStatus());
            statusLabel.setFont(new Font("Arial", Font.BOLD, 12));
            
            infoPanel.add(idLabel);
            infoPanel.add(mileageLabel);
            infoPanel.add(statusLabel);
            infoPanel.add(fuelLabel);
            
            add(infoPanel, BorderLayout.CENTER);
            
            JPanel rightPanel = new JPanel(new BorderLayout(5, 8));
            rightPanel.setOpaque(false);
            
            fuelBar = new JProgressBar(0, (int) vehicle.getMaxFuel());
            fuelBar.setValue((int) vehicle.getFuelLevel());
            fuelBar.setStringPainted(true);
            fuelBar.setPreferredSize(new Dimension(120, 22));
            
            refuelButton = new JButton("Refuel");
            refuelButton.setFont(new Font("Arial", Font.BOLD, 11));
            refuelButton.setPreferredSize(new Dimension(90, 28));
            refuelButton.setFocusPainted(false);
            refuelButton.setOpaque(true);
            refuelButton.setContentAreaFilled(true);
            refuelButton.addActionListener(e -> refuelVehicle());
            
            rightPanel.add(fuelBar, BorderLayout.CENTER);
            rightPanel.add(refuelButton, BorderLayout.SOUTH);
            
            add(rightPanel, BorderLayout.EAST);
        }
        
        public void updateDisplay() {
            mileageLabel.setText(String.format("Mileage: %.0f km", vehicle.getMileage()));
            fuelLabel.setText(String.format("Fuel: %.1f / %.1f L", vehicle.getFuelLevel(), vehicle.getMaxFuel()));
            fuelBar.setValue((int) vehicle.getFuelLevel());
            
            SimulatedVehicle.VehicleStatus status = vehicle.getStatus();
            statusLabel.setText("Status: " + status);
        }
        
        private void refuelVehicle() {
            vehicle.refuel();
            updateDisplay();
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
            }
            
            FleetHighwaySimulator simulator = new FleetHighwaySimulator();
            simulator.setVisible(true);
        });
    }
}
