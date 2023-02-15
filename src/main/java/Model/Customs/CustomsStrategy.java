package Model.Customs;

import Model.Gate.Gate;

public interface CustomsStrategy {
    void computeEstimatedTimes(Gate gate1, Gate gate2);
    void replaceTrucks(Gate gate1, Gate gate2);
}
