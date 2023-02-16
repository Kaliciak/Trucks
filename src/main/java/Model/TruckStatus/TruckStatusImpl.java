package Model.TruckStatus;

import Model.Gate.Gate;
import Model.Truck.Truck;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class TruckStatusImpl implements TruckStatus {
    private final int capacity;
    private final List<Truck> gate1Trucks;
    private final List<Truck> gate2Trucks;
    private final List<Integer> documentsQueue;
    private final Set<Integer> passedTrucksIds;

    public TruckStatusImpl(int capacity, Gate gate1, Gate gate2, List<Truck> documentsQueue, Set<Integer> passedTrucksIds) {
        this.capacity = capacity;
        gate1Trucks = gate1.getTrucks().stream().map(Truck::copyTruck).toList();
        gate2Trucks = gate2.getTrucks().stream().map(Truck::copyTruck).toList();
        this.documentsQueue = documentsQueue.stream().map(Truck::getId).toList();
        this.passedTrucksIds = new HashSet<>(passedTrucksIds);
    }
    @Override
    public long getWaitingTime(int truckId) throws TruckInDocumentsQueueException, TruckAlreadyPassedException, TruckNotFoundException {
        var optionalTruck = gate1Trucks.stream().filter(truck -> truck.getId() == truckId).findFirst();
        if(optionalTruck.isPresent()) {
            return optionalTruck.get().getWaitingTime();
        }

        optionalTruck = gate2Trucks.stream().filter(truck -> truck.getId() == truckId).findFirst();
        if(optionalTruck.isPresent()) {
            return optionalTruck.get().getWaitingTime();
        }

        if(documentsQueue.contains(truckId)) {
            throw new TruckInDocumentsQueueException();
        }

        if(passedTrucksIds.contains(truckId)) {
            throw new TruckAlreadyPassedException();
        }

        throw new TruckNotFoundException();
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public List<Truck> getGate1Trucks() {
        return gate1Trucks.stream().map(Truck::copyTruck).toList();
    }

    @Override
    public Truck getGate1CheckedTruck() {
        return null;
    }

    @Override
    public List<Truck> getGate2Trucks() {
        return gate2Trucks.stream().map(Truck::copyTruck).toList();
    }

    @Override
    public Truck getGate2CheckedTruck() {
        return null;
    }

    @Override
    public List<Integer> getDocumentsQueue() {
        return new LinkedList<>(documentsQueue);
    }
}
