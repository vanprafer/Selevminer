package selevminer.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.parallel.asynchronous.algorithm.impl.AsynchronousMultiThreadedNSGAII;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.termination.impl.TerminationByEvaluations;

import selevminer.algorithm.ProcessModelClusterer;

/*
 * Tool that allows the user to execute the Selevminer discovery algorithm
 */
public class Selevminer<AnyProcessModel> {
	// Path to the xes log we are going to apply the algorithm to 
	public String logPath;
	// Tool that contains the architecture for the internal discovery algorithm (for example, MINERful)
	public Miner<AnyProcessModel> miner;	
	// Number of clusters desired to obtain. This is translated into the models 
	public Integer numClusters = 7;
	// Distance algorithm
	public ProcessModelDistanceCalculator<AnyProcessModel> distanceCalculator;
	// Timeout for discovery algorithm
	public Integer timeout = 10;
	
	/*
	 * This function combines discovery and evolutionary techniques in order to return a list of the optimal models given some metrics.
	 * This depends on the mining algorithm you have chosen and its metrics
	 */
	public List<AnyProcessModel> selevminerDiscovery() {
		
		List<AnyProcessModel> modelSolutions = new ArrayList<AnyProcessModel>();
		
		/* +------------------------+
		 * | EVOLUTIONARY ALGORITHM |
		 * +------------------------+
		 */
		
		// Configuration parameters for the evolutionary algorithm
		Problem<DoubleSolution> problem;
		AsynchronousMultiThreadedNSGAII<DoubleSolution> algorithm;
		CrossoverOperator<DoubleSolution> crossover;
		MutationOperator<DoubleSolution> mutation;
		
		// Optimization problem definition
		File log = new File(logPath); 
		problem = new SelevminerProblem<AnyProcessModel>(log, miner, timeout);
		
		// Crossover function
		double crossoverProbability = 0.9;
		double crossoverDistributionIndex = 30.0;
		crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);
		
		// Mutation function
		double mutationProbability = 1.0 / problem.getNumberOfVariables();
		double mutationDistributionIndex = 20.0;
		mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);
		
		algorithm = new AsynchronousMultiThreadedNSGAII<DoubleSolution>(
			1, 
			problem, 
			10, 
			crossover, 
			mutation, 
			new TerminationByEvaluations(100)
		);
			
		algorithm.run();
		
		System.out.println("He finalizado el algoritmo genetico");
		
		// Obtaining the Pareto front (last generation) and applying discovery to each chromosome in order to get a process model for each one
		List<DoubleSolution> paretoFront = algorithm.getResult();
		
		List<Set<AnyProcessModel>> clusters = new ArrayList<Set<AnyProcessModel>>();
		
		for (DoubleSolution chromosome: paretoFront) {
			System.out.println("Calculating...");
			AnyProcessModel processModel = miner.discover(log, chromosome.variables());
			modelSolutions.add(processModel);
			
			Set<AnyProcessModel> modelSet = new HashSet<AnyProcessModel>();
			modelSet.add(processModel);
			clusters.add(modelSet);
		}
		
		ProcessModelClusterer<AnyProcessModel> clusterer = new ProcessModelClusterer<AnyProcessModel>();
		clusterer.objectiveClusters = numClusters;
		clusterer.clusters = clusters;
		clusterer.distanceCalculator = distanceCalculator;
		clusterer.cluster();
		
		System.out.println("He finalizado el clustering");
		
		// We are looking for a random model from the cluster
		modelSolutions.clear();
		
		for(Set<AnyProcessModel> cluster: clusterer.clusters) {
			Integer item = new Random().nextInt(cluster.size());
			Integer i = 0;
			for(AnyProcessModel pm : cluster){
			    if (i == item) {
			    	modelSolutions.add(pm);
			    	break;
			    }
			    i++;
			}
		}
		
		return modelSolutions;
	}
}
