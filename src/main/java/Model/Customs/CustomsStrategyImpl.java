package Model.Customs;

import Model.Gate.Gate;
import Model.Truck.Truck;

import java.util.function.IntConsumer;

import static java.lang.Math.min;

public class CustomsStrategyImpl implements CustomsStrategy {
    private final int queueCapacity;

    public CustomsStrategyImpl(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }
    @Override
    public void computeEstimatedTimes(Gate gate1, Gate gate2) {
        var timePassed = 0L;
        var gate1Copy = gate1.copyGate();
        var gate2Copy = gate2.copyGate();
        replaceTrucks(gate1Copy, gate2Copy);

        // simulate the traffic
        while(!gate1Copy.getTrucks().isEmpty() || !gate2Copy.getTrucks().isEmpty()) {
            var forwardTime = getForwardTime(gate1Copy, gate2Copy);
            timePassed += forwardTime;

            long finalTimePassed = timePassed;
            // forward the gates queues
            forwardGate(gate1Copy, forwardTime, truckId -> setWaitingTimeWithId(truckId, finalTimePassed, gate1, gate2));
            forwardGate(gate2Copy, forwardTime, truckId -> setWaitingTimeWithId(truckId, finalTimePassed, gate1, gate2));

            replaceTrucks(gate1Copy, gate2Copy);
        }
    }

    private long getForwardTime(Gate gate1, Gate gate2) {
        if(!gate1.getTrucks().isEmpty() && !gate2.getTrucks().isEmpty()) {
            return min(gate1.getTrucks().get(0).getWaitingTime(), gate2.getTrucks().get(0).getWaitingTime());
        }
        if(!gate1.getTrucks().isEmpty()) {
            return gate1.getTrucks().get(0).getWaitingTime();
        }
        if(!gate2.getTrucks().isEmpty()) {
            return gate2.getTrucks().get(0).getWaitingTime();
        }
        throw new IndexOutOfBoundsException();
    }

    private void setWaitingTimeWithId(int truckId, long time, Gate gate1, Gate gate2) {
        var foundTruck = gate1.findTruck(truckId);
        if(foundTruck == null) {
            foundTruck = gate2.findTruck(truckId);
        }
        foundTruck.setWaitingTime(time);
    }

    private void forwardGate(Gate gate, long forwardTime, IntConsumer setWaitingTimeFunc) {
        if(!gate.getTrucks().isEmpty()) {
            var truck = gate.getTrucks().get(0);
            // if queue moved
            if(gate.forwardBy(forwardTime)) {
                // new truck starts being checked
                if(!gate.getTrucks().isEmpty()) {
                    var newTruck = gate.getTrucks().get(0);
                    newTruck.setWaitingTime(newTruck.getWeight());
                }

                // set waiting time to the removed truck
                setWaitingTimeFunc.accept(truck.getId());
            }
        }
    }

    @Override
    public void replaceTrucks(Gate gate1, Gate gate2) {
        // swap trucks
        // lighter trucks goes to the gate with less time to move, so they are checked sooner
        if(!gate1.getTrucks().isEmpty() && !gate2.getTrucks().isEmpty()) {
            var gate1moveTime = gate1.getTrucks().get(0).getWaitingTime();
            var gate2moveTime = gate2.getTrucks().get(0).getWaitingTime();
            for(int i = 2; i <= queueCapacity; i ++) {
                // if any queue has empty space at this index
                if(gate1.getTrucks().size() <= i || gate2.getTrucks().size() <= i) {
                    break;
                }

                var truck1 = gate1.getTrucks().get(i);
                var truck2 = gate2.getTrucks().get(i);

                // if gate1 truck is lighter and gate2 moves sooner
                // or
                // if gate2 truck is lighter and gate1 moves sooner
                if( (truck1.getWeight() < truck2.getWeight()
                        && gate1moveTime > gate2moveTime)
                    || (truck1.getWeight() > truck2.getWeight()
                        && gate1moveTime < gate2moveTime) )
                {
                    swapTrucks(gate1, gate2, truck1, truck2, i);
                }
            }
        }
    }

    private void swapTrucks(Gate gate1, Gate gate2, Truck truck1, Truck truck2, int index) {
        gate1.replaceTruck(truck2, index);
        gate2.replaceTruck(truck1, index);
    }

    @Override
    public boolean assignTruck(Truck truck, Gate gate1, Gate gate2) {
        // if both gates are full
        if(gate1.isFull() && gate2.isFull()) {
            return false;
        }

        if(!gate1.isFull() && !gate2.isFull()) {
            // consider cases to decide to which gate assign given truck

            // if added to gate 1
            var case1Time = estimateTimeWithAddedTruck(gate1, gate2, truck);

            // if added to gate 2
            var case2Time = estimateTimeWithAddedTruck(gate2, gate1, truck);

            // decide to which gate assign given truck
            if(case1Time <= case2Time) {
                gate1.pushTruck(truck);
            }
            else {
                gate2.pushTruck(truck);
            }
        }
        // if one of the gates is full
        else if(!gate1.isFull()) {
            gate1.pushTruck(truck);
        }
        else if(!gate2.isFull()) {
            gate2.pushTruck(truck);
        }

        replaceTrucks(gate1, gate2);
        computeEstimatedTimes(gate1, gate2);
        return true;
    }

    private long estimateTimeWithAddedTruck(Gate extendedGate, Gate otherGate, Truck truck) {
        var extendedGateCopy = extendedGate.copyGate();
        var otherGateCopy = otherGate.copyGate();

        extendedGateCopy.pushTruck(truck.copyTruck());
        computeEstimatedTimes(extendedGateCopy, otherGateCopy);
        return extendedGateCopy.waitingTime() + otherGateCopy.waitingTime();
    }
}
