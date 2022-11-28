package selevminer.model;

import java.io.File;
import java.util.List;

public interface PMEvolutionaryOptimizer<AnyProcessModel> {

	public List<PMWrapper<AnyProcessModel>> optimize(File eventLog, PMMiner<AnyProcessModel> miner);
}
