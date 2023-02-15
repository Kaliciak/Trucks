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
    private final int queueCapacity;
    private final CustomsStrategy customsStrategy;
    private final TruckFactory truckFactory;
    private final Gate gate1;
    private final Gate gate2;
    private final List<Truck> documentsQueue = new LinkedList<>();

    private final Set<Integer> passedTrucks = new HashSet<>();

    public CustomsManagerImpl(CustomsStrategy customsStrategy, TruckFactory truckFactory, int queueCapacity) {
        this.queueCapacity = queueCapacity;
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
        return new TruckStatusImpl(queueCapacity, gate1, gate2, documentsQueue, passedTrucks);
    }

    private void assignTrucks() {
        while(assignTruck());
    }

    private boolean assignTruck() {
        // TODO validate this, maybe we dont wont to always fill the empty queue
        // if there is no truck to add or both gates are full
        if(documentsQueue.isEmpty() || (gate1.isFull() && gate2.isFull())) {
            return false;
        }

        Truck truck = documentsQueue.get(0);

        if(!gate1.isFull() && !gate2.isFull()) {
            // if added to gate 1
            var gate1Copy = gate1.copyGate();
            var gate2Copy = gate2.copyGate();

            gate1Copy.pushTruck(truck.copyTruck());
            customsStrategy.computeEstimatedTimes(gate1Copy, gate2Copy);
            var case1Time = gate1Copy.waitingTime() + gate2Copy.waitingTime();

            // if added to gate 2
            gate1Copy = gate1.copyGate();
            gate2Copy = gate2.copyGate();

            gate2Copy.pushTruck(truck.copyTruck());
            customsStrategy.computeEstimatedTimes(gate1Copy, gate2Copy);
            var case2Time = gate1Copy.waitingTime() + gate2Copy.waitingTime();

            // decide to which gate add given truck
            if(case1Time < case2Time) {
                gate1.pushTruck(truck);
            }
            else {
                gate2.pushTruck(truck);
            }
        }
        else if(!gate1.isFull()) {
            gate1.pushTruck(truck);
        }
        else if(!gate2.isFull()) {
            gate2.pushTruck(truck);
        }

        documentsQueue.remove(0);
        customsStrategy.replaceTrucks(gate1, gate2);
        customsStrategy.computeEstimatedTimes(gate1, gate2);
        return true;
    }
}
