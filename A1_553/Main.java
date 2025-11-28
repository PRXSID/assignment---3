import java.util.*;
import transportation.management.FleetManager;
import transportation.vehicles.*;
import transportation.abstractclasses.Vehicle;
import transportation.interfaces.*;
import transportation.exceptions.*;
import transportation.utility.*;
import simulation.FleetHighwaySimulator;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static FleetManager fleetManager = new FleetManager();

    public static void main(String[] args) {
        initializeDemoData();
        
        boolean running = true;
        while (running) {
            displayMenu();
            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                // CORE VEHICLE MANAGEMENT
                case 1: addVehicle(); break;
                case 2: removeVehicle(); break;
                
                // OPERATIONS
                case 3: startJourney(); break;
                case 4: refuelAll(); break;
                case 5: performMaintenance(); break;
                
                // CARGO/PASSENGER OPERATIONS
                case 6: embarkPassengers(); break;
                case 7: disembarkPassengers(); break;
                case 8: loadCargo(); break;
                case 9: unloadCargo(); break;
                
                // REPORTS
                case 10: generateReport(); break;
                case 11: listMaintenanceNeeds(); break;
                case 12: calculateTotalFuelConsumption(); break;
                case 13: searchByType(); break;
                
                // PERSISTENCE
                case 14: saveFleet(); break;
                case 15: loadFleet(); break;

                // ASSIGNMENT 2 COLLECTION / SORTING FEATURES
                case 16: listDistinctModels(); break;
                case 17: sortFleet(); break;
                
                // ASSIGNMENT 3 - HIGHWAY SIMULATOR INTEGRATION
                case 18: launchHighwaySimulator(); break;
                
                // EXIT
                case 19: running = false; System.out.println("Exiting System. Goodbye!"); break;

                default: System.out.println("Invalid choice. Try again.");
            }
            System.out.println();
        }
    }
    
    private static void initializeDemoData() {
        try {
            System.out.println("Initializing Demo Fleet Data");
            fleetManager.addVehicle(new Car("C001", "Toyota Camry", 180.0, 4));
            fleetManager.getVehicleById("C001").setMileage(12000, 2000);
            ((FuelConsumable) fleetManager.getVehicleById("C001")).refuel(50.0);
            ((PassengerCarrier) fleetManager.getVehicleById("C001")).boardPassengers(2);
            
            fleetManager.addVehicle(new Truck("T002", "Volvo", 110.0, 10));
            ((FuelConsumable) fleetManager.getVehicleById("T002")).refuel(100.0);
            ((CargoCarrier) fleetManager.getVehicleById("T002")).loadCargo(3000.0);

            fleetManager.addVehicle(new Bus("B003", "Mercedes", 140.0, 6));
            
            fleetManager.addVehicle(new Airplane("A004", "Boeing 747", 900.0, 12000.0));
            ((FuelConsumable) fleetManager.getVehicleById("A004")).refuel(500.0);
            
            fleetManager.addVehicle(new CargoShip("S005", "Titanic", 35.0, false));
            ((FuelConsumable) fleetManager.getVehicleById("S005")).refuel(2000.0);
            
            fleetManager.addVehicle(new Car("C006", "Toyota", 200.0, 4)); 
            System.out.println("Initialized " + fleetManager.getFleet().size() + " vehicles.\n");
        } catch (Exception e) {
            System.out.println("Initialization Error: " + e.getMessage());
        }
    }

    private static void displayMenu() {
        System.out.println("$$$$$ FLEET MANAGEMENT SYSTEM $$$$$");
        System.out.println("1. Add Vehicle");
        System.out.println("2. Remove Vehicle");
        System.out.println("3. Start Journey (All Vehicles)");
        System.out.println("4. Refuel All Vehicles");
        System.out.println("5. Perform Maintenance (Needed Only)");
        System.out.println("6. Embark Passengers (by ID)");
        System.out.println("7. Disembark Passengers (by ID)");
        System.out.println("8. Load Cargo (by ID)");
        System.out.println("9. Unload Cargo (by ID)");
        System.out.println("10. Generate Full Report");
        System.out.println("11. List Vehicles Needing Maintenance");
        System.out.println("12. Calculate Total Fuel Consumption");
        System.out.println("13. Search by Type / Interface");
        System.out.println("14. Save Fleet to CSV");
        System.out.println("15. Load Fleet from CSV");
        System.out.println("16. List Distinct Vehicle Models");
        System.out.println("17. Sort Fleet");
        System.out.println("18. Launch Highway Simulator (GUI)");
        System.out.println("19. Exit System");
    }

    private static void sortFleet() {
        System.out.println("\nSort Fleet");
        System.out.println("1. Sort by Efficiency (Descending - Most Efficient First)");
        System.out.println("2. Sort by Max Speed (Descending - Fastest First)");
        System.out.println("3. Sort by Model Name (Alphabetical)");
        System.out.println("4. Sort by Total Mileage (Descending - Most Used First)");
        
        int choice = getIntInput("Enter sort choice: ");
        
        try {
            switch(choice) {
                case 1:
                    fleetManager.sortFleetByEfficiencyDescending();
                    System.out.println("\nFleet sorted by Efficiency (Most to Least Efficient):");
                    break;
                case 2:
                    fleetManager.sortFleetBySpeed();
                    System.out.println("\nFleet sorted by Max Speed (Fastest to Slowest):");
                    break;
                case 3:
                    fleetManager.sortFleetByModelName();
                    System.out.println("\nFleet sorted by Model Name (A-Z):");
                    break;
                case 4:
                    fleetManager.sortFleetByTotalMileage(); 
                    System.out.println("\nFleet sorted by Total Mileage (Most Used First):");
                    break;
                default: 
                    System.out.println("Invalid sort choice.");
                    return;
            }
            
            for (Vehicle v : fleetManager.getFleet()) {
                v.displayInfo();
            }

        } catch (Exception e) {
            System.out.println("Error during sorting: " + e.getMessage());
        }
    }
    
    private static void listDistinctModels() {
        System.out.println("\nDistinct Vehicle Models (Alphabetical)");
        Set<String> models = fleetManager.getDistinctModels();
        if (models.isEmpty()) {
            System.out.println("No models found in the fleet.");
            return;
        }
        
        int i = 1;
        for(String model : models) {
            System.out.println(i++ + ". " + model);
        }
        System.out.println("Total Distinct Models: " + models.size());
    }

    private static int getIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            if (scanner.hasNextInt()) {
                int val = scanner.nextInt();
                scanner.nextLine();
                return val;
            } else {
                System.out.println("Please enter a valid number.");
                scanner.nextLine();
            }
        }
    }

    private static double getDoubleInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            if (scanner.hasNextDouble()) {
                double val = scanner.nextDouble();
                scanner.nextLine();
                return val;
            } else {
                System.out.println("Please enter a valid number.");
                scanner.nextLine();
            }
        }
    }

    private static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    
    private static void addVehicle() {
        try {
            System.out.println("Select vehicle type:");
            System.out.println("1. Car 2. Truck 3. Bus 4. Airplane 5. Cargo Ship");
            int typeChoice = getIntInput("Enter type: ");
            String id = getStringInput("Enter ID: ");
            String model = getStringInput("Enter model: ");
            double maxSpeed = getDoubleInput("Enter max speed: ");

            switch (typeChoice) {
                case 1: {
                    int wheels = getIntInput("Enter number of wheels: ");
                    Car car = new Car(id, model, maxSpeed, wheels);
                    double fuel = getDoubleInput("Enter initial fuel (can be 0): ");
                    if (fuel > 0) car.refuel(fuel);
                    int passengers = getIntInput("Enter initial passengers: ");
                    try { car.boardPassengers(passengers); } catch (OverloadException ignored) {}
                    fleetManager.addVehicle(car);
                    System.out.println("Car added successfully.");
                    break;
                }
                case 2: {
                    int wheels = getIntInput("Enter number of wheels: ");
                    Truck truck = new Truck(id, model, maxSpeed, wheels);
                    double fuel = getDoubleInput("Enter initial fuel (can be 0): ");
                    if (fuel > 0) truck.refuel(fuel);
                    double cargo = getDoubleInput("Enter initial cargo (kg): ");
                    try { truck.loadCargo(cargo); } catch (OverloadException | InvalidOperationException ignored) {}
                    fleetManager.addVehicle(truck);
                    System.out.println("Truck added successfully.");
                    break;
                }
                case 3: {
                    int wheels = getIntInput("Enter number of wheels: ");
                    Bus bus = new Bus(id, model, maxSpeed, wheels);
                    double fuel = getDoubleInput("Enter initial fuel (can be 0): ");
                    if (fuel > 0) bus.refuel(fuel);
                    double cargo = getDoubleInput("Enter initial cargo (kg): ");
                    try { bus.loadCargo(cargo); } catch (OverloadException | InvalidOperationException ignored) {}
                    int passengers = getIntInput("Enter initial passengers: ");
                    try { bus.boardPassengers(passengers); } catch (OverloadException ignored) {}
                    fleetManager.addVehicle(bus);
                    System.out.println("Bus added successfully.");
                    break;
                }
                case 4: {
                    double maxAlt = getDoubleInput("Enter max altitude: ");
                    Airplane plane = new Airplane(id, model, maxSpeed, maxAlt);
                    double fuel = getDoubleInput("Enter initial fuel (can be 0): ");
                    if (fuel > 0) plane.refuel(fuel);
                    double cargo = getDoubleInput("Enter initial cargo (kg): ");
                    try { plane.loadCargo(cargo); } catch (OverloadException | InvalidOperationException ignored) {}
                    int passengers = getIntInput("Enter initial passengers: ");
                    try { plane.boardPassengers(passengers); } catch (OverloadException ignored) {}
                    fleetManager.addVehicle(plane);
                    System.out.println("Airplane added successfully.");
                    break;
                }
                case 5: {
                    System.out.println("Does it have sails? 1. Yes 2. No");
                    boolean hasSail = getIntInput("Choice: ") == 1;
                    CargoShip ship = new CargoShip(id, model, maxSpeed, hasSail);
                    if (!hasSail) {
                        double fuel = getDoubleInput("Enter initial fuel: ");
                        if (fuel > 0) ship.refuel(fuel);
                    }
                    double cargo = getDoubleInput("Enter initial cargo (kg): ");
                    try { ship.loadCargo(cargo); } catch (OverloadException | InvalidOperationException ignored) {}
                    fleetManager.addVehicle(ship);
                    System.out.println("Cargo ship added successfully.");
                    break;
                }
                default: System.out.println("Invalid type.");
            }
        } catch (InvalidOperationException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void removeVehicle() {
        String id = getStringInput("Enter vehicle ID to remove: ");
        try { fleetManager.removeVehicle(id); System.out.println("Vehicle removed."); }
        catch (InvalidOperationException e) { System.out.println("Error: " + e.getMessage()); }
    }

    private static void startJourney() {
        double distance = getDoubleInput("Enter journey distance (km): ");
        fleetManager.startAllJourneys(distance);
    }

    private static void calculateTotalFuelConsumption() {
        double distance = getDoubleInput("Enter distance (km): ");
        double totalFuel = fleetManager.getTotalFuelConsumption(distance);
        System.out.printf("Total fuel consumption: %.2f liters%n", totalFuel);
    }

    private static void refuelAll() {
        double amount = getDoubleInput("Enter fuel amount: ");
        int count = 0;
        for (Vehicle v : fleetManager.getFleet()) {
            if (v instanceof FuelConsumable) {
                try { ((FuelConsumable) v).refuel(amount); count++; }
                catch (Exception e) { System.out.println("Error refueling " + v.getId() + ": " + e.getMessage()); }
            }
        }
        System.out.println("Refueled " + count + " vehicles");
    }

    private static void performMaintenance() {
        fleetManager.maintainAll();
        System.out.println("Maintenance performed.");
    }

    private static void generateReport() {
        System.out.println(fleetManager.generateReport());
    }

    private static void saveFleet() {
        String filename = getStringInput("Enter filename: ");
        fleetManager.saveToFile(filename);
    }

    private static void loadFleet() {
        String filename = getStringInput("Enter filename: ");
        fleetManager.loadFromFile(filename);
    }

    private static void searchByType() {
        System.out.println("Search by: 1.Car 2.Truck 3.Bus 4.Airplane 5.CargoShip 6.Fuel 7.Cargo 8.Passenger 9.Maintainable");
        int choice = getIntInput("Choice: ");
        Class<?> type = switch (choice) {
            case 1 -> Car.class;
            case 2 -> Truck.class;
            case 3 -> Bus.class;
            case 4 -> Airplane.class;
            case 5 -> CargoShip.class;
            case 6 -> FuelConsumable.class;
            case 7 -> CargoCarrier.class;
            case 8 -> PassengerCarrier.class;
            case 9 -> Maintainable.class;
            default -> null;
        };
        if (type == null) { System.out.println("Invalid choice."); return; }
        List<Vehicle> results = fleetManager.searchByType(type);
        System.out.println("Found " + results.size() + " vehicles:");
        for (Vehicle v : results) v.displayInfo();
    }

    private static void listMaintenanceNeeds() {
        List<Vehicle> list = fleetManager.getVehiclesNeedingMaintenance();
        System.out.println("Vehicles needing maintenance: " + list.size());
        for (Vehicle v : list) v.displayInfo();
    }

    private static void embarkPassengers() {
        String id = getStringInput("Enter vehicle ID: ");
        Vehicle vehicle = fleetManager.getVehicleById(id);
        if (vehicle instanceof PassengerCarrier) {
            int passengers = getIntInput("Enter number of passengers to embark: ");
            try { ((PassengerCarrier) vehicle).boardPassengers(passengers); }
            catch (OverloadException e) { System.out.println("Error: " + e.getMessage()); }
        } else System.out.println("Vehicle cannot carry passengers.");
    }

    private static void disembarkPassengers() {
        String id = getStringInput("Enter vehicle ID: ");
        Vehicle vehicle = fleetManager.getVehicleById(id);
        if (vehicle instanceof PassengerCarrier) {
            int passengers = getIntInput("Enter number of passengers to disembark: ");
            try { ((PassengerCarrier) vehicle).disembarkPassengers(passengers); }
            catch (InvalidOperationException e) { System.out.println("Error: " + e.getMessage()); }
        } else System.out.println("Vehicle not found or cannot carry passengers.");
    }

    private static void loadCargo() {
        String id = getStringInput("Enter vehicle ID: ");
        Vehicle vehicle = fleetManager.getVehicleById(id);
        if (vehicle instanceof CargoCarrier) {
            double amount = getDoubleInput("Enter cargo amount (kg): ");
            try { ((CargoCarrier) vehicle).loadCargo(amount); }
            catch (OverloadException | InvalidOperationException e) { System.out.println("Error: " + e.getMessage()); }
        } else System.out.println("Vehicle cannot carry cargo.");
    }

    private static void unloadCargo() {
        String id = getStringInput("Enter vehicle ID: ");
        Vehicle vehicle = fleetManager.getVehicleById(id);
        if (vehicle instanceof CargoCarrier) {
            double amount = getDoubleInput("Enter cargo amount (kg): ");
            try { ((CargoCarrier) vehicle).unloadCargo(amount); }
            catch (InvalidOperationException e) { System.out.println("Error: " + e.getMessage()); }
        } else System.out.println("Vehicle cannot carry cargo.");
    }
    
    /**
     * Launch the Highway Simulator GUI with current fleet vehicles.
     * This integrates Assignment 3 (Highway Simulator) with the Fleet Management System.
     */
    private static void launchHighwaySimulator() {
        System.out.println("\nLaunching Highway Simulator...");
        System.out.println("1. Use current fleet vehicles");
        System.out.println("2. Use demo vehicles");
        
        int choice = getIntInput("Enter choice: ");
        
        final java.util.List<Vehicle> vehiclesToUse;
        
        if (choice == 1) {
            vehiclesToUse = fleetManager.getFleet();
            if (vehiclesToUse.isEmpty()) {
                System.out.println("No vehicles in fleet. Using demo vehicles instead.");
            } else {
                System.out.println("Loading " + vehiclesToUse.size() + " vehicles from fleet...");
            }
        } else {
            vehiclesToUse = null;
            System.out.println("Using demo vehicles...");
        }
        
        // Launch the GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                // Set look and feel to system default
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Use default look and feel
            }
            
            FleetHighwaySimulator simulator;
            if (vehiclesToUse != null && !vehiclesToUse.isEmpty()) {
                simulator = new FleetHighwaySimulator(vehiclesToUse);
            } else {
                simulator = new FleetHighwaySimulator();
            }
            simulator.setVisible(true);
        });
        
        System.out.println("Highway Simulator launched in a new window.");
        System.out.println("Note: Mileage changes in the simulator will update the fleet vehicles.");
    }
}
