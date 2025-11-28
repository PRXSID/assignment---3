package simulation;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Shared Highway Distance Counter that tracks total kilometers traveled by all vehicles.
 * 
 * This class demonstrates a race condition when synchronization is disabled,
 * and shows correct behavior when synchronization is enabled.
 */
public class HighwayDistanceCounter {
    private int totalDistance = 0;
    private boolean synchronizationEnabled = false;
    private final ReentrantLock lock = new ReentrantLock();
    
    // Track expected vs actual for race condition demonstration
    private int expectedIncrements = 0;
    
    /**
     * Increment the highway distance counter.
     * When synchronization is disabled, this can cause race conditions.
     * When enabled, uses ReentrantLock for thread-safe access.
     */
    public void incrementDistance(int amount) {
        if (synchronizationEnabled) {
            lock.lock();
            try {
                // Thread-safe increment
                totalDistance += amount;
                expectedIncrements++;
            } finally {
                lock.unlock();
            }
        } else {
            // UNSYNCHRONIZED ACCESS - Causes race condition!
            // Reading the current value, adding to it, and writing back
            // are not atomic operations. Multiple threads can read the same
            // value before any writes occur, leading to lost updates.
            int current = totalDistance;
            // Simulate some processing delay to increase chance of race condition
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            totalDistance = current + amount;
            expectedIncrements++;
        }
    }
    
    /**
     * Get the current total distance.
     */
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
    
    /**
     * Get the expected number of increments (for race condition comparison).
     * Note: This is synchronized to prevent race conditions when reading.
     */
    public synchronized int getExpectedIncrements() {
        return expectedIncrements;
    }
    
    /**
     * Enable or disable synchronization.
     */
    public void setSynchronizationEnabled(boolean enabled) {
        this.synchronizationEnabled = enabled;
    }
    
    /**
     * Check if synchronization is enabled.
     */
    public boolean isSynchronizationEnabled() {
        return synchronizationEnabled;
    }
    
    /**
     * Reset the counter to zero.
     */
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
