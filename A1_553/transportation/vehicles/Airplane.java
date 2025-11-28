package transportation.vehicles;

import transportation.abstractclasses.*;
import transportation.exceptions.*;
import transportation.interfaces.*;

public class Airplane extends AirVehicle implements FuelConsumable, PassengerCarrier, CargoCarrier, Maintainable {
    private double fuelLevel, currentCargo;
    private int currentPassengers;
    private boolean maintenanceNeeded;
    private static final int PASSENGER_CAPACITY = 200;
    private static final double CARGO_CAPACITY = 10000.0;
    
    public Airplane(String id, String model, double maxSpeed, double maxAltitude) throws InvalidOperationException {
        super(id, model, maxSpeed, maxAltitude);
        this.fuelLevel = 0;
        this.currentCargo = 0;
        this.currentPassengers = 0;
        this.maintenanceNeeded = false;
    }
    
    @Override
    public void move(double distance) throws Exception {
        if (distance < 0) throw new InvalidOperationException("Distance cannot be negative");
        
        double fuelNeeded = distance / calculateFuelEfficiency();
        if (fuelLevel < fuelNeeded) {
            throw new InsufficientFuelException("Not enough fuel for the journey");
        }
        
        fuelLevel -= fuelNeeded;
        addMileage(distance);
        System.out.println("Airplane " + getId() + " flying at " + getMaxAltitude() + " m for " + distance + " km");
    }
    
    @Override
    public double calculateFuelEfficiency() { 
        return 5.0; 
    }
    
    @Override
    public void refuel(double amount) throws InvalidOperationException {
        if (amount <= 0) throw new InvalidOperationException("Fuel amount must be positive");
        fuelLevel += amount;
    }
    
    @Override
    public double getFuelLevel() { return fuelLevel; }
    
    @Override
    public double consumeFuel(double distance) throws InsufficientFuelException {
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
    public void boardPassengers(int count) throws OverloadException {
        if (currentPassengers + count > PASSENGER_CAPACITY) {
            throw new OverloadException("Cannot board " + count + " passengers. Capacity exceeded");
        }
        currentPassengers += count;
    }
    
    @Override
    public void disembarkPassengers(int count) throws InvalidOperationException {
        if (count > currentPassengers) {
            throw new InvalidOperationException("Cannot disembark more passengers than currently onboard");
        }
        currentPassengers -= count;
    }
    
    @Override
    public int getPassengerCapacity() { return PASSENGER_CAPACITY; }
    
    @Override
    public int getCurrentPassengers() { return currentPassengers; }
    
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
        return "Airplane," + super.toString() + "," + fuelLevel + "," + currentCargo + "," + 
               currentPassengers + "," + maintenanceNeeded;
    }
}