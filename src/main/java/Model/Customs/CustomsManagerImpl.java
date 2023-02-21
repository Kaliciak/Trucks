package Model.Customs;

import Model.Gate.Gate;
import Model.Gate.GateImpl;
import Model.Truck.Truck;
import Model.Truck.TruckFactory.TruckFactory;
import Model.TruckStatus.TruckStatus;
import Model.TruckStatus.TruckStatusImpl;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class CustomsManagerImpl implements CustomsManager {
    private final CustomsStrategy customsStrategy;
    private final TruckFactory truckFactory;
    private final Gate gate1;
    private final Gate gate2;
    private final List<Truck> documentsQueue = new LinkedList<>();

    private final Set<Integer> passedTrucks = new HashSet<>();

    public CustomsManagerImpl(CustomsStrategy customsStrategy, TruckFactory truckFactory, int queueCapacity) {
        this.customsStrategy = customsStrategy;
        this.truckFactory = truckFactory;
        gate1 = new GateImpl(queueCapacity);
        gate2 = new GateImpl(queueCapacity);
    }

    @Override
    public void step() {
        // save the truck id if it passed the customs check
        var truck1Id = gate1.getTrucks().isEmpty() ? null : gate1.getTrucks().get(0).getId();
        var truck2Id = gate2.getTrucks().isEmpty() ? null : gate2.getTrucks().get(0).getId();

        var gate1Result = gate1.forwardBy(1);
        var gate2Result = gate2.forwardBy(1);

        if(gate1Result && truck1Id != null) {
            passedTrucks.add(truck1Id);
        }

        if(gate2Result && truck2Id != null) {
            passedTrucks.add(truck2Id);
        }

        // if any of the queues moved
        if(gate1Result || gate2Result) {
            customsStrategy.replaceTrucks(gate1, gate2);
            assignTrucks();
        }
    }

    @Override
    public int addTruck(int weight) {
        var newTruck = truckFactory.newTruck(weight);
        documentsQueue.add(newTruck);
        assignTrucks();
        return newTruck.getId();
    }

    @Override
    public TruckStatus getStatus() {
        return new TruckStatusImpl(gate1, gate2, documentsQueue, passedTrucks);
    }

    private void assignTrucks() {
        // assign trucks until the queues are full
        while(assignTruck());
    }

    private boolean assignTruck() {
        // if there is no truck to add
        if(documentsQueue.isEmpty()) {
            return false;
        }

        Truck truck = documentsQueue.get(0);
        if(!customsStrategy.assignTruck(truck, gate1, gate2)) {
            return false;
        }
        documentsQueue.remove(0);
        return true;
    }
}
