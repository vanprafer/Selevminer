package selevminer.model;

import java.io.File;
import java.util.List;

/**
 * This interface is used to represent an algorithm that discovers a list of AnyProcessModel given
 * an event log (param eventLog) and a miner (param miner). These models should be an approximation of the
 * Pareto Front given by the metrics defined inside the PMMiner param.
 * 
 * Examples: MO Particle Swarm Optimization, NSGAII, NSGAIII...
 */
public interface PMEvolutionaryOptimizer<AnyProcessModel> {

	/**
	 * Get an approximation for the Pareto Front given by the metrics in the miner parameter.
	 * @param eventLog - File containing the event log
	 * @param miner - Miner that will be used to discover all the models in the Pareto Front
	 * @return
	 */
	public List<PMWrapper<AnyProcessModel>> optimize(File eventLog, PMMiner<AnyProcessModel> miner);
}
