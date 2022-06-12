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
 * MINERfulMiner is an specific type of miner which returns a ProcessModel
 * (class from Minerful)
 */
public class MINERfulMiner implements Miner<ProcessModel> {
	
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
		return 2;
	}

	public String getName() {
		return "Minerful Miner";
	}

}
