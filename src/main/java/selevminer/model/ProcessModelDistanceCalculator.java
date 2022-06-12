package selevminer.model;

/*
 * This interface represents a distance calculator object, which is defined as an object which contains
 * an algorithm to calculate the distance between two process models
 * */
public interface ProcessModelDistanceCalculator<AnyProcessModel> {

	public Double distance(AnyProcessModel a, AnyProcessModel b);
	
}
