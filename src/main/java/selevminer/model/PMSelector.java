package selevminer.model;

import java.util.Set;

public interface PMSelector<AnyProcessModel> {

	// This method select a process model from cluster
	public AnyProcessModel select(Set<AnyProcessModel> cluster);
}
