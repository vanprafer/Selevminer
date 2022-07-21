package selevminer.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

// Optimization problem for JMetal library (this is required)
public class JMetalProblem<AnyProcessModel> extends AbstractDoubleProblem {
	
	private static final long serialVersionUID = 1L;
	private File log;
	private PMMiner<AnyProcessModel> miner;
	// Size of the chromosome (in evolutionary algorithm terms) or number of configuration parameters (for the discovery algorithm)
	private Integer numberOfVariables;
	// Size of the fitness array (evolutionary algorithm) or number of metrics to evaluate a process model (discovery algorithm)
	private Integer numberOfObjectives;
	private Integer numberOfConstraints;
	private String name;

	public JMetalProblem(File log, PMMiner<AnyProcessModel> miner) {
		super();
		this.log = log;
		this.miner = miner;
		this.numberOfObjectives = miner.getNumberOfObjectives();
		this.numberOfVariables = miner.getNumberOfVariables();
		this.numberOfConstraints = miner.getNumberOfConstraints();
		this.name = miner.getName();

		List<Double> lowerBounds = new ArrayList<Double>();
		List<Double> upperBounds = new ArrayList<Double>();
		
		for(int i = 0; i < this.numberOfVariables; i ++) {
			lowerBounds.add(0.);
			upperBounds.add(1.);
		}
		
		this.setVariableBounds(lowerBounds, upperBounds);
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
		
		// Discover gets the log and the chromosome
		AnyProcessModel pm = miner.discover(log, solution.variables());
		List<Double> fitness = new ArrayList<Double>();
		
		if(pm == null) {
            // Adding big numbers into fitness list because we consider that the PM discovery is too slow
            for(int i=0; i<this.getNumberOfObjectives(); i++) {
            	// We provide a bad fitness for metrics in order to represent that a model that cannot be discovered is bad
            	// A bad fitness makes sure that the chromosome will not reproduce
            	fitness.add(17000.);
            }
        } else {
			fitness = miner.metrics(pm);
		}
		
		System.out.println("The current chromosome is: " + solution.variables());
		        
		// Store the list inside the internal array 
		for (int i=0; i<fitness.size(); i++) {
			solution.objectives()[i] = fitness.get(i);
		}

	    return solution;
	}


}
