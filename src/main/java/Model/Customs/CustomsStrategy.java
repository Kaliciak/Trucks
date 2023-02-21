package Model.Customs;

import Model.Gate.Gate;
import Model.Truck.Truck;

public interface CustomsStrategy {
    void computeEstimatedTimes(Gate gate1, Gate gate2);
    void replaceTrucks(Gate gate1, Gate gate2);
    boolean assignTruck(Truck truck, Gate gate1, Gate gate2);
}
