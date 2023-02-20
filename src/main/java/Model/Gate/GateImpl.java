package Model.Gate;

import Model.Truck.Truck;

import java.util.LinkedList;
import java.util.List;

public class GateImpl implements Gate {
    private List<Truck> trucks = new LinkedList<>();

    // number of trucks that can wait in the queue
    private final int capacity;

    public GateImpl(int capacity) {
        this.capacity = capacity;
    }

    private GateImpl(Gate otherGate) {
        this.capacity = otherGate.getCapacity();
        trucks = new LinkedList<>(otherGate.getTrucks().stream().map(Truck::copyTruck).toList());
    }

    @Override
    public List<Truck> getTrucks() {
        return new LinkedList<>(trucks);
    }

    @Override
    public Truck findTruck(int id) {
        var optionalTruck = trucks.stream().filter(truck -> truck.getId() == id).findFirst();
        return optionalTruck.orElse(null);
    }

    @Override
    public boolean isFull() {
        // checked truck is also in list but not in queue
        return trucks.size() > capacity;
    }

    @Override
    public boolean pushTruck(Truck truck) {
        if(isFull()) {
            return false;
        }
        if(trucks.isEmpty()) {
            truck.setWaitingTime(truck.getWeight());
        }
        trucks.add(truck);
        return true;
    }

    @Override
    public boolean replaceTruck(Truck newTruck, int position) {
        if(position < 0 || position >= trucks.size()) {
            return false;
        }
        trucks.set(position, newTruck);
        return true;
    }

    // time needs to be less or equal to checked truck waiting time
    // otherwise the behaviour is undefined
    @Override
    public boolean forwardBy(long time) {
        // reduce waiting time of each of the truck
        trucks.forEach(truck -> truck.setWaitingTime(truck.getWaitingTime() - time));

        // if checked truck has finished, then move the queue
        if(trucks.size() > 0 && trucks.get(0).getWaitingTime() <= 0) {
            trucks.remove(0);
            return true;
        }

        return false;
    }

    @Override
    public GateImpl copyGate() {
        return new GateImpl(this);
    }

    @Override
    public long waitingTime() {
        return trucks.stream().mapToLong(Truck::getWaitingTime).sum();
    }

    @Override
    public int getCapacity() {
        return capacity;
    }
}
