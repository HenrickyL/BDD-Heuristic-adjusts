package org.ufc.planner.interfaces;

public interface IMetricTracker {
    void start();
    void reset();
    long elapsed();
    void printElapsed();
    boolean checkLimitExceeded();
}
