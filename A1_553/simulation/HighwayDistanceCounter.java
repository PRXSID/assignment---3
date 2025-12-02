package simulation;

import java.util.concurrent.locks.ReentrantLock;

public class HighwayDistanceCounter {
    private int totalDistance = 0;
    private boolean synchronizationEnabled = false;
    private final ReentrantLock lock = new ReentrantLock();
    
    private int expectedIncrements = 0;
    
    public void incrementDistance(int amount) {
        if (synchronizationEnabled) {
            lock.lock();
            try {
                totalDistance += amount;
                expectedIncrements++;
            } finally {
                lock.unlock();
            }
        } else {
            int current = totalDistance;
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            totalDistance = current + amount;
            expectedIncrements++;
        }
    }
    
    public int getTotalDistance() {
        if (synchronizationEnabled) {
            lock.lock();
            try {
                return totalDistance;
            } finally {
                lock.unlock();
            }
        }
        return totalDistance;
    }
    
    public synchronized int getExpectedIncrements() {
        return expectedIncrements;
    }
    
    public void setSynchronizationEnabled(boolean enabled) {
        this.synchronizationEnabled = enabled;
    }
    
    public boolean isSynchronizationEnabled() {
        return synchronizationEnabled;
    }
    
    public void reset() {
        if (synchronizationEnabled) {
            lock.lock();
            try {
                totalDistance = 0;
                expectedIncrements = 0;
            } finally {
                lock.unlock();
            }
        } else {
            totalDistance = 0;
            expectedIncrements = 0;
        }
    }
}
