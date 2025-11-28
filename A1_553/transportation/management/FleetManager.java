package transportation.management;

import java.util.*;
import java.io.*;
import transportation.vehicles.*;
import transportation.abstractclasses.Vehicle;
import transportation.exceptions.*;
import transportation.interfaces.*;
import transportation.utility.*;

public class FleetManager {
    private List<Vehicle> fleet = new ArrayList<>();
    private Set<String> distinctModels = new TreeSet<>();

    public void addVehicle(Vehicle vehicle) throws InvalidOperationException {
        for (Vehicle v : fleet) {
            if (v.getId().equals(vehicle.getId())) {
                throw new InvalidOperationException("Vehicle with ID " + vehicle.getId() + " already exists");
            }
        }
        fleet.add(vehicle);
        distinctModels.add(vehicle.getModel());
    }

    public void removeVehicle(String id) throws InvalidOperationException {
        Vehicle vehicleToRemove = null;
        for (Iterator<Vehicle> iterator = fleet.iterator(); iterator.hasNext();) {
            Vehicle vehicle = iterator.next();
            if (vehicle.getId().equals(id)) {
                iterator.remove();
                vehicleToRemove = vehicle;
                break;
            }
        }
        
        if (vehicleToRemove != null) {
            rebuildDistinctModels();
            return;
        }

        throw new InvalidOperationException("Vehicle with ID " + id + " not found");
    }
    
    private void rebuildDistinctModels() {
        distinctModels.clear();
        for (Vehicle v : fleet) {
            distinctModels.add(v.getModel());
        }
    }

    public Vehicle getVehicleById(String id) {
        for (Vehicle v : fleet) {
            if (v.getId().equals(id)) return v;
        }
        return null;
    }

    public List<Vehicle> getFleet() {
        return new ArrayList<>(fleet);
    }
    
    public Set<String> getDistinctModels() {
        return distinctModels;
    }

    public void startAllJourneys(double distance) {
        for (Vehicle v : fleet) {
            try {
                v.move(distance);
            } catch (Exception e) {
                System.out.println("Could not move vehicle " + v.getId() + ": " + e.getMessage());
            }
        }
    }

    public double getTotalFuelConsumption(double distance) {
        double totalFuel = 0;
        for (Vehicle v : fleet) {
            if (v instanceof FuelConsumable) {
                try {
                    FuelConsumable f = (FuelConsumable) v;
                    totalFuel += f.consumeFuel(distance);
                } catch (Exception e) {
                    System.out.println("Could not calculate fuel for vehicle " + v.getId() + ": " + e.getMessage());
                }
            }
        }
        return totalFuel;
    }

    public void maintainAll() {
        for (Vehicle vehicle : fleet) {
            if (vehicle instanceof Maintainable) {
                Maintainable maintainable = (Maintainable) vehicle;
                if (maintainable.needsMaintenance()) {
                    maintainable.performMaintenance();
                }
            }
        }
    }

    public List<Vehicle> searchByType(Class<?> type) {
        List<Vehicle> result = new ArrayList<>();
        for (Vehicle vehicle : fleet) {
            if (type.isInstance(vehicle)) {
                result.add(vehicle);
            }
        }
        return result;
    }

    public void sortFleetByEfficiency() {
        Collections.sort(fleet);
    }
    
    public void sortFleetByEfficiencyDescending() {
        Collections.sort(fleet, new EfficiencyComparator()); 
    }
    
    public void sortFleetBySpeed() {
        Collections.sort(fleet, new MaxSpeedComparator());
    }

    public void sortFleetByModelName() {
        Collections.sort(fleet, new ModelNameComparator());
    }

    public void sortFleetByTotalMileage() {
        Collections.sort(fleet, new TotalMileageComparator());
    }

      public Vehicle getFastestVehicle() {
        if (fleet.isEmpty()) return null;
        return Collections.max(fleet, Collections.reverseOrder(new MaxSpeedComparator()));
    }
    
    public Vehicle getLeastEfficientVehicle() {
        if (fleet.isEmpty()) return null;
        return Collections.min(fleet);
    }

