# How to run the project
To run this project you need to have the newest gradle version. Then you can run the project with
```
gradle run --console=plain
```

# Instruction
After you successfully run the project, you should see the following message:
```
Welcome to the truck customs simulation!
Type "help" to see available commands.
```
Then you will be able to run the following commands:
* help - list all available commands.
* arrive [positive integer] - make a new truck with a given weight arrive. The weight has to be a positive integer.
* status - print the current status of the system. You will see information about 3 different queues:
  * queue to gate 1,
  * queue to gate 2,
  * queue to the gate, where the documents are checked.
  
  The first truck in each queue is listed at the top, and the last one is listed at the bottom. The first truck in the queue to the gate is the one being checked.
* step [optional positive integer] - advances the simulation by the given numbers of time units. If no number is given, the default value is 1.
* waitingTime [integer] - get the estimated waiting time at the moment of the truck with a given id.
* exit - finish the simulation and close the program.

# Assumptions
In this simulation I consider the following model:
* Each of the 2 main gates has at most 5 trucks waiting in the queue, and at most one truck being checked at the moment.
When the gate finishes checking one truck, the next truck from the front of the queue starts being checked immediately.
It is forbidden for the gate to not check any truck, while the queue is not empty.
* The service doesn't know the weights of the trucks waiting in the queue at the gate, where the documents are checked.
Trucks are weighted at this gate, and the decision to which gate direct them is made there.
Therefore, the estimated waiting time is computed only for the trucks assigned to the gate's queue.
* After its documents are checked, the truck needs to be assigned to one of the gate's queue unless both of them are full.
In this case, it waits for one of the queues to move and make space, in the meantime blocking the documents gate.
* Checking the truck's documents is instant, it takes no time.
* The trucks can change the queues only in the process of swapping with other truck in the same position. 
The truck can't move to the other queue, even if there is an empty space.

# Strategy
The system assigns new trucks dynamically. 
When the new truck arrives, it tries to swap the trucks and assign the new one to the queue, so that the average waiting time for all the trucks is minimal.
Therefore it doesn't plan its strategy for future trucks, because it doesn't know anything about their weight or arrival time.
It knows only about the truck being currently checked at the documents gate.

What the system tries to do, is to pass the lighter trucks as fast as possible, so that less number of trucks have to wait for the heavier trucks to be checked, therefore reducing average waiting time.
It tries to achieve that by swapping the lighter trucks to the queue which will move sooner, so they will advance their position faster than the heavier ones.
