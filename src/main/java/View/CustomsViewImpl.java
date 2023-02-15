package View;

import Model.Customs.CustomsStrategyImpl;
import Model.TruckStatus.TruckStatus;
import ViewModel.CustomsViewModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

public class CustomsViewImpl implements CustomsView {
    private final CustomsViewModel viewModel;
    private final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    final String arriveFormat = "arrive [positive integer]";
    final String statusFormat = "status";
    final String stepFormat = "step";
    final String waitingTimeFormat = "waitingTime [integer]";
    final String exitFormat = "exit";

    public CustomsViewImpl(CustomsViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void printWelcomeMessage() {
        System.out.println("Welcome in truck customs simulation!");
        System.out.println("Type \"help\" to see available commands.");
    }

    @Override
    public boolean readInput() {
        try {
            var line = reader.readLine();
            if(line == null) {
                return false;
            }
            var command = line.split("\\s+");
            if(command.length > 0) {
                switch (command[0].toLowerCase()) {
                    case "exit" -> {
                        return false;
                    }
                    case "arrive" -> handleArrive(command);
                    case "status" -> handleStatus(command);
                    case "step" -> handleStep(command);
                    case "waitingtime" -> handleWaitingTime(command);
                    case "help" -> System.out.printf("Available commands are: %n" +
                                    "\t-%s%n" +
                                    "\t-%s%n" +
                                    "\t-%s%n" +
                                    "\t-%s%n" +
                                    "\t-%s%n",
                            arriveFormat, statusFormat, stepFormat, waitingTimeFormat, exitFormat);
                    default -> System.out.println("Command not recognized. To see available commands type \"help\" ");
                }
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private void handleArrive(String[] args) {
        if(args.length != 2) {
            System.out.printf("Bad number of arguments. Valid format is: %s%n", arriveFormat);
            return;
        }

        try {
            var weight = Integer.parseInt(args[1]);
            if(weight <= 0) {
                System.out.printf("Expected positive integer as an argument. Valid format is: %s%n", arriveFormat);
                return;
            }

            var id = viewModel.arrive(weight);
            System.out.printf("Truck with weight %d successfully arrived with an id %d.%n", weight, id);

        } catch (NumberFormatException e) {
            System.out.printf("Bad argument. Valid format is: %s%n", arriveFormat);
        }
    }

    private void handleStatus(String[] args) {
        if(args.length != 1) {
            System.out.printf("Bad number of arguments. Valid format is: %s%n", statusFormat);
            return;
        }

        var status = viewModel.status();

        // print state of gate 1
        System.out.print("Gate 1 queue end: ");
        var gate1Trucks = new LinkedList<>(status.getGate1Trucks());
        if(!gate1Trucks.isEmpty()) {
            var checkedTruck = gate1Trucks.get(0);
            gate1Trucks.remove(0);
            Collections.reverse(gate1Trucks);
            gate1Trucks.forEach(truck -> System.out.printf("___[id: %d, weight: %d]___", truck.getId(), truck.getWeight()));
            System.out.printf("___[id: %d, weight: %d, customs time left: %d]___", checkedTruck.getId(), checkedTruck.getWeight(), checkedTruck.getWaitingTime());
        }
        System.out.println(" :Gate 1 front");

        // print state of gate 2
        System.out.print("Gate 2 queue end: ");
        var gate2Trucks = new LinkedList<>(status.getGate2Trucks());
        if(!gate2Trucks.isEmpty()) {
            var checkedTruck = gate2Trucks.get(0);
            gate2Trucks.remove(0);
            Collections.reverse(gate2Trucks);
            gate2Trucks.forEach(truck -> System.out.printf("___[id: %d, weight: %d]___", truck.getId(), truck.getWeight()));
            System.out.printf("___[id: %d, weight: %d, customs time left: %d]___", checkedTruck.getId(), checkedTruck.getWeight(), checkedTruck.getWaitingTime());
        }
        System.out.println(" :Gate 2 front");

        // print ids of trucks waiting in documents queue
        System.out.print("Documents queue end: ");
        var documentsQueue = new LinkedList<>(status.getDocumentsQueue());
        Collections.reverse(documentsQueue);
        documentsQueue.forEach(truckId -> System.out.printf("___[id: %d]___", truckId));
        System.out.println(" :Documents queue front");
    }

    private void handleStep(String[] args) {
        if(args.length != 1) {
            System.out.printf("Bad number of arguments. Valid format is: %s%n", stepFormat);
            return;
        }

        viewModel.step();
        System.out.println("Simulation moved by one unit of time");
    }

    private void handleWaitingTime(String[] args) {
        if(args.length != 2) {
            System.out.printf("Bad number of arguments. Valid format is: %s%n", waitingTimeFormat);
            return;
        }

        try {
            var id = Integer.parseInt(args[1]);

            try {
                var time = viewModel.waitingTime(id);
                System.out.printf("Estimated waiting time for truck with id %d is %d time units.%n", id, time);
            } catch (TruckStatus.TruckAlreadyPassedException e) {
                System.out.printf("Truck with id %d has already passed the customs check.%n", id);
            } catch (TruckStatus.TruckInDocumentsQueueException e) {
                System.out.printf("Truck with id %d is waiting in queue to check its documents so its waiting time is unknown.%n", id);
            } catch (TruckStatus.TruckNotFoundException e) {
                System.out.printf("Truck with id %d hasn't been seen so far.%n", id);
            }

        } catch (NumberFormatException e) {
            System.out.printf("Bad argument. Valid format is: %s%n", waitingTimeFormat);
        }
    }
}
