package transportation.vehicles;
import transportation.exceptions.*;
import transportation.abstractclasses.*;
import transportation.interfaces.*;

public class CargoShip extends WaterVehicle implements CargoCarrier, Maintainable, FuelConsumable {
    private double currentCargo, fuelLevel;
    private boolean maintenanceNeeded;
    private static final double CARGO_CAPACITY = 50000.0;
    
    public CargoShip(String id, String model, double maxSpeed, boolean hasSail) throws InvalidOperationException {
        super(id, model, maxSpeed, hasSail);
        this.currentCargo = 0;
        this.fuelLevel = hasSail ? 0 : 100;
        this.maintenanceNeeded = false;
    }
    
    @Override
    public void move(double distance) throws Exception {
        if (distance < 0) throw new InvalidOperationException("Distance cannot be negative");
        
        if (!hasSail()) {
            double fuelNeeded = distance / calculateFuelEfficiency();
            if (fuelLevel < fuelNeeded) {
                throw new InsufficientFuelException("Not enough fuel for the journey");
            }
            fuelLevel -= fuelNeeded;
        }
        
        addMileage(distance);
        System.out.println("CargoShip " + getId() + " sailing with cargo for " + distance + " km");
    }
    
    @Override
    public double calculateFuelEfficiency() { 
        return hasSail() ? 0 : 4.0; 
    }
    
    @Override
    public void refuel(double amount) throws InvalidOperationException {
        if (hasSail()) throw new InvalidOperationException("Sail-powered ships don't need fuel");
        if (amount <= 0) throw new InvalidOperationException("Fuel amount must be positive");
        fuelLevel += amount;
    }
    
    @Override
    public double getFuelLevel() { return fuelLevel; }
    
    @Override
    public double consumeFuel(double distance) throws InsufficientFuelException {
        if (hasSail()) return 0;
        
        double fuelNeeded = distance / calculateFuelEfficiency();
        if (fuelLevel < fuelNeeded) {
            throw new InsufficientFuelException("Not enough fuel");
        }
        fuelLevel -= fuelNeeded;
        return fuelNeeded;
    }
    
    @Override
    public void loadCargo(double weight) throws OverloadException, InvalidOperationException {
        if (weight <= 0) throw new InvalidOperationException("Cargo weight must be positive");
        if (currentCargo + weight > CARGO_CAPACITY) {
            throw new OverloadException("Cannot load " + weight + " kg. Capacity exceeded");
        }
        currentCargo += weight;
    }
    
    @Override
    public void unloadCargo(double weight) throws InvalidOperationException {
        if (weight <= 0) throw new InvalidOperationException("Cargo weight must be positive");
        if (weight > currentCargo) {
            throw new InvalidOperationException("Cannot unload more cargo than currently loaded");
        }
        currentCargo -= weight;
    }
    
    @Override
    public double getCargoCapacity() { return CARGO_CAPACITY; }
    
    @Override
    public double getCurrentCargo() { return currentCargo; }
    
    @Override
    public void scheduleMaintenance() { maintenanceNeeded = true; }
    
    @Override
    public boolean needsMaintenance() {
    return mileageSinceMaintenance > 10000 || maintenanceNeeded;
}

    
    @Override
    public void performMaintenance() {
    maintenanceNeeded = false;     // or your existing logic
    resetMileageSinceMaintenance(); // reset only mileageSinceMaintenance
}
    
    @Override
    public String toString() {
        return "CargoShip," + super.toString() + "," + currentCargo + "," + fuelLevel + "," + maintenanceNeeded;
    }
}