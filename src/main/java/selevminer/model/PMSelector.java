package selevminer.model;

import java.util.Set;

/**
 * This interface represents an algorithm that extracts a single, most representative model from a cluster.
 */
public interface PMSelector<AnyProcessModel> {

	/**
	 * Return the most representative model from a cluster of Process models.
	 * @param cluster - Set of process models
	 * @return Most representative Process model
	 */
	public PMWrapper<AnyProcessModel> select(Set<PMWrapper<AnyProcessModel>> cluster);
}
