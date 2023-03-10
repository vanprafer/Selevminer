package selevminer.model;

import java.util.List;
import java.util.Set;

/**
 * This interface is used to define an algorithm that clusters a list of AnyProcessModel into a list of
 * sets of AnyProcessModel. As long as the algorithm returns a given number of clusters (param objectiveClusters),
 * Selevminer will be able to use the algorithm with any other modules that also follow the specifications.
 * 
 * Examples: Single-Linkage Agglomerative Clustering, K-Means...
 */
public interface PMClusterer<AnyProcessModel> {

	/**
	 * Cluster a list of Process models into a list of sets.
	 * @param clusters - Initial list of process models
	 * @param objectiveClusters - Number of clusters to return (i.e. number of sets inside the list)
	 * @param distanceCalculator - Distance metric to use
	 * @return
	 */
	public List<Set<PMWrapper<AnyProcessModel>>> cluster(List<PMWrapper<AnyProcessModel>> clusters, Integer objectiveClusters, PMDistanceCalculator<AnyProcessModel> distanceCalculator);
	
}
