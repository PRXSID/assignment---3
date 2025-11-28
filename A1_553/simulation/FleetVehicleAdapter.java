package simulation;

import transportation.abstractclasses.Vehicle;
import transportation.interfaces.FuelConsumable;
import transportation.exceptions.InvalidOperationException;

/**
 * Adapter class that wraps a Fleet Management Vehicle for use in the Highway Simulation.
 * This integrates Assignment 1-2 (Fleet Management) with Assignment 3 (Highway Simulator).
 * 
 * The adapter allows existing Vehicle objects from the transportation package to be used
 * in the simulation by providing the interface expected by VehicleThread.
 */
public class FleetVehicleAdapter extends SimulatedVehicle {
    private final Vehicle wrappedVehicle;
    private final boolean isFuelConsumable;
    private static final double DEFAULT_MAX_FUEL = 100.0;
    private static final double DEFAULT_FUEL_RATE = 1.0;
    
    /**
     * Creates an adapter for a Fleet Management Vehicle.
     * 
     * @param vehicle The vehicle from the Fleet Management System to wrap
     */
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
    
    /**
     * Creates an adapter for a Fleet Management Vehicle with custom fuel settings.
     * 
     * @param vehicle The vehicle from the Fleet Management System to wrap
     * @param maxFuel Maximum fuel capacity for the simulation
     * @param fuelRate Fuel consumption rate per km
     */
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
    
    /**
     * Get the wrapped Fleet Management Vehicle.
     * 
     * @return The original Vehicle object
     */
    public Vehicle getWrappedVehicle() {
        return wrappedVehicle;
    }
    
    /**
     * Travel one kilometer - overridden to also update the wrapped vehicle's mileage.
     * 
     * @return true if travel was successful, false if not enough fuel
     */
    @Override
    public synchronized boolean travel() {
        boolean result = super.travel();
        
        // If travel was successful, update the wrapped vehicle's mileage as well
        if (result && wrappedVehicle != null) {
            // Update the wrapped vehicle's mileage
            try {
                wrappedVehicle.setMileage(
                    wrappedVehicle.getTotalMileage() + 1,
                    wrappedVehicle.getMileageSinceMaintenance() + 1
                );
            } catch (Exception e) {
                // Ignore mileage update failures
            }
        }
        
        return result;
    }
    
    /**
     * Refuel the vehicle - overridden to also refuel the wrapped vehicle if applicable.
     */
    @Override
    public synchronized void refuel() {
        super.refuel();
        
        // If the wrapped vehicle is fuel consumable, refuel it too
        if (isFuelConsumable && wrappedVehicle != null) {
            try {
                FuelConsumable fuelConsumable = (FuelConsumable) wrappedVehicle;
                // Add enough fuel to reach max capacity
                double currentFuel = fuelConsumable.getFuelLevel();
                double toAdd = getMaxFuel() - currentFuel;
                if (toAdd > 0) {
                    fuelConsumable.refuel(toAdd);
                }
            } catch (InvalidOperationException e) {
                // Ignore refuel failures
            }
        }
    }
    
    /**
     * Refuel with a specific amount - overridden to also refuel the wrapped vehicle.
     * 
     * @param amount Amount of fuel to add
     */
    @Override
    public synchronized void refuel(double amount) {
        super.refuel(amount);
        
        // If the wrapped vehicle is fuel consumable, refuel it too
        if (isFuelConsumable && wrappedVehicle != null && amount > 0) {
            try {
                ((FuelConsumable) wrappedVehicle).refuel(amount);
            } catch (InvalidOperationException e) {
                // Ignore refuel failures
            }
        }
    }
    
    /**
     * Check if the wrapped vehicle supports fuel operations.
     * 
     * @return true if the wrapped vehicle implements FuelConsumable
     */
    public boolean isFuelConsumable() {
        return isFuelConsumable;
    }
    
    @Override
    public String toString() {
        return String.format("[Adapted] %s (%s): Mileage=%.0f km, Fuel=%.1f L, Status=%s", 
                            getName(), getId(), getMileage(), getFuelLevel(), getStatus());
    }
}
