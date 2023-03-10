package selevminer.algorithm;

import dk.brics.automaton.Automaton;
import minerful.concept.ProcessModel;
import selevminer.model.PMDistanceCalculator;
import selevminer.model.PMWrapper;

public class MINERfulDistanceCalculator implements PMDistanceCalculator<ProcessModel> {
	
	// The difference between 2 and 3 is the same as the difference between 200 y 300
	private Double scaledDifference(Double a, Double b) {
		return Math.abs((a - b) / Double.max(a, b));
	}

	public Double distance(PMWrapper<ProcessModel> a, PMWrapper<ProcessModel> b) {
		Automaton autA = ((MINERfulPMWrapper) a).getAutomaton(null, null);
		Automaton autB = ((MINERfulPMWrapper) b).getAutomaton(null, null); 
		
		Double constDiff = scaledDifference((double)a.getPm(null, null).getAllConstraints().size(), (double)b.getPm(null, null).getAllConstraints().size());
		Double stateDiff = scaledDifference((double)autA.getNumberOfStates(), (double)autB.getNumberOfStates());
		Double transDiff = scaledDifference((double)autA.getNumberOfTransitions(), (double)autB.getNumberOfTransitions());
		
		return stateDiff + transDiff + constDiff;
	}

}
