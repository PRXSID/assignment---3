package simulation;

/**
 * Represents a simulated vehicle for the highway simulation.
 * Maintains vehicle state including ID, mileage, fuel level, and operational status.
 */
public class SimulatedVehicle {
    private final String id;
    private final String name;
    private double mileage;
    private double fuelLevel;
    private final double maxFuel;
    private final double fuelConsumptionRate; // fuel consumed per km
    private VehicleStatus status;
    
    /**
     * Possible operational statuses for a vehicle.
     */
    public enum VehicleStatus {
        RUNNING("Running"),
        PAUSED("Paused"),
        OUT_OF_FUEL("Out-of-Fuel"),
        STOPPED("Stopped");
        
        private final String displayName;
        
        VehicleStatus(String displayName) {
            this.displayName = displayName;
        }
        
        @Override
        public String toString() {
            return displayName;
        }
    }
    
    /**
     * Creates a new simulated vehicle.
     * 
     * @param id Unique vehicle identifier
     * @param name Vehicle name/model
     * @param initialFuel Initial fuel level
     * @param maxFuel Maximum fuel capacity
     * @param fuelConsumptionRate Fuel consumed per kilometer
     */
    public SimulatedVehicle(String id, String name, double initialFuel, double maxFuel, double fuelConsumptionRate) {
        this.id = id;
        this.name = name;
        this.mileage = 0;
        this.fuelLevel = initialFuel;
        this.maxFuel = maxFuel;
        this.fuelConsumptionRate = fuelConsumptionRate;
        this.status = VehicleStatus.STOPPED;
    }
    
    /**
     * Get the vehicle ID.
     */
    public String getId() {
        return id;
    }
    
    /**
     * Get the vehicle name.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Get current mileage traveled.
     */
    public synchronized double getMileage() {
        return mileage;
    }
    
    /**
     * Get current fuel level.
     */
    public synchronized double getFuelLevel() {
        return fuelLevel;
    }
    
    /**
     * Get maximum fuel capacity.
     */
    public double getMaxFuel() {
        return maxFuel;
    }
    
    /**
     * Get the current operational status.
     */
    public synchronized VehicleStatus getStatus() {
        return status;
    }
    
    /**
     * Set the operational status.
     */
    public synchronized void setStatus(VehicleStatus status) {
        this.status = status;
    }
    
    /**
     * Travel one kilometer (if fuel is available).
     * 
     * @return true if travel was successful, false if not enough fuel to start
     */
    public synchronized boolean travel() {
        if (fuelLevel >= fuelConsumptionRate) {
            mileage += 1;
            fuelLevel -= fuelConsumptionRate;
            
            // Check if we have enough fuel for the next trip
            if (fuelLevel < fuelConsumptionRate) {
                status = VehicleStatus.OUT_OF_FUEL;
            }
            // Return true since we did travel 1km successfully
            return true;
        } else {
            status = VehicleStatus.OUT_OF_FUEL;
            return false;
        }
    }
    
    /**
     * Refuel the vehicle to full capacity.
     */
    public synchronized void refuel() {
        fuelLevel = maxFuel;
        // If was out of fuel, set to running
        if (status == VehicleStatus.OUT_OF_FUEL) {
            status = VehicleStatus.RUNNING;
        }
    }
    
    /**
     * Refuel the vehicle with a specific amount.
     * 
     * @param amount Amount of fuel to add
     */
    public synchronized void refuel(double amount) {
        fuelLevel = Math.min(fuelLevel + amount, maxFuel);
        // If was out of fuel and now has enough fuel, set to running
        if (status == VehicleStatus.OUT_OF_FUEL && fuelLevel >= fuelConsumptionRate) {
            status = VehicleStatus.RUNNING;
        }
    }
    
    /**
     * Reset the vehicle to initial state.
     * 
     * @param initialFuel The initial fuel level
     */
    public synchronized void reset(double initialFuel) {
        this.mileage = 0;
        this.fuelLevel = initialFuel;
        this.status = VehicleStatus.STOPPED;
    }
    
    @Override
    public String toString() {
        return String.format("%s (%s): Mileage=%.0f km, Fuel=%.1f L, Status=%s", 
                            name, id, mileage, fuelLevel, status);
    }
}
