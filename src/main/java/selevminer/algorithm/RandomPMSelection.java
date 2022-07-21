package selevminer.algorithm;

import java.util.Random;
import java.util.Set;

import selevminer.model.PMSelector;

// This is a simple method to select a process model from a cluster
public class RandomPMSelection<AnyProcessModel> implements PMSelector<AnyProcessModel>{
	
	public AnyProcessModel select(Set<AnyProcessModel> cluster) {
		
		Integer item = new Random().nextInt(cluster.size());
		Integer i = 0;
		
		// Iterates over set in order to find the randomly selected element in 'item' variable
		for(AnyProcessModel pm: cluster){
		    if (i == item) {
		    	return pm;
		    }
		    i++;
		}
		return null;
	}
}
