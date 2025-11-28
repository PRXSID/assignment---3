package transportation.abstractclasses;
import transportation.exceptions.InvalidOperationException;

public abstract class AirVehicle extends Vehicle {
    private double maxAltitude;
    
    public AirVehicle(String id, String model, double maxSpeed, double maxAltitude) throws InvalidOperationException {
        super(id, model, maxSpeed);
        this.maxAltitude = maxAltitude;
    }
    
    public double getMaxAltitude(){ return maxAltitude; }
    
    @Override
    public double estimateJourneyTime(double distance){ 
        return (distance / getMaxSpeed()) * 0.95; 
    }
    
    @Override
    public String toString() {
        return super.toString() + "," + maxAltitude;
    }
}