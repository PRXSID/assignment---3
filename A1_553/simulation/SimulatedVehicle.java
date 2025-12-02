package simulation;

public class SimulatedVehicle {
    private final String id;
    private final String name;
    private double mileage;
    private double fuelLevel;
    private final double maxFuel;
    private final double fuelConsumptionRate;
    private VehicleStatus status;
    
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
    
    public SimulatedVehicle(String id, String name, double initialFuel, double maxFuel, double fuelConsumptionRate) {
        this.id = id;
        this.name = name;
        this.mileage = 0;
        this.fuelLevel = initialFuel;
        this.maxFuel = maxFuel;
        this.fuelConsumptionRate = fuelConsumptionRate;
        this.status = VehicleStatus.STOPPED;
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public synchronized double getMileage() {
        return mileage;
    }
    
    public synchronized double getFuelLevel() {
        return fuelLevel;
    }
    
    public double getMaxFuel() {
        return maxFuel;
    }
    
    public synchronized VehicleStatus getStatus() {
        return status;
    }
    
    public synchronized void setStatus(VehicleStatus status) {
        this.status = status;
    }
    
    public synchronized boolean travel() {
        if (fuelLevel >= fuelConsumptionRate) {
            mileage += 1;
            fuelLevel -= fuelConsumptionRate;
            
            if (fuelLevel < fuelConsumptionRate) {
                status = VehicleStatus.OUT_OF_FUEL;
            }
            return true;
        } else {
            status = VehicleStatus.OUT_OF_FUEL;
            return false;
        }
    }
    
    public synchronized void refuel() {
        fuelLevel = maxFuel;
        if (status == VehicleStatus.OUT_OF_FUEL) {
            status = VehicleStatus.RUNNING;
        }
    }
    
    public synchronized void refuel(double amount) {
        fuelLevel = Math.min(fuelLevel + amount, maxFuel);
        if (status == VehicleStatus.OUT_OF_FUEL && fuelLevel >= fuelConsumptionRate) {
            status = VehicleStatus.RUNNING;
        }
    }
    
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
