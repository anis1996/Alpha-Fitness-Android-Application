// IMyAidlInterface.aidl
package edu.sjsu.anis.alphafitness;

// Declare any non-default types here with import statements

interface IMyAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void startWorkout(long startTime);

    void stopWorkout();


}
