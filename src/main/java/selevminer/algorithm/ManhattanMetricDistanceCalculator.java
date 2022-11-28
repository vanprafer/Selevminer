package selevminer.algorithm;

import java.util.List;

import selevminer.model.PMDistanceCalculator;
import selevminer.model.PMWrapper;

public class ManhattanMetricDistanceCalculator<AnyProcessModel> implements PMDistanceCalculator<AnyProcessModel> {
	
	public Double distance(PMWrapper<AnyProcessModel> a, PMWrapper<AnyProcessModel> b) {
		List<Double> metricsA = a.getMetrics(null, null);
		List<Double> metricsB = b.getMetrics(null, null);
		
		Double manhattan = 0.0;
		
		for (int i = 0; i < metricsA.size(); i ++) {
			manhattan += Math.abs(metricsA.get(i) - metricsB.get(i));
		}
		
		return manhattan;
	}

}
