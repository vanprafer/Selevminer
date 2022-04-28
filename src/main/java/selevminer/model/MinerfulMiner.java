package selevminer.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import dk.brics.automaton.Automaton;
import minerful.MinerFulMinerLauncher;
import minerful.concept.ProcessModel;
import minerful.miner.params.MinerFulCmdParameters;
import minerful.params.InputLogCmdParameters;
import minerful.params.InputLogCmdParameters.InputEncoding;
import minerful.params.SystemCmdParameters;
import minerful.postprocessing.params.PostProcessingCmdParameters;

/*
 * MinerfulMiner is an specific type of miner which returns a ProcessModel
 * (class from Minerful)
 */
public class MinerfulMiner implements Miner<ProcessModel> {
	
	public ProcessModel discover(File log, List<Double> configParams) {
		
		// Input parameters for MINERful
		InputLogCmdParameters inputParams =	new InputLogCmdParameters();
		MinerFulCmdParameters minerFulParams = new MinerFulCmdParameters();
		SystemCmdParameters systemParams = new SystemCmdParameters();
		PostProcessingCmdParameters postParams = new PostProcessingCmdParameters();
		
		inputParams.inputLogFile = log;
		inputParams.inputLanguage = InputEncoding.xes;
		
		// Those parameters build the chromosome
		minerFulParams.branchingLimit = Integer.min(Integer.max(configParams.get(0).intValue(), MinerFulCmdParameters.MINIMUM_BRANCHING_LIMIT), 3);
		postParams.confidenceThreshold = Double.min(Double.max(configParams.get(1), 0), 1);
		postParams.cropRedundantAndInconsistentConstraints = configParams.get(2) > 0;
		postParams.interestFactorThreshold = Double.min(Double.max(configParams.get(3), 0), 1);
		postParams.supportThreshold = Double.min(Double.max(configParams.get(4), 0), 1);
		
		MinerFulMinerLauncher miFuMiLa = new MinerFulMinerLauncher(inputParams, minerFulParams, postParams, systemParams);
		
		ProcessModel processModel = miFuMiLa.mine();
		
		return processModel;
	}

	// Metrics definition to evaluate the model
	public List<Double> metrics(ProcessModel processModelDiscovered) {
		
		List<Double> metricList = new ArrayList<Double>();
		Automaton automaton = processModelDiscovered.buildAutomaton();
		
		Double states = (double) automaton.getNumberOfStates();
		Double transitions = (double) automaton.getNumberOfTransitions();
		
		// Provisional statistics to evaluate the model. Here, states and transitions are confronted 
		metricList.add((double) (states + 1) / (transitions + 1));
		metricList.add((double) (transitions + 1) / (states + 1));
		
		return metricList;
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

	public Integer getNumberOfVariables() {
		return 5;
	}

	public Integer getNumberOfConstraints() {
		return 0;
	}

	public Integer getNumberOfObjectives() {
		return 2;
	}

	public String getName() {
		return "Minerful Miner";
	}

}
