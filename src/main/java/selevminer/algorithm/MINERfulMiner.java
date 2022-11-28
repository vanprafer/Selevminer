package selevminer.algorithm;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.State;
import dk.brics.automaton.Transition;
import minerful.MinerFulMinerLauncher;
import minerful.MinerFulOutputManagementLauncher;
import minerful.concept.ProcessModel;
import minerful.io.params.OutputModelParameters;
import minerful.miner.params.MinerFulCmdParameters;
import minerful.params.InputLogCmdParameters;
import minerful.params.InputLogCmdParameters.InputEncoding;
import minerful.params.SystemCmdParameters;
import minerful.params.ViewCmdParameters;
import minerful.postprocessing.params.PostProcessingCmdParameters;
import selevminer.model.PMMiner;

/*
 * MINERfulMiner is an specific type of miner which returns a ProcessModel
 * (class from Minerful)
 */
public class MINERfulMiner implements PMMiner<ProcessModel> {
	
	class Task implements Callable<ProcessModel> {
		
		MinerFulMinerLauncher miFuMiLa;
		
	    public ProcessModel call() throws Exception {	   			
	        return miFuMiLa.mine();
	    }
	}
	
	private Integer timeout; 
	
	public MINERfulMiner(Integer timeout) {
		super();
		this.timeout = timeout;
	}

	public ProcessModel discover(File log, List<Double> configParams) {
		
		// Input parameters for MINERful
		InputLogCmdParameters inputParams =	new InputLogCmdParameters();
		MinerFulCmdParameters minerFulParams = new MinerFulCmdParameters();
		SystemCmdParameters systemParams = new SystemCmdParameters();
		PostProcessingCmdParameters postParams = new PostProcessingCmdParameters();
		
		inputParams.inputLogFile = log;
		inputParams.inputLanguage = InputEncoding.xes;
		
		// Denormalize parameters
		Double p0 = denormalizeConfigParam(configParams.get(0), 0);
		Double p1 = denormalizeConfigParam(configParams.get(1), 1);
		Double p2 = denormalizeConfigParam(configParams.get(2), 2);
		Double p3 = denormalizeConfigParam(configParams.get(3), 3);
		Double p4 = denormalizeConfigParam(configParams.get(4), 4);
		
		// Those parameters build the chromosome
		minerFulParams.branchingLimit = Integer.min(Integer.max(p0.intValue(), MinerFulCmdParameters.MINIMUM_BRANCHING_LIMIT), 3);
		postParams.confidenceThreshold = Double.min(Double.max(p1, 0), 1);
		postParams.cropRedundantAndInconsistentConstraints = p2 > 0;
		postParams.interestFactorThreshold = Double.min(Double.max(p3, 0), 1);
		postParams.supportThreshold = Double.min(Double.max(p4, 0), 1);
		
		MinerFulMinerLauncher miFuMiLa = new MinerFulMinerLauncher(inputParams, minerFulParams, postParams, systemParams);
		
        ExecutorService executor = Executors.newSingleThreadExecutor();
        // Whenever it executes
        Task task = new Task();
        task.miFuMiLa = miFuMiLa;
        
        // Async function
        Future<ProcessModel> future = executor.submit(task);
        
        try {
        	// Definition of maximum time of waiting for discovery process
        	ProcessModel result = future.get(timeout, TimeUnit.SECONDS);
            executor.shutdownNow();

        	System.out.println("----------------------------------------------");
        	System.out.println("Model succesfully calculated!");
        	System.out.println("----------------------------------------------");
        	
        	return result;
            
        } catch (Exception e) {
        	// Whenever the time out passes the limit, we finish the process
            executor.shutdownNow();
            
        	System.out.println("----------------------------------------------");
            System.out.println("Model calculation aborted!");
        	System.out.println("----------------------------------------------");
        	
        	return null;
        }
	}
	

	// Metrics definition to evaluate the model
	public List<Double> metrics(ProcessModel processModelDiscovered) {
		
		List<Double> metricList = new ArrayList<Double>();
		Automaton automaton = processModelDiscovered.buildAutomaton();
		
		Double states = (double) automaton.getNumberOfStates();
		Double transitions = (double) automaton.getNumberOfTransitions();
		
		// Provisional statistics to evaluate the model
		
		// Ratios. Here, states and transitions are confronted 
		metricList.add((states + 1) / (transitions + 1));
		//metricList.add((transitions + 1) / (states + 1));

		// Cyclomatic complexity
		metricList.add(transitions + states + 2);
		
		// Activities
		metricList.add(states);
		//metricList.add(-states); // We change the sign in order to get the maximum value instead of the minimum
		
		// Number of joins and splits
		List<Double> joinsAndSplits = getNumberOfJoinsAndSplits(automaton);
		// Minimize
		metricList.add(joinsAndSplits.get(0));
		metricList.add(joinsAndSplits.get(1));
		// Maximize
		//metricList.add(-joinsAndSplits.get(0));
		//metricList.add(-joinsAndSplits.get(1));
		
		return metricList;
	}
	
	private List<Double> getNumberOfJoinsAndSplits(Automaton automaton) {
		
		List<Double> joinsAndSplits = new ArrayList<Double>();
		joinsAndSplits.add(0.);
		joinsAndSplits.add(0.);
		
		for(State state: automaton.getStates()) {
			
			// Splits
			if(state.getTransitions().size() > 1) {
				joinsAndSplits.set(1, joinsAndSplits.get(1) + 1.);
			}
			
			Integer count = 0;
						
			for(State state2: automaton.getStates()) {
				for(Transition transition: state2.getTransitions()) {
					if(state == transition.getDest()) {
						count ++;
					}
				}
			}
			
			// Joins
			if(count > 1)  {
				joinsAndSplits.set(0, joinsAndSplits.get(0) + 1.);
			}
		}
		
		return joinsAndSplits;
	}
	
	public void saveAsCondec(ProcessModel processModelDiscovered, String path) {
		MinerFulOutputManagementLauncher outputMgt = new MinerFulOutputManagementLauncher();
		SystemCmdParameters systemParams = new SystemCmdParameters();
		ViewCmdParameters viewParams = new ViewCmdParameters();
		OutputModelParameters outParams = new OutputModelParameters();
		
		outParams.fileToSaveAsConDec = new File(path);
		
		outputMgt.manageOutput(processModelDiscovered, viewParams, outParams, systemParams);
	}

	public List<Double> getLowerBounds() {
		List<Double> lowerBounds = new ArrayList<Double>();
		lowerBounds.add((double) MinerFulCmdParameters.MINIMUM_BRANCHING_LIMIT);
		lowerBounds.add(0.0);
		lowerBounds.add(-1.0);
		lowerBounds.add(0.0);
		lowerBounds.add(0.0);
		return lowerBounds;
	}

	public List<Double> getUpperBounds() {
		List<Double> upperBounds = new ArrayList<Double>();
		upperBounds.add(3.0);
		upperBounds.add(1.0);
		upperBounds.add(1.0);
		upperBounds.add(1.0);
		upperBounds.add(1.0);
		return upperBounds;
	}
	
	private Double denormalizeConfigParam(Double value, Integer paramIndex) {
		Double lower = getLowerBounds().get(paramIndex);
		Double upper = getUpperBounds().get(paramIndex);
		
		return value * (upper - lower) + lower;
	}

	public Integer getNumberOfVariables() {
		return 5;
	}

	public Integer getNumberOfConstraints() {
		return 0;
	}

	public Integer getNumberOfObjectives() {
		return 5;
	}

	public String getName() {
		return "Minerful Miner";
	}

}
