package selevminer.model;

import java.io.File;
import java.util.List;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

// Optimization problem for JMetal library (this is required)
public class SelevminerProblem<AnyProcessModel> extends AbstractDoubleProblem {
	
	private static final long serialVersionUID = 1L;
	private File log;
	private Miner<AnyProcessModel> miner;
	// Size of the chromosome (in evolutionary algorithm terms) or number of configuration parameters (for the discovery algorithm)
	private Integer numberOfVariables;
	// Size of the fitness array (evolutionary algorithm) or number of metrics to evaluate a process model (discovery algorithm)
	private Integer numberOfObjectives;
	private Integer numberOfConstraints;
	private String name;

	public SelevminerProblem(File log, Miner<AnyProcessModel> miner) {
		super();
		this.log = log;
		this.miner = miner;
		this.numberOfObjectives = miner.getNumberOfObjectives();
		this.numberOfVariables = miner.getNumberOfVariables();
		this.numberOfConstraints = miner.getNumberOfConstraints();
		this.name = miner.getName();
		this.setVariableBounds(miner.getLowerBounds(), miner.getUpperBounds());
	}

	public int getNumberOfVariables() {
		return this.numberOfVariables;
	}

	// Objective functions and fitness are synonymous
	public int getNumberOfObjectives() {
		return this.numberOfObjectives;
	}

	public int getNumberOfConstraints() {
		return this.numberOfConstraints;
	}

	public String getName() {
		return this.name;
	}

	// Take an unevaluated chromosome, calculate its fitness (metrics via discovery algorithm) and store it internally to avoid recomputing it 
	public DoubleSolution evaluate(DoubleSolution solution) {

		AnyProcessModel processModel = miner.discover(log, solution.variables());
		List<Double> fitness = miner.metrics(processModel);

		// Store the list inside the internal array 
		for (int i=0; i<fitness.size(); i++) {
			solution.objectives()[i] = fitness.get(i);
		}

	    return solution;
	}


}
