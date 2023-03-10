package selevminer.model;

/**
 * This interface represents a distance calculator object, which is defined as an object which contains
 * an algorithm to calculate the distance between two process models. This distance can be any calculation
 * that increases when the models are dissimilar and approaches 0 when they are similar. Also, it should be
 * symmetrical (i.e. the distance from a to b is the same as the distance from b to a).
 * */
public interface PMDistanceCalculator<AnyProcessModel> {

	/**
	 * Calculate distance between two process models.
	 * @param a - First process model
	 * @param b - Second process model
	 * @return Distance from a to b
	 */
	public Double distance(PMWrapper<AnyProcessModel> a, PMWrapper<AnyProcessModel> b);
	
}
