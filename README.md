# Selevminer

## About the project
Selevminer is a system that allows the user to compose multi-objective optimization, clustering and other algorithms to obtain a selection of declarative models from an event log in ```XES``` format.

## How to import the project
The project is has been developed using Eclipse, so the following instructions will only be reproducible in this IDE. In any case, other systems may allow similar steps:

1. Clone the project with ```git clone <URI> <folder>```.
2. Import the project in your workspace using ```File/Import/Existing Projects into Workspace```.
3. Set up the needed libraries using ```*Right click on project folder*/Build Path/Configure Build Path```. In this menu, select ```Libraries/Add External JARs...``` and select every file inside the ```src/main/resources/lib``` folder.
4. Select Apply and Close.

The project should now be compiling without errors.

## Configuring and running Selevminer
The source file ```src/main/java/selevminer/Main.java``` contains an example of how to use Selevminer. In this file you will find a variable of type ```Seleminer<ProcessModel>``` (i.e. an instance of Selevminer that returns ```ProcessModel```s) that contains all the information needed to run the algorithm.

In order to use Selevminer as is, configure the ```population```, ```generations```, ```timeout```, ```cores``` and ```logPath``` variables. The file also handles exporting the results as CONDEC, a .txt containing the metrics and a .dot file with the automata returned by MINERful, but the output folder must be configured using the ```outpath``` variable.

If you want to extend Selevminer, each configurable algorithm has a corresponding interface:

- Miner: ```src/main/java/selevminer/model/PMMiner.java```
- Clustering: ```src/main/java/selevminer/model/PMClusterer.java```
- Optimization algorithm: ```src/main/java/selevminer/model/PMEvolutionaryOptimizer.java```
- Selection: ```src/main/java/selevminer/model/PMSelector.java```
- Distance metric: ```src/main/java/selevminer/model/PMDistanceCalculator.java```

More information can be found inside each file. There are examples of classes implementing these interfaces inside ```src/main/java/selevminer/algorithm```.

## Data in this repository
If you want to test the algorithm you can use the files inside ```src/main/resources/logs```. The logs contained in this folder are from BPIC challenges.