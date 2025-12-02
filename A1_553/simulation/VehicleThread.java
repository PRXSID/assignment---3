package simulation;

public class VehicleThread extends Thread {
    private final SimulatedVehicle vehicle;
    private final HighwayDistanceCounter counter;
    private volatile boolean running = false;
    private volatile boolean paused = false;
    private final Object pauseLock = new Object();
    
    private static final int UPDATE_INTERVAL = 1000;
    
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
            
            if (vehicle.getStatus() == SimulatedVehicle.VehicleStatus.RUNNING) {
                boolean traveled = vehicle.travel();
                
                if (traveled) {
                    counter.incrementDistance(1);
                }
            } else if (vehicle.getStatus() == SimulatedVehicle.VehicleStatus.OUT_OF_FUEL) {
            }
            
            try {
                Thread.sleep(UPDATE_INTERVAL);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        vehicle.setStatus(SimulatedVehicle.VehicleStatus.STOPPED);
    }
    
    public void startSimulation() {
        if (!isAlive()) {
            start();
        }
    }
    
    public void pauseSimulation() {
        paused = true;
        vehicle.setStatus(SimulatedVehicle.VehicleStatus.PAUSED);
    }
    
    public void resumeSimulation() {
        synchronized (pauseLock) {
            paused = false;
            if (vehicle.getStatus() == SimulatedVehicle.VehicleStatus.PAUSED) {
                vehicle.setStatus(SimulatedVehicle.VehicleStatus.RUNNING);
            }
            pauseLock.notifyAll();
        }
    }
    
    public void stopSimulation() {
        running = false;
        paused = false;
        synchronized (pauseLock) {
            pauseLock.notifyAll();
        }
        this.interrupt();
    }
    
    public boolean isRunning() {
        return running && isAlive();
    }
    
    public boolean isPaused() {
        return paused;
    }
    
    public SimulatedVehicle getVehicle() {
        return vehicle;
    }
}
