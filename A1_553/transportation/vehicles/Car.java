package transportation.vehicles;
import transportation.exceptions.*;
import transportation.abstractclasses.*;
import transportation.interfaces.*;

public class Car extends LandVehicle implements FuelConsumable, PassengerCarrier, Maintainable {
    private double fuelLevel;
    private int currentPassengers;
    private boolean maintenanceNeeded;
    private static final int PASSENGER_CAPACITY = 5;
    
    public Car(String id, String model, double maxSpeed, int numWheels) throws InvalidOperationException {
        super(id, model, maxSpeed, numWheels);
        this.fuelLevel = 0;
        this.currentPassengers = 0;
        this.maintenanceNeeded = false;
    }
    
    @Override
    public void move(double distance) throws Exception {
        if (distance < 0) throw new InvalidOperationException("Distance cannot be negative");
        
        double fuelNeeded = distance / calculateFuelEfficiency();
        if (fuelLevel < fuelNeeded) {
            throw new InsufficientFuelException("Not enough fuel for the complete journey");
        }
        
        fuelLevel -= fuelNeeded;
        addMileage(distance);
        System.out.println("Car " + getId() + " driving on road for " + distance + " km");
    }
    
    @Override
    public double calculateFuelEfficiency(){ return 15.0; }
    
    @Override
    public void refuel(double amount) throws InvalidOperationException {
        if (amount <= 0) throw new InvalidOperationException("Fuel amount must be positive");
        fuelLevel += amount;
    }
    
    @Override
    public double getFuelLevel(){ return fuelLevel; }
    
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
    public void boardPassengers(int count) throws OverloadException {
        if (currentPassengers + count > PASSENGER_CAPACITY) {
            throw new OverloadException("Cant board " + count + " passengers. Capacity exceeded");
        }
        currentPassengers += count;
    }
    
    @Override
    public void disembarkPassengers(int count) throws InvalidOperationException {
        if (count > currentPassengers) {
            throw new InvalidOperationException("Cannot disembark more passengers than currently on the vehicle");
        }
        currentPassengers -= count;
    }
    
    @Override
    public int getPassengerCapacity(){ return PASSENGER_CAPACITY; }
    
    @Override
    public int getCurrentPassengers(){ return currentPassengers; }
    
    @Override
    public void scheduleMaintenance(){ maintenanceNeeded = true; }
    
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
        return "Car," + super.toString() + "," + fuelLevel + "," + currentPassengers + "," + maintenanceNeeded;
    }
}