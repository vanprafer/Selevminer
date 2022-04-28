package selevminer.model;

import java.io.File;
import java.util.List;

/*
 * This interface represents a miner object, which is defined as an object that can discover
 * a process model (of class AnyProcessModel) from a log and some parameters
 * */
public interface Miner<AnyProcessModel> {

	public AnyProcessModel discover(File log, List<Double> chromosome);
	
	public List<Double> metrics(AnyProcessModel processModelDiscovered);
	
	// Lower bounds for each variable
	public List<Double> getLowerBounds();
	// Upper bounds for each variable
	public List<Double> getUpperBounds();
	
	public Integer getNumberOfVariables();
	
	public Integer getNumberOfConstraints();
	
	public Integer getNumberOfObjectives();
	
	public String getName();
}
