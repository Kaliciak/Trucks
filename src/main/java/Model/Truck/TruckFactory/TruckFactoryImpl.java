package Model.Truck.TruckFactory;

import Model.Truck.Truck;
import Model.Truck.TruckImpl;

public class TruckFactoryImpl implements TruckFactory {
    private int id = 0;
    @Override
    public Truck newTruck(int weight) {
        return new TruckImpl(id++, weight);
    }
}
