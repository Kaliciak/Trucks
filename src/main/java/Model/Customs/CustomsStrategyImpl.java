package Model.Customs;

import Model.Gate.Gate;
import Model.Truck.Truck;

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

            if(!gate1Copy.getTrucks().isEmpty()) {
                var truck = gate1Copy.getTrucks().get(0);
                // if queue moved
                if(gate1Copy.forwardBy(forwardTime)) {
                    // new truck starts being checked
                    if(!gate1Copy.getTrucks().isEmpty()) {
                        var newTruck = gate1Copy.getTrucks().get(0);
                        newTruck.setWaitingTime(newTruck.getWeight());
                    }

                    // set waiting time to the removed truck
                    var foundTruck = gate1.findTruck(truck.getId());
                    if(foundTruck == null) {
                        foundTruck = gate2.findTruck(truck.getId());
                    }
                    foundTruck.setWaitingTime(timePassed);
                }
            }

            if(!gate2Copy.getTrucks().isEmpty()) {
                var truck = gate2Copy.getTrucks().get(0);
                // if queue moved
                if(gate2Copy.forwardBy(forwardTime)) {
                    // new truck starts being checked
                    if(!gate2Copy.getTrucks().isEmpty()) {
                        var newTruck = gate2Copy.getTrucks().get(0);
                        newTruck.setWaitingTime(newTruck.getWeight());
                    }

                    // set waiting time to the removed truck
                    var foundTruck = gate1.findTruck(truck.getId());
                    if(foundTruck == null) {
                        foundTruck = gate2.findTruck(truck.getId());
                    }
                    foundTruck.setWaitingTime(timePassed);
                }
            }

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

    @Override
    public void replaceTrucks(Gate gate1, Gate gate2) {
        // swap trucks
        // lighter trucks goes to the gate with less time to move, so they are checked quicker
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

        // TODO: consider empty spaces
    }

    private void swapTrucks(Gate gate1, Gate gate2, Truck truck1, Truck truck2, int index) {
        gate1.replaceTruck(truck2, index);
        gate2.replaceTruck(truck1, index);
    }
}
