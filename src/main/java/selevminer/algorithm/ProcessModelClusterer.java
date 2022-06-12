package selevminer.algorithm;

import java.util.List;
import java.util.Set;

import selevminer.model.ProcessModelDistanceCalculator;

public class ProcessModelClusterer<AnyProcessModel> {
	
	public Integer objectiveClusters;
	public List<Set<AnyProcessModel>> clusters; 
	public ProcessModelDistanceCalculator<AnyProcessModel> distanceCalculator;
	
	public List<Set<AnyProcessModel>> cluster() {
		Double minDistance = 100000000.;
		// Closest clusters
		Integer indexA = 0;
		Integer indexB = 0;
		
		for(Integer i=0; clusters.size()>i; i++) {
			for(Integer j=0; clusters.size()>j; j++) {
				if(i != j) {
					Double newDistance = distanceClustersMean(clusters.get(i), clusters.get(j));
					if(newDistance < minDistance) {
						minDistance = newDistance;
						indexA = i;
						indexB = j;
					}
				}
			}
		}
		Set<AnyProcessModel> c = clusters.get(indexA);
		c.addAll(clusters.get(indexB));
		
		if(indexA > indexB) {
			clusters.remove((int) indexA);
			clusters.remove((int) indexB);
		} else {
			clusters.remove((int) indexB);
			clusters.remove((int) indexA);
		}
		
		clusters.add(c);
		
		if(objectiveClusters < clusters.size()) {
			cluster();
		}

		return clusters;
	}
	
	// This function calculates the mean between each model of two clusters
	public Double distanceClustersMean(Set<AnyProcessModel> a, Set<AnyProcessModel> b) {
		
		Double distance = 0.;
		for(AnyProcessModel modelA: a) {
			for(AnyProcessModel modelB: b) {
				distance += distanceCalculator.distance(modelA, modelB);
			}
		}

		return distance/(a.size()*b.size());
	}
	
}
