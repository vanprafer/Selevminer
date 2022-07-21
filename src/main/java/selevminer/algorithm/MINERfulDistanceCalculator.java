package selevminer.algorithm;

import dk.brics.automaton.Automaton;
import minerful.concept.ProcessModel;
import selevminer.model.PMDistanceCalculator;

public class MINERfulDistanceCalculator implements PMDistanceCalculator<ProcessModel> {
	
	/* +------------------------+
	 * |     SCALED DISTANCE    |
	 * +------------------------+
	 */
	
	// The difference between 2 and 3 is the same as the difference between 200 y 300
	private Double scaledDifference(Double a, Double b) {
		return Math.abs((a - b) / Double.max(a, b));
	}

	public Double distance(ProcessModel a, ProcessModel b) {

		Automaton autA = a.buildAutomaton();
		Automaton autB = b.buildAutomaton(); 
		
		Double constDiff = scaledDifference((double)a.getAllConstraints().size(), (double)b.getAllConstraints().size());
		Double stateDiff = scaledDifference((double)autA.getNumberOfStates(), (double)autB.getNumberOfStates());
		Double transDiff = scaledDifference((double)autA.getNumberOfTransitions(), (double)autB.getNumberOfTransitions());
		
		return stateDiff + transDiff + constDiff;
	}

}
