package Model.Truck;

public interface Truck {
    int getId();
    int getWeight();
    long getWaitingTime();
    void setWaitingTime(long time);
    Truck copyTruck();
}