    public String generateReport() {
        StringBuilder report = new StringBuilder();
        report.append("FLEET REPORT\n");
        report.append("Total vehicles: ").append(fleet.size()).append("\n");

        int cars = searchByType(Car.class).size();
        int trucks = searchByType(Truck.class).size();
        int buses = searchByType(Bus.class).size();
        int airplanes = searchByType(Airplane.class).size();
        int cargoShips = searchByType(CargoShip.class).size();

        report.append("Cars: ").append(cars).append("\n");
        report.append("Trucks: ").append(trucks).append("\n");
        report.append("Buses: ").append(buses).append("\n");
        report.append("Airplanes: ").append(airplanes).append("\n");
        report.append("CargoShips: ").append(cargoShips).append("\n");
        
        report.append("Distinct Vehicle Models: ").append(distinctModels.size()).append("\n");
        
        Vehicle fastest = getFastestVehicle();
        if (fastest != null) {
            report.append("Fastest Vehicle (ID/Model/Speed): ").append(fastest.getId()).append("/").append(fastest.getModel()).append("/").append(String.format("%.1f km/h", fastest.getMaxSpeed())).append("\n");
        }
        
        Vehicle leastEfficient = getLeastEfficientVehicle();
        if (leastEfficient != null) {
            report.append("Least Efficient Vehicle (ID/Model/Efficiency): ").append(leastEfficient.getId()).append("/").append(leastEfficient.getModel()).append("/").append(String.format("%.2f km/l", leastEfficient.calculateFuelEfficiency())).append("\n");
        }


        double totalEfficiency = 0;
        int vehicleCount = 0;
        for (Vehicle v : fleet) {
            totalEfficiency += v.calculateFuelEfficiency();
            vehicleCount++;
        }
        double avgEfficiency = vehicleCount > 0 ? totalEfficiency / vehicleCount : 0;
        report.append("Average fuel efficiency: ").append(String.format("%.2f km/l", avgEfficiency)).append("\n");

        double totalMileage = 0;
        for (Vehicle v : fleet) {
            totalMileage += v.getTotalMileage(); 
        }
        report.append("Total mileage: ").append(String.format("%.1f km", totalMileage)).append("\n");

        long maintenanceNeeded = 0;
        for (Vehicle v : fleet) {
            if (v instanceof Maintainable) {
                if (((Maintainable) v).needsMaintenance()) {
                    maintenanceNeeded++;
                }
            }
        }
        report.append("Vehicles needing maintenance: ").append(maintenanceNeeded).append("\n");

        return report.toString();
    }

    public List<Vehicle> getVehiclesNeedingMaintenance() {
        List<Vehicle> result = new ArrayList<>();
        for (Vehicle vehicle : fleet) {
            if (vehicle instanceof Maintainable) {
                if (((Maintainable) vehicle).needsMaintenance()) {
                    result.add(vehicle);
                }
            }
        }
        return result;
    }

