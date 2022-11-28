package selevminer.model;

/*
 * This interface represents a distance calculator object, which is defined as an object which contains
 * an algorithm to calculate the distance between two process models
 * */
public interface PMDistanceCalculator<AnyProcessModel> {

	public Double distance(PMWrapper<AnyProcessModel> a, PMWrapper<AnyProcessModel> b);
	
}
