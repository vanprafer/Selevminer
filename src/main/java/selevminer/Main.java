package selevminer;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

import minerful.concept.ProcessModel;
import selevminer.algorithm.MINERfulDistanceCalculator;
import selevminer.algorithm.MINERfulMiner;
import selevminer.algorithm.MINERfulPMWrapper;
import selevminer.algorithm.NSGAIIEvolutionaryOptimizer;
import selevminer.algorithm.RandomPMSelection;
import selevminer.algorithm.SingleLinkageAgglomerativeClusterer;
import selevminer.model.PMClusterer;
import selevminer.model.PMEvolutionaryOptimizer;
import selevminer.model.PMWrapper;
import selevminer.model.Selevminer;

public class Main {

	public static void main(String[] args) throws Exception {
		
		/*
		 * Configuration
		 */
		
		String logPath = "";
		String outPath = "";
		
		Integer population = 10;
		Integer generations = 5;
		Integer numSolutions = 5;
		Integer timeout = 20;
		Integer cores = 1;

		/*
		 * Creating Selevminer Instance
		 */
		
		long start = System.currentTimeMillis();
		
		// Here you can customize the algorithms chosen for each stage
		Selevminer<ProcessModel> selevminer = new Selevminer<ProcessModel>();
		selevminer.miner = new MINERfulMiner(timeout);
		selevminer.pmSelector = new RandomPMSelection<ProcessModel>();
		selevminer.clusterer = new SingleLinkageAgglomerativeClusterer<ProcessModel>();
		selevminer.distanceCalculator = new MINERfulDistanceCalculator();
		selevminer.evOptimizer = new NSGAIIEvolutionaryOptimizer<ProcessModel>(population, generations, cores);

		selevminer.logPath = logPath;
		selevminer.numSolutions = numSolutions;	
		
		/*
		 * Perform discovery
		 */
		
		List<PMWrapper<ProcessModel>> results = selevminer.selevminerDiscovery(MINERfulPMWrapper.class); 
		
		/*
		 * Write results
		 */
		
		long end = System.currentTimeMillis();
		Long time = Long.valueOf(end - start);

		// Write file with the number of milliseconds that the execution took
		FileWriter timeFile = new FileWriter(outPath + "/total time.txt");
		timeFile.write(time.toString());
		timeFile.close();
		
		System.out.println("Process models returned: " + results.size());

		// Write files with a CONDEC, a dot file and the metrics for each model returned by Selevminer
		Integer i = 1;
		String folderName = population + "p_" + generations + "g_" + timeout + "t_" + selevminer.numSolutions + "s";
		String path = outPath + "/" + folderName + "/";
		
		if (!new File(path).mkdirs()) {
			System.err.println("Unable to create path " + path);
			System.exit(0);
		}
				
		File log = new File(selevminer.logPath);
		
		for (PMWrapper<ProcessModel> pm: results) {     
			((MINERfulMiner) selevminer.miner).saveAsCondec(pm.getPm(log, selevminer.miner), path + "Model " + i + "_condec.xml");

			FileWriter writer = new FileWriter(path + "Model " + i + "_dot.txt");
			writer.write(((MINERfulPMWrapper)pm).getAutomaton(log, selevminer.miner).toDot());
			writer.close();

			FileWriter writer2 = new FileWriter(path + "Model " + i + "_metrics.txt");
			writer2.write(pm.getMetrics(log, selevminer.miner).toString());
			writer2.close();
			
			i ++;
		}
	}
}
