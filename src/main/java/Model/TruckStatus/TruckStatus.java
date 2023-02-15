package Model.TruckStatus;

import Model.Truck.Truck;

import java.util.List;

public interface TruckStatus {
    long getWaitingTime(int truckId) throws TruckInDocumentsQueueException, TruckAlreadyPassedException, TruckNotFoundException;
    int getCapacity();
    List<Truck> getGate1Trucks();
    Truck getGate1CheckedTruck();
    List<Truck> getGate2Trucks();
    Truck getGate2CheckedTruck();
    List<Integer> getDocumentsQueue();

    class TruckInDocumentsQueueException extends Exception {}
    class TruckAlreadyPassedException extends Exception {}
    class TruckNotFoundException extends Exception {}
}
