package Model.Customs;

import Model.TruckStatus.TruckStatus;

public interface CustomsManager {
    void step();
    int addTruck(int weight);
    TruckStatus getStatus();
}
