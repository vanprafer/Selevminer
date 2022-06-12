package selevminer.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.State;
import dk.brics.automaton.Transition;
import minerful.concept.ProcessModel;
import selevminer.graph.Graph;
import selevminer.graph.NMSimilarity;
import selevminer.model.ProcessModelDistanceCalculator;

public class MINERfulDistanceCalculator implements ProcessModelDistanceCalculator<ProcessModel> {
	
	private Graph pmToGraph(ProcessModel a) {
		Automaton automatonA = a.buildAutomaton();
		SortedSet<State> statesASorted = new TreeSet<State>();
		statesASorted.addAll(automatonA.getStates());
		
		List<State> statesASortedList = new ArrayList<State>();
		statesASortedList.addAll(statesASorted);
		
		Integer[][] statesMatrix = new Integer[statesASortedList.size()][statesASortedList.size()];
		
		// Matriz de ceros
		for(Integer i=0; i<statesASortedList.size(); i++) {
			for(Integer j=0; j<statesASortedList.size(); j++) {
				statesMatrix[i][j] = 0;
			}
		}
		
		// Relleno mi matriz de ceros con las transiciones de los nodos,
		// donde los nodos representan a la posición de la matriz
		for(State s: statesASortedList) {
			for(Transition t: s.getTransitions()) {
				Integer destination = statesASortedList.indexOf(t.getDest());
				Integer start = statesASortedList.indexOf(s);
				statesMatrix[start][destination] ++;
			}	
		}
		
		Graph graph = new Graph(statesMatrix);
		
		return graph;
	}
	
	private Double scaledDifference(Double a, Double b) {
		return Math.abs((a - b) / Double.max(a, b));
	}

	public Double distance(ProcessModel a, ProcessModel b) {
		/*Graph graphA = pmToGraph(a);
		Graph graphB = pmToGraph(b);
        NMSimilarity similarityMeasure = new NMSimilarity(graphA, graphB, 0.0001);
        
		return similarityMeasure.getGraphSimilarity();*/

		Automaton autA = a.buildAutomaton();
		Automaton autB = b.buildAutomaton(); 
		
		Double constDiff = scaledDifference((double)a.getAllConstraints().size(), (double)b.getAllConstraints().size());
		Double stateDiff = scaledDifference((double)autA.getNumberOfStates(), (double)autB.getNumberOfStates());
		Double transDiff = scaledDifference((double)autA.getNumberOfTransitions(), (double)autB.getNumberOfTransitions());
		
		return stateDiff + transDiff + constDiff;
	}

}
