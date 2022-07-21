package selevminer.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.parallel.asynchronous.algorithm.impl.AsynchronousMultiThreadedNSGAII;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.termination.impl.TerminationByEvaluations;

public class NSGAIIEvolutionaryOptimizer<AnyProcessModel> implements PMEvolutionaryOptimizer<AnyProcessModel>{
	
	// Number of population
	private Integer population;
	// Total number of generations
	private Integer generations;
	// Number of cores to use
	private Integer cores;

	public NSGAIIEvolutionaryOptimizer(Integer population, Integer generations, Integer cores) {
		super();
		this.population = population;
		this.generations = generations;
		this.cores = cores;
	}

	// This method uses the JMetal library in order to obtain a Pareto Front 
	public List<AnyProcessModel> optimize(File eventLog, PMMiner<AnyProcessModel> miner) {
		
		// Configuration parameters for the evolutionary algorithm
		Problem<DoubleSolution> problem;
		AsynchronousMultiThreadedNSGAII<DoubleSolution> algorithm;
		CrossoverOperator<DoubleSolution> crossover;
		MutationOperator<DoubleSolution> mutation;
		
		// Optimization problem definition
		problem = new JMetalProblem<AnyProcessModel>(eventLog, miner);
		
		// Crossover function
		double crossoverProbability = 0.9;
		double crossoverDistributionIndex = 30.0;
		crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);
		
		// Mutation function
		double mutationProbability = 1.0 / problem.getNumberOfVariables();
		double mutationDistributionIndex = 20.0;
		mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);
		
		algorithm = new AsynchronousMultiThreadedNSGAII<DoubleSolution>(
			cores, 
			problem, 
			population, 
			crossover, 
			mutation, 
			// The next method calculates the number of evaluations for the given number of generations
			new TerminationByEvaluations(generations*population)
		);
			
		algorithm.run();
		
		// Obtaining the Pareto Front (last generation) and applying discovery to each chromosome in order to get a process model for each one
		List<DoubleSolution> chromosomes = algorithm.getResult();
		
		List<AnyProcessModel> paretoFront = new ArrayList<AnyProcessModel>();
		
		for (DoubleSolution chromosome: chromosomes) {
			// We discover a PM for each chromosome
			AnyProcessModel processModel = miner.discover(eventLog, chromosome.variables());
			
			// Whenever a model excedes the timeout, we discard it
			if(processModel != null) {
				paretoFront.add(processModel);
			}
		}
		
		return paretoFront;
	}

	
}
