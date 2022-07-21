package selevminer.model;

import java.util.List;
import java.util.Set;

public interface PMClusterer<AnyProcessModel> {

	public List<Set<AnyProcessModel>> cluster(List<AnyProcessModel> clusters, Integer objectiveClusters, PMDistanceCalculator<AnyProcessModel> distanceCalculator);
	
}
