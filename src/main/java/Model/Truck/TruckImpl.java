package Model.Truck;

public class TruckImpl implements Truck {
    private final int id;
    private final int weight;
    private long waitingTime;

    public TruckImpl(int id, int weight) {
        this.id = id;
        this.weight = weight;
        waitingTime = weight;
    }
    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getWeight() {
        return weight;
    }

    @Override
    public long getWaitingTime() {
        return waitingTime;
    }

    @Override
    public void setWaitingTime(long time) {
        this.waitingTime = time;
    }

    @Override
    public Truck copyTruck() {
        Truck newTruck = new TruckImpl(id, weight);
        newTruck.setWaitingTime(waitingTime);
        return newTruck;
    }
}
