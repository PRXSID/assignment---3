package transportation.abstractclasses;
import transportation.exceptions.InvalidOperationException;

public abstract class LandVehicle extends Vehicle {
    private int numWheels;
    
    public LandVehicle(String id, String model, double maxSpeed, int numWheels) throws InvalidOperationException {
        super(id, model, maxSpeed);
        this.numWheels = numWheels;
    }
    
    public int getNumWheels(){ return numWheels; }
    
    @Override
    public double estimateJourneyTime(double distance){ 
        return (distance / getMaxSpeed()) * 1.1; 
    }
    
    @Override
    public String toString() {
        return super.toString() + "," + numWheels;
    }
}