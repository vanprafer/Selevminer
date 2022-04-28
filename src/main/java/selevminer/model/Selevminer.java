package selevminer.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaiii.NSGAIIIBuilder;
import org.uma.jmetal.example.AlgorithmRunner;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

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
		Algorithm<List<DoubleSolution>> algorithm;
		CrossoverOperator<DoubleSolution> crossover;
		MutationOperator<DoubleSolution> mutation;
		SelectionOperator<List<DoubleSolution>, DoubleSolution> selection;
		
		// Optimization problem definition
		File log = new File(logPath); 
		problem = new SelevminerProblem<AnyProcessModel>(log, miner);
		
		// Crossover function
		double crossoverProbability = 0.9;
		double crossoverDistributionIndex = 30.0;
		crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);
		
		// Mutation function
		double mutationProbability = 1.0 / problem.getNumberOfVariables();
		double mutationDistributionIndex = 20.0;
		mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);
		
		// Selection criterion
		selection = new BinaryTournamentSelection<DoubleSolution>();
		
		// Creating the algorithm object
		algorithm = new NSGAIIIBuilder<DoubleSolution>(problem)
				        .setCrossoverOperator(crossover)
				        .setMutationOperator(mutation)
				        .setSelectionOperator(selection)
				        .setPopulationSize(5)
				        .setMaxIterations(3)
				        .setNumberOfDivisions(5)
				        .build();
		
		// Running the algorithm
		AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();
		
		// Obtaining the Pareto front (last generation) and applying discovery to each chromosome in order to get a process model for each one
		List<DoubleSolution> paretoFront = algorithm.getResult();
		
		for (DoubleSolution chromosome: paretoFront) {
			AnyProcessModel processModel = miner.discover(log, chromosome.variables());
			modelSolutions.add(processModel);
		}
		
		return modelSolutions;
	}
}
