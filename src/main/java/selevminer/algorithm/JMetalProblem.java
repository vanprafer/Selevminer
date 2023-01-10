package selevminer.algorithm;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;

import selevminer.model.PMMiner;

import org.apache.commons.lang3.tuple.Pair;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;

// Optimization problem for JMetal library (this is required)
public class JMetalProblem<AnyProcessModel> implements DoubleProblem {

	private static final long serialVersionUID = 1L;
	private File log;
	private PMMiner<AnyProcessModel> miner;
	// Size of the chromosome (in evolutionary algorithm terms) or number of
	// configuration parameters (for the discovery algorithm)
	private Integer numberOfVariables;
	// Size of the fitness array (evolutionary algorithm) or number of metrics to
	// evaluate a process model (discovery algorithm)
	private Integer numberOfObjectives;
	private Integer numberOfConstraints;
	private String name;

	protected List<Pair<Double, Double>> bounds;

	public JMetalProblem(File log, PMMiner<AnyProcessModel> miner) {
		super();
		this.log = log;
		this.miner = miner;
		this.numberOfObjectives = miner.getNumberOfObjectives();
		this.numberOfVariables = miner.getNumberOfVariables();
		this.numberOfConstraints = miner.getNumberOfConstraints();
		this.name = miner.getName();
		this.bounds = new ArrayList<Pair<Double, Double>>();

		for (int i = 0; i < this.numberOfVariables; i++) {
			bounds.add(Pair.of(0.0, 1.0));
		}
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

	// Take an unevaluated chromosome, calculate its fitness (metrics via discovery
	// algorithm) and store it internally to avoid recomputing it
	public DoubleSolution evaluate(DoubleSolution solution) {

		// Discover gets the log and the chromosome
		AnyProcessModel pm = miner.discover(log, solution.variables());
		List<Double> fitness = new ArrayList<Double>();

		if (pm == null) {
			// Adding big numbers into fitness list because we consider that the PM
			// discovery is too slow
			for (int i = 0; i < this.getNumberOfObjectives(); i++) {
				// We provide a bad fitness for metrics in order to represent that a model that
				// cannot be discovered is bad
				// A bad fitness makes sure that the chromosome will not reproduce
				fitness.add(17000.);
			}
		} else {
			fitness = miner.metrics(pm);
		}

		System.out.println("The current chromosome is: " + solution.variables());

		// Store the list inside the internal array
		for (int i = 0; i < fitness.size(); i++) {
			solution.objectives()[i] = fitness.get(i);
		}

		return solution;
	}

	public Double getLowerBound(int index) {
		return this.bounds.get(index).getLeft();
	}

	public Double getUpperBound(int index) {
		return this.bounds.get(index).getRight();
	}

	public List<Pair<Double, Double>> getBounds() {
		return new ArrayList<Pair<Double, Double>>(this.bounds);
	}

	// Creates a random DoubleSolution element (the first set of elements uses this several times)
	public DoubleSolution createSolution() {
		List<Bounds<Double>> b = new ArrayList<Bounds<Double>>();
		
		for (final Pair<Double, Double> p: this.bounds) {
			b.add(new Bounds<Double>() {
		        /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public Double getLowerBound() {
		          return p.getLeft();
		        }

		        public Double getUpperBound() {
		          return p.getRight();
		        }
		    });
		}
		
	    return new DefaultDoubleSolution(getNumberOfObjectives(), getNumberOfConstraints(), b);
	}

}
