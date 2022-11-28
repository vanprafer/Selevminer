package selevminer.model;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
 * Tool that allows the user to execute the Selevminer discovery algorithm
 */
public class Selevminer<AnyProcessModel> {
	
	/* ALGORITHMS */
	
	// Evolutionary (multiobjective) optimizer
	public PMEvolutionaryOptimizer<AnyProcessModel> evOptimizer;
	// Tool that contains the architecture for the internal discovery algorithm (for example, MINERful)
	public PMMiner<AnyProcessModel> miner;	
	// Clustering algorithm
	public PMClusterer<AnyProcessModel> clusterer;
	// PM selector
	public PMSelector<AnyProcessModel> pmSelector;
	// Distance algorithm
	public PMDistanceCalculator<AnyProcessModel> distanceCalculator;
	
	// Path to the xes log we are going to apply the algorithm to 
	public String logPath;

	// Number of clusters desired to obtain. This is translated into the models 
	public Integer numSolutions = 7;
	
	/*
	 * This function combines discovery and evolutionary techniques in order to return a list of the optimal models given some metrics.
	 * This depends on the mining algorithm you have chosen and its metrics
	 */
	public List<PMWrapper<AnyProcessModel>> selevminerDiscovery(Class<? extends PMWrapper<AnyProcessModel>> wrapperClass) throws Exception {
		
		File eventLog = new File(logPath); 
		
	    long initTime = System.currentTimeMillis();	
	    
		List<PMWrapper<AnyProcessModel>> paretoFront = evOptimizer.optimize(eventLog, miner);
		
		// Remove duplicates (same metrics)
		List<PMWrapper<AnyProcessModel>> filteredParetoFront = new ArrayList<PMWrapper<AnyProcessModel>>();
		Set<List<Double>> metrics = new HashSet<List<Double>>();
		
		Constructor<? extends PMWrapper<AnyProcessModel>> constructor = wrapperClass.getConstructor(PMWrapper.class);
		
		for(PMWrapper<AnyProcessModel> pmwrapper: paretoFront) {
			List<Double> pmMetrics = pmwrapper.getMetrics(eventLog, miner);
			
			if (!metrics.contains(pmMetrics) && pmMetrics != null) {
				metrics.add(pmMetrics);
				filteredParetoFront.add(constructor.newInstance(pmwrapper));
			}
		}
		
		paretoFront = filteredParetoFront;
	    
	    long clusterTime = System.currentTimeMillis();
	    
		System.out.println("Finished optimization step in " + (clusterTime - initTime) + "ms!!");
		
		List<Set<PMWrapper<AnyProcessModel>>> clusters = clusterer.cluster(paretoFront, numSolutions, distanceCalculator);
		
	    long selectTime = System.currentTimeMillis();
	    
		System.out.println("Finished clustering step in " + (selectTime - clusterTime) + "ms!!");
		
		// We are looking for a random model from the cluster
		paretoFront.clear();
		
		// We select a PM from each cluster and store it in a list in order to return it
		for(Set<PMWrapper<AnyProcessModel>> cluster: clusters) {
			paretoFront.add(pmSelector.select(cluster));
		}
		
	    long endTime = System.currentTimeMillis();

	    System.out.println("Finished selection step in " + (endTime - selectTime) + "ms!!");
		
		return paretoFront;
	}
}
