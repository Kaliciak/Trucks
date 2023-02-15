package ViewModel;

import Model.TruckStatus.TruckStatus;

public interface CustomsViewModel {
    int arrive(int weight);
    TruckStatus status();
    void step();
    long waitingTime(int truckId) throws TruckStatus.TruckInDocumentsQueueException, TruckStatus.TruckAlreadyPassedException, TruckStatus.TruckNotFoundException;
}
