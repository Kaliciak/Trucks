package Model.Gate;

import Model.Truck.Truck;

import java.util.List;

public interface Gate {
    // getTrucks().get(0) is truck being checked by the staff right now
    // other trucks are the trucks waiting in order
    List<Truck> getTrucks();
    Truck findTruck(int id);boolean isFull();
    long waitingTime();
    int getCapacity();

    // adds Truck to end of the gate's queue
    // returns true if there is space in queue
    // returns false otherwise
    boolean pushTruck(Truck truck);

    // returns false if position is incorrect
    // returns true otherwise
    boolean replaceTruck(Truck newTruck, int position);

    // returns true if the queue moved during this step
    // returns false otherwise
    boolean forwardBy(long time);

    Gate copyGate();
}
