package View;

import Model.TruckStatus.TruckStatus;
import ViewModel.CustomsViewModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CustomsViewImpl implements CustomsView {
    private final CustomsViewModel viewModel;
    private final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    final String arriveFormat = "arrive [positive integer]";
    final String statusFormat = "status";
    final String stepFormat = "step [optional positive integer]";
    final String waitingTimeFormat = "waitingTime [integer]";
    final String exitFormat = "exit";

    public CustomsViewImpl(CustomsViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void printWelcomeMessage() {
        System.out.println("Welcome to the truck customs simulation!");
        System.out.println("Type \"help\" to see available commands.");
    }

    @Override
    public boolean readInput() {
        try {
            System.out.print("> ");
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

        System.out.println("Current status is:");
        System.out.println();

        // print state of gate 1
        System.out.println("_______Gate 1 front________");
        status.getGate1Trucks().forEach(truck -> System.out.printf("[id: %d, weight: %d, estimated waiting time: %d]%n", truck.getId(), truck.getWeight(), truck.getWaitingTime()));
        System.out.println("_____Gate 1 queue end______");

        System.out.println();

        // print state of gate 2
        System.out.println("_______Gate 2 front________");
        status.getGate2Trucks().forEach(truck -> System.out.printf("[id: %d, weight: %d, estimated waiting time: %d]%n", truck.getId(), truck.getWeight(), truck.getWaitingTime()));
        System.out.println("_____Gate 2 queue end______");

        System.out.println();

        // print ids of trucks waiting in documents queue
        System.out.println("___Documents queue front___");
        status.getDocumentsQueue().forEach(truckId -> System.out.printf("[id: %d]%n", truckId));
        System.out.println("____Documents queue end____");
        System.out.println();
    }

    private void handleStep(String[] args) {
        if(args.length > 2) {
            System.out.printf("Bad number of arguments. Valid format is: %s%n", stepFormat);
            return;
        }

        if(args.length == 1) {
            viewModel.step();
            System.out.println("Simulation moved by 1 unit of time.");
        }

        else {
            var steps = Integer.parseInt(args[1]);
            if(steps <= 0) {
                System.out.printf("Expected positive integer as an argument. Valid format is: %s%n", stepFormat);
                return;
            }
            for(int i = 0; i < steps; i ++) {
                viewModel.step();
            }
            System.out.printf("Simulation moved by %d units of time.%n", steps);
        }
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
