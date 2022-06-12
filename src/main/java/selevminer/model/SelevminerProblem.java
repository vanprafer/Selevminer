package selevminer.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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
	private Integer timeout; 

	public SelevminerProblem(File log, Miner<AnyProcessModel> miner, Integer timeout) {
		super();
		this.log = log;
		this.miner = miner;
		this.numberOfObjectives = miner.getNumberOfObjectives();
		this.numberOfVariables = miner.getNumberOfVariables();
		this.numberOfConstraints = miner.getNumberOfConstraints();
		this.name = miner.getName();
		this.timeout = timeout;

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
	
	class Task implements Callable<List<Double>> {
		
		DoubleSolution solution;
		
	    public List<Double> call() throws Exception {	    	
			AnyProcessModel processModel = miner.discover(log, solution.variables());
			List<Double> fitness = miner.metrics(processModel);
			
	        return fitness;
	    }
	}

	// Take an unevaluated chromosome, calculate its fitness (metrics via discovery algorithm) and store it internally to avoid recomputing it 
	public DoubleSolution evaluate(DoubleSolution solution) {

		List<Double> fitness = new ArrayList<Double>();
		
		// Executes discovery algorithm
        ExecutorService executor = Executors.newSingleThreadExecutor();
        // Whenever it executes
        Task task = new Task();
        task.solution = solution;
        
        // Async function
        Future<List<Double>> future = executor.submit(task);
        
        try {
        	// Definition of maximum time of waiting for discovery process
        	fitness = future.get(timeout, TimeUnit.SECONDS);
            executor.shutdownNow();

        	System.out.println("----------------------------------------------");
        	System.out.println("Calculated succesfully!");
        	System.out.println("----------------------------------------------");
            
        } catch (Exception e) {
        	// Whenever the time out passes the limit, we finish the process
            executor.shutdownNow();
            
        	System.out.println("----------------------------------------------");
            System.out.println("Aborted calculation");
        	System.out.println("----------------------------------------------");

            // Adding zeros into fitness list because we consider that the PM discovery is too slow
            for(int i=0; i<this.getNumberOfObjectives(); i++) {
            	fitness.add(17000.);
            }
        }
        
		// Store the list inside the internal array 
		for (int i=0; i<fitness.size(); i++) {
			solution.objectives()[i] = fitness.get(i);
		}

	    return solution;
	}


}
