package Model.Truck.TruckFactory;

import Model.Truck.Truck;

public interface TruckFactory {
    Truck newTruck(int weight);
}
