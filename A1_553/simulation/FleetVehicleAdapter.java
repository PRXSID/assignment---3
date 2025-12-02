package simulation;

import transportation.abstractclasses.Vehicle;
import transportation.interfaces.FuelConsumable;
import transportation.exceptions.InvalidOperationException;

public class FleetVehicleAdapter extends SimulatedVehicle {
    private final Vehicle wrappedVehicle;
    private final boolean isFuelConsumable;
    private static final double DEFAULT_MAX_FUEL = 100.0;
    private static final double DEFAULT_FUEL_RATE = 1.0;
    
    public FleetVehicleAdapter(Vehicle vehicle) {
        super(
            vehicle.getId(),
            vehicle.getModel(),
            vehicle instanceof FuelConsumable ? ((FuelConsumable) vehicle).getFuelLevel() : DEFAULT_MAX_FUEL,
            DEFAULT_MAX_FUEL,
            DEFAULT_FUEL_RATE
        );
        this.wrappedVehicle = vehicle;
        this.isFuelConsumable = vehicle instanceof FuelConsumable;
    }
    
    public FleetVehicleAdapter(Vehicle vehicle, double maxFuel, double fuelRate) {
        super(
            vehicle.getId(),
            vehicle.getModel(),
            vehicle instanceof FuelConsumable ? ((FuelConsumable) vehicle).getFuelLevel() : maxFuel,
            maxFuel,
            fuelRate
        );
        this.wrappedVehicle = vehicle;
        this.isFuelConsumable = vehicle instanceof FuelConsumable;
    }
    
    public Vehicle getWrappedVehicle() {
        return wrappedVehicle;
    }
    
    @Override
    public synchronized boolean travel() {
        boolean result = super.travel();
        
        if (result && wrappedVehicle != null) {
            try {
                wrappedVehicle.setMileage(
                    wrappedVehicle.getTotalMileage() + 1,
                    wrappedVehicle.getMileageSinceMaintenance() + 1
                );
            } catch (Exception e) {
            }
        }
        
        return result;
    }
    
    @Override
    public synchronized void refuel() {
        super.refuel();
        
        if (isFuelConsumable && wrappedVehicle != null) {
            try {
                FuelConsumable fuelConsumable = (FuelConsumable) wrappedVehicle;
                double currentFuel = fuelConsumable.getFuelLevel();
                double toAdd = getMaxFuel() - currentFuel;
                if (toAdd > 0) {
                    fuelConsumable.refuel(toAdd);
                }
            } catch (InvalidOperationException e) {
            }
        }
    }
    
    @Override
    public synchronized void refuel(double amount) {
        super.refuel(amount);
        
        if (isFuelConsumable && wrappedVehicle != null && amount > 0) {
            try {
                ((FuelConsumable) wrappedVehicle).refuel(amount);
            } catch (InvalidOperationException e) {
            }
        }
    }
    
    public boolean isFuelConsumable() {
        return isFuelConsumable;
    }
    
    @Override
    public String toString() {
        return String.format("[Adapted] %s (%s): Mileage=%.0f km, Fuel=%.1f L, Status=%s", 
                            getName(), getId(), getMileage(), getFuelLevel(), getStatus());
    }
}