    // Using try-catch-finally for manual resource closing.
    public void saveToFile(String filename) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter(filename));
            writer.println("Type,Id,Model,MaxSpeed,TotalMileage,MileageSinceMaintenance,Field1,Field2,Field3,Field4,Field5");
            for (Vehicle vehicle : fleet) {
                writer.println(vehicle.toString()); 
            }
            System.out.println("Fleet saved to " + filename);
        } catch (IOException e) {
            System.out.println("Error saving fleet: " + e.getMessage());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    // Using try-catch-finally for manual resource closing.
    public void loadFromFile(String filename) {
        BufferedReader reader = null;
        fleet.clear();
        distinctModels.clear();

        try {
            reader = new BufferedReader(new FileReader(filename));
            String line = reader.readLine(); 
            while ((line = reader.readLine()) != null) {
                try {
                    Vehicle vehicle = createVehicleFromString(line);
                    if (vehicle != null) {
                        fleet.add(vehicle);
                        distinctModels.add(vehicle.getModel());
                    }
                } catch (Exception e) {
                    System.out.println("Error parsing vehicle: " + e.getMessage());
                }
            }
            System.out.println("Fleet loaded from " + filename);
        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found (" + filename + "). Please check the file path.");
        } catch (IOException e) {
            System.out.println("Error loading fleet: " + e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.out.println("Error closing file reader: " + e.getMessage());
                }
            }
        }
    }

    private Vehicle createVehicleFromString(String data) throws InvalidOperationException {
        String[] parts = data.split(",", -1);
        if (parts.length < 6) return null; 

        String type = parts[0];
        String id = parts[1];
        String model = parts[2];
        double maxSpeed = Double.parseDouble(parts[3]);
        double totalMileage = Double.parseDouble(parts[4]);
        double mileageSinceMaintenance = Double.parseDouble(parts[5]);

        Vehicle vehicle = null;
        
        int numWheels;
        double fuelLevel;
        double cargo;
        int passengers;
        boolean maint;

        try {
            switch (type) {
                case "Car":
                    numWheels = Integer.parseInt(parts[6]);
                    fuelLevel = Double.parseDouble(parts[7]);
                    passengers = Integer.parseInt(parts[8]);
                    maint = Boolean.parseBoolean(parts[9]);

                    Car car = new Car(id, model, maxSpeed, numWheels);
                    if (fuelLevel > 0.0) ((FuelConsumable) car).refuel(fuelLevel);
                    if (passengers > 0) ((PassengerCarrier) car).boardPassengers(passengers); 
                    if (maint) ((Maintainable) car).scheduleMaintenance();
                    vehicle = car;
                    break;

                case "Truck":
                    numWheels = Integer.parseInt(parts[6]);
                    fuelLevel = Double.parseDouble(parts[7]);
                    cargo = Double.parseDouble(parts[8]);
                    maint = Boolean.parseBoolean(parts[9]);

                    Truck truck = new Truck(id, model, maxSpeed, numWheels);
                    if (fuelLevel > 0.0) ((FuelConsumable) truck).refuel(fuelLevel);
                    if (cargo > 0.0) ((CargoCarrier) truck).loadCargo(cargo);
                    if (maint) ((Maintainable) truck).scheduleMaintenance();
                    vehicle = truck;
                    break;

                case "Bus":
                    numWheels = Integer.parseInt(parts[6]);
                    fuelLevel = Double.parseDouble(parts[7]);
                    cargo = Double.parseDouble(parts[8]);
                    passengers = Integer.parseInt(parts[9]);
                    maint = Boolean.parseBoolean(parts[10]);

                    Bus bus = new Bus(id, model, maxSpeed, numWheels);
                    if (fuelLevel > 0.0) ((FuelConsumable) bus).refuel(fuelLevel);
                    if (cargo > 0.0) ((CargoCarrier) bus).loadCargo(cargo);
                    if (passengers > 0) ((PassengerCarrier) bus).boardPassengers(passengers);
                    if (maint) ((Maintainable) bus).scheduleMaintenance();
                    vehicle = bus;
                    break;

                case "Airplane":
                    double maxAltitude = Double.parseDouble(parts[6]);
                    fuelLevel = Double.parseDouble(parts[7]);
                    cargo = Double.parseDouble(parts[8]);
                    passengers = Integer.parseInt(parts[9]);
                    maint = Boolean.parseBoolean(parts[10]);

                    Airplane plane = new Airplane(id, model, maxSpeed, maxAltitude);
                    if (fuelLevel > 0.0) ((FuelConsumable) plane).refuel(fuelLevel);
                    if (cargo > 0.0) ((CargoCarrier) plane).loadCargo(cargo);
                    if (passengers > 0) ((PassengerCarrier) plane).boardPassengers(passengers);
                    if (maint) ((Maintainable) plane).scheduleMaintenance();
                    vehicle = plane;
                    break;

                case "CargoShip":
                    boolean hasSail = Boolean.parseBoolean(parts[6]);
                    cargo = Double.parseDouble(parts[7]);
                    fuelLevel = Double.parseDouble(parts[8]);
                    maint = Boolean.parseBoolean(parts[9]);

                    CargoShip ship = new CargoShip(id, model, maxSpeed, hasSail);
                    if (!hasSail && fuelLevel > 0.0) ((FuelConsumable) ship).refuel(fuelLevel);
                    if (cargo > 0.0) ((CargoCarrier) ship).loadCargo(cargo);
                    if (maint) ((Maintainable) ship).scheduleMaintenance();
                    vehicle = ship;
                    break;
            }

            if (vehicle != null) {
                vehicle.setMileage(totalMileage, mileageSinceMaintenance);
            }

            return vehicle;

        } catch (Exception e) {
            System.out.println("Error creating vehicle: " + e.getMessage() + " from data: " + data);
            return null;
        }
    }
}
