package simulation;

/**
 * Thread class that simulates a vehicle traveling on the highway.
 * Each vehicle runs in its own thread and periodically updates:
 * - Its own mileage and fuel level
 * - The shared Highway Distance Counter
 */
public class VehicleThread extends Thread {
    private final SimulatedVehicle vehicle;
    private final HighwayDistanceCounter counter;
    private volatile boolean running = false;
    private volatile boolean paused = false;
    private final Object pauseLock = new Object();
    
    // Update interval in milliseconds (approximately 1 second)
    private static final int UPDATE_INTERVAL = 1000;
    
    /**
     * Creates a new vehicle thread.
     * 
     * @param vehicle The vehicle to simulate
     * @param counter The shared highway distance counter
     */
    public VehicleThread(SimulatedVehicle vehicle, HighwayDistanceCounter counter) {
        super("VehicleThread-" + vehicle.getId());
        this.vehicle = vehicle;
        this.counter = counter;
    }
    
    @Override
    public void run() {
        running = true;
        vehicle.setStatus(SimulatedVehicle.VehicleStatus.RUNNING);
        
        while (running) {
            // Check if paused
            synchronized (pauseLock) {
                while (paused && running) {
                    try {
                        pauseLock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
            
            if (!running) break;
            
            // Check if vehicle can travel (has fuel)
            if (vehicle.getStatus() == SimulatedVehicle.VehicleStatus.RUNNING) {
                // Attempt to travel 1 km
                boolean traveled = vehicle.travel();
                
                if (traveled) {
                    // Update shared highway distance counter
                    counter.incrementDistance(1);
                }
                // If travel failed due to fuel, status is already set to OUT_OF_FUEL
            } else if (vehicle.getStatus() == SimulatedVehicle.VehicleStatus.OUT_OF_FUEL) {
                // Vehicle is out of fuel - wait for refueling
                // The GUI will refuel the vehicle
            }
            
            // Wait for the update interval
            try {
                Thread.sleep(UPDATE_INTERVAL);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        vehicle.setStatus(SimulatedVehicle.VehicleStatus.STOPPED);
    }
    
    /**
     * Start the simulation for this vehicle.
     */
    public void startSimulation() {
        if (!isAlive()) {
            start();
        }
    }
    
    /**
     * Pause the vehicle simulation.
     */
    public void pauseSimulation() {
        paused = true;
        vehicle.setStatus(SimulatedVehicle.VehicleStatus.PAUSED);
    }
    
    /**
     * Resume the vehicle simulation.
     */
    public void resumeSimulation() {
        synchronized (pauseLock) {
            paused = false;
            if (vehicle.getStatus() == SimulatedVehicle.VehicleStatus.PAUSED) {
                vehicle.setStatus(SimulatedVehicle.VehicleStatus.RUNNING);
            }
            pauseLock.notifyAll();
        }
    }
    
    /**
     * Stop the vehicle simulation.
     */
    public void stopSimulation() {
        running = false;
        paused = false;
        synchronized (pauseLock) {
            pauseLock.notifyAll();
        }
        // Interrupt after notifying to ensure proper cleanup order
        this.interrupt();
    }
    
    /**
     * Check if the simulation is running.
     */
    public boolean isRunning() {
        return running && isAlive();
    }
    
    /**
     * Check if the simulation is paused.
     */
    public boolean isPaused() {
        return paused;
    }
    
    /**
     * Get the vehicle being simulated.
     */
    public SimulatedVehicle getVehicle() {
        return vehicle;
    }
}
