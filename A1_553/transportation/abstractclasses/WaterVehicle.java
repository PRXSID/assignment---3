package transportation.abstractclasses;
import transportation.exceptions.InvalidOperationException;

public abstract class WaterVehicle extends Vehicle {
    private boolean hasSail;
    
    public WaterVehicle(String id, String model, double maxSpeed, boolean hasSail) throws InvalidOperationException {
        super(id, model, maxSpeed);
        this.hasSail = hasSail;
    }
    
    public boolean hasSail(){ return hasSail; }
    
    @Override
    public double estimateJourneyTime(double distance){ 
        return (distance / getMaxSpeed()) * 1.15; 
    }
    
    @Override
    public String toString() {
        return super.toString() + "," + hasSail;
    }
}