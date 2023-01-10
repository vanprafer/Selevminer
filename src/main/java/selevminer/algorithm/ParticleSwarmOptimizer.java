package selevminer.algorithm;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.algorithm.multiobjective.dmopso.DMOPSO;
import org.uma.jmetal.algorithm.multiobjective.dmopso.DMOPSO.FunctionType;
import org.uma.jmetal.algorithm.multiobjective.omopso.OMOPSO;
import org.uma.jmetal.algorithm.multiobjective.omopso.OMOPSOBuilder;
import org.uma.jmetal.operator.mutation.impl.NonUniformMutation;
import org.uma.jmetal.operator.mutation.impl.UniformMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

import selevminer.model.PMOptimizer;
import selevminer.model.PMMiner;
import selevminer.model.PMWrapper;

public class ParticleSwarmOptimizer<AnyProcessModel> implements PMOptimizer<AnyProcessModel>{
	
	// Number of population
	private Integer population;
	// Total number of generations
	private Integer generations;

	public ParticleSwarmOptimizer(Integer population, Integer generations) {
		super();
		this.population = population;
		this.generations = generations;
	}

	// This method uses the JMetal library in order to obtain a Pareto Front 
	public List<PMWrapper<AnyProcessModel>> optimize(File eventLog, PMMiner<AnyProcessModel> miner) {
		
		// Configuration parameters for the evolutionary algorithm
		Problem<DoubleSolution> problem;
		OMOPSO algorithm;
		
		// Optimization problem definition
		problem = new JMetalProblem<AnyProcessModel>(eventLog, miner);
		
		algorithm = new OMOPSOBuilder((DoubleProblem) problem, new SequentialSolutionListEvaluator<DoubleSolution>())
		    .setMaxIterations(generations)
		    .setSwarmSize(population)
		    .setUniformMutation(new UniformMutation(1.0 / problem.getNumberOfVariables(), 0.5))
		    .setNonUniformMutation(new NonUniformMutation(1.0 / problem.getNumberOfVariables(), 0.5, 250))
		    .build();
		
		algorithm.run();
		
		// Obtaining the Pareto Front from the last swarm
		List<DoubleSolution> chromosomes = algorithm.getResult();
		List<PMWrapper<AnyProcessModel>> paretoFront = new ArrayList<PMWrapper<AnyProcessModel>>();
		
		for(DoubleSolution chromosome: chromosomes) {
			PMWrapper<AnyProcessModel> pwwrapper = new PMWrapper<AnyProcessModel>((DefaultDoubleSolution) chromosome);
			paretoFront.add(pwwrapper);
		}
		
		return paretoFront;
	}

	
}
