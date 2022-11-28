package selevminer.model;

import java.util.List;
import java.util.Set;

public interface PMClusterer<AnyProcessModel> {

	public List<Set<PMWrapper<AnyProcessModel>>> cluster(List<PMWrapper<AnyProcessModel>> clusters, Integer objectiveClusters, PMDistanceCalculator<AnyProcessModel> distanceCalculator);
	
}
