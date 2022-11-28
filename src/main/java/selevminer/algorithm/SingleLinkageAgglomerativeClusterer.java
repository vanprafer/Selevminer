package selevminer.algorithm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import selevminer.model.PMClusterer;
import selevminer.model.PMDistanceCalculator;
import selevminer.model.PMWrapper;

public class SingleLinkageAgglomerativeClusterer<AnyProcessModel> implements PMClusterer<AnyProcessModel>{
		
	// This method gets the demanded number clusters
	public List<Set<PMWrapper<AnyProcessModel>>> cluster(List<PMWrapper<AnyProcessModel>> processModels, Integer objectiveClusters,
			PMDistanceCalculator<AnyProcessModel> distanceCalculator) {
		
		// We initialize a cluster for each process model
		List<Set<PMWrapper<AnyProcessModel>>> clusters = firstClusters(processModels);
		
		// We iterate until we get the demanded number clusters. Each iteration merges two clusters
		while(objectiveClusters < clusters.size()) {
			
			System.out.println("Agglomerating " + clusters.size() + " clusters...");
			
			Double minDistance = 100000000.;
			// Closest clusters
			Integer indexA = 0;
			Integer indexB = 0;
			
			// We get the clusters for the next iteration
			for(Integer i=0; i<clusters.size(); i++) {
				for(Integer j=0; j<clusters.size(); j++) {
					// We only analyze different clusters
					if(i != j) {
						Double newDistance = distanceClustersMean(clusters.get(i), clusters.get(j), distanceCalculator);
						// We search the minimum distances between clusters for each iteration of the clustering algorithm
						if(newDistance < minDistance) {
							minDistance = newDistance;
							// We get the index of the clusters that give the minimum distance 
							indexA = i;
							indexB = j;
						}
					}
				}
			}
			
			// We create a new cluster with the models of the two closest clusters
			Set<PMWrapper<AnyProcessModel>> c = clusters.get(indexA);
			c.addAll(clusters.get(indexB));
			
			// We need to remove the highest element first
			if(indexA > indexB) {
				clusters.remove((int) indexA);
				clusters.remove((int) indexB);
			} else {
				clusters.remove((int) indexB);
				clusters.remove((int) indexA);
			}
			
			// We add the new cluster
			clusters.add(c);
		}

		return clusters;
	}
	
	// This function calculates the mean between each model of two clusters
	public Double distanceClustersMean(Set<PMWrapper<AnyProcessModel>> clusterA, Set<PMWrapper<AnyProcessModel>> clusterB, 
			PMDistanceCalculator<AnyProcessModel> distanceCalculator) {
		
		// This part calculates the sum of all distances between the models of the two clusters
		Double distance = 0.;
		for(PMWrapper<AnyProcessModel> modelA: clusterA) {
			for(PMWrapper<AnyProcessModel> modelB: clusterB) {
				distance += distanceCalculator.distance(modelA, modelB);
			}
		}
		
		// This returns the mean between the items for each cluster
		// Denominator is the total number of distances between each element of each cluster
		return distance/(clusterA.size()*clusterB.size());
	}	
	
	// This method initializes the first clusters. One for each element
	private List<Set<PMWrapper<AnyProcessModel>>> firstClusters(List<PMWrapper<AnyProcessModel>> processModels) {
		
		List<Set<PMWrapper<AnyProcessModel>>> firstClusters = new ArrayList<Set<PMWrapper<AnyProcessModel>>>();
		
		for(PMWrapper<AnyProcessModel> processModel: processModels) {
			Set<PMWrapper<AnyProcessModel>> modelSet = new HashSet<PMWrapper<AnyProcessModel>>();
			modelSet.add(processModel);
			firstClusters.add(modelSet);
		}
		
		return firstClusters;
	}
}
