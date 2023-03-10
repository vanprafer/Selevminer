package selevminer.model;

import java.io.File;
import java.util.List;

/**
 * This interface represents a miner object, which is defined as an object that can discover
 * a process model (of class AnyProcessModel) from a log and some parameters represented as a list of
 * Doubles. These should be normalized, but it is not strictly necessary.
 * */
public interface PMMiner<AnyProcessModel> {

	/**
	 * Discover a process model given a log and a list of parameters
	 * 
	 * @param log - Event log file
	 * @param chromosome - List of parameters to be used by the miner
	 * @return A process model
	 */
	public AnyProcessModel discover(File log, List<Double> chromosome);
	
	/**
	 * Calculate performance metrics for a given process model.
	 * 
	 * @param processModelDiscovered - A process model discovered by this miner
	 * @return A list of metrics
	 */
	public List<Double> metrics(AnyProcessModel processModelDiscovered);
	
	/**
	 * Get lower bound for each parameter
	 * @return Lower bounds
	 */
	public List<Double> getLowerBounds();

	/**
	 * Get upper bound for each parameter
	 * @return Upper bounds
	 */
	public List<Double> getUpperBounds();
	
	/**
	 * Get the number of parameters that this miner uses to discover a model.
	 * @return Number of parameters
	 */
	public Integer getNumberOfVariables();
	
	/**
	 * Get the number of constraints that this miner uses when discovering a model.
	 * @return Number of constraints
	 */
	public Integer getNumberOfConstraints();
	
	/**
	 * Get the number of metrics that this miner can calculate for a given process model.
	 * @return Number of metrics
	 */
	public Integer getNumberOfObjectives();
	
	/**
	 * Get name of the miner
	 * @return Name of the miner
	 */
	public String getName();
	

}
