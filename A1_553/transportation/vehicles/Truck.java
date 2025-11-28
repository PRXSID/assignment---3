package transportation.vehicles;

import transportation.abstractclasses.*;
import transportation.exceptions.*;
import transportation.interfaces.*;

public class Truck extends LandVehicle implements FuelConsumable, CargoCarrier, Maintainable {
    private double fuelLevel, currentCargo;
    private boolean maintenanceNeeded;
    private static final double CARGO_CAPACITY = 5000.0;

    public Truck(String id, String model, double maxSpeed, int numWheels) throws InvalidOperationException {
        super(id, model, maxSpeed, numWheels);
        this.fuelLevel = 0;
        this.currentCargo = 0;
        this.maintenanceNeeded = false;
    }

    @Override
    public void move(double distance) throws Exception {
        if (distance < 0) throw new InvalidOperationException("Distance cannot be negative");

        double efficiency = calculateFuelEfficiency();
        if (currentCargo > CARGO_CAPACITY * 0.5) efficiency *= 0.9;

        double fuelNeeded = distance / efficiency;
        if (fuelLevel < fuelNeeded) throw new InsufficientFuelException("Not enough fuel for the journey");

        fuelLevel -= fuelNeeded;
        addMileage(distance);
        System.out.println("Truck " + getId() + " hauling cargo for " + distance + " km");
    }

    @Override
    public double calculateFuelEfficiency() { return 8.0; }

    @Override
    public void refuel(double amount) throws InvalidOperationException {
        if (amount <= 0) throw new InvalidOperationException("Fuel amount must be positive");
        fuelLevel += amount;
    }

    @Override
    public double getFuelLevel() { return fuelLevel; }

    @Override
    public double consumeFuel(double distance) throws InsufficientFuelException {
        double efficiency = calculateFuelEfficiency();
        if (currentCargo > CARGO_CAPACITY * 0.5) efficiency *= 0.9;

        double fuelNeeded = distance / efficiency;
        if (fuelLevel < fuelNeeded) throw new InsufficientFuelException("Not enough fuel");
        fuelLevel -= fuelNeeded;
        return fuelNeeded;
    }

    @Override
    public void loadCargo(double weight) throws OverloadException, InvalidOperationException {
        if (weight <= 0) throw new InvalidOperationException("Cargo weight must be positive");
        if (currentCargo + weight > CARGO_CAPACITY) throw new OverloadException("Cannot load " + weight + " kg. Capacity exceeded");
        currentCargo += weight;
    }

    @Override
    public void unloadCargo(double weight) throws InvalidOperationException {
        if (weight <= 0) throw new InvalidOperationException("Cargo weight must be positive");
        if (weight > currentCargo) throw new InvalidOperationException("Cannot unload more cargo than currently loaded");
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
        maintenanceNeeded = false;
        resetMileageSinceMaintenance();
    }

    @Override
    public String toString() {
        return "Truck," + super.toString() + "," + fuelLevel + "," + currentCargo + "," + maintenanceNeeded;
    }
}
