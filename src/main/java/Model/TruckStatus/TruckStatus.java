package Model.TruckStatus;

import Model.Truck.Truck;

import java.util.List;

public interface TruckStatus {
    long getWaitingTime(int truckId) throws TruckInDocumentsQueueException, TruckAlreadyPassedException, TruckNotFoundException;
    List<Truck> getGate1Trucks();
    List<Truck> getGate2Trucks();
    List<Integer> getDocumentsQueue();
    class TruckInDocumentsQueueException extends Exception {}
    class TruckAlreadyPassedException extends Exception {}
    class TruckNotFoundException extends Exception {}
}
