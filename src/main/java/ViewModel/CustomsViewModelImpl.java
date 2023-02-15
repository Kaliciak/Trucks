package ViewModel;

import Model.Customs.CustomsManager;
import Model.Customs.CustomsManagerImpl;
import Model.Customs.CustomsStrategy;
import Model.Customs.CustomsStrategyImpl;
import Model.Truck.TruckFactory.TruckFactoryImpl;
import Model.TruckStatus.TruckStatus;

public class CustomsViewModelImpl implements CustomsViewModel {
    private final CustomsManager customsManager;

    public CustomsViewModelImpl(int queueCapacity) {
        var customsStrategy = new CustomsStrategyImpl(queueCapacity);
        var truckFactory = new TruckFactoryImpl();
        customsManager = new CustomsManagerImpl(customsStrategy, truckFactory, queueCapacity);
    }
    @Override
    public int arrive(int weight) {
        return customsManager.addTruck(weight);
    }

    @Override
    public TruckStatus status() {
        return customsManager.getStatus();
    }

    @Override
    public void step() {
        customsManager.step();
    }

    @Override
    public long waitingTime(int truckId) throws TruckStatus.TruckInDocumentsQueueException, TruckStatus.TruckAlreadyPassedException, TruckStatus.TruckNotFoundException {
        return customsManager.getStatus().getWaitingTime(truckId);
    }
}
