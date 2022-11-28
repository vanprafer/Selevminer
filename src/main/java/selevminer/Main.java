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
import selevminer.model.PMWrapper;
import selevminer.model.Selevminer;

public class Main {

	public static void main(String[] args) throws Exception {
		
		// NOTE: ProcessModel exists in MINERful library, but AnyProcessModel is just a template in order to extend Selevminer

		String logPath = args[0];
		String outPath = args[1];
		Integer populationArg = Integer.valueOf(args[2]);
		Integer generationsArg = Integer.valueOf(args[3]);
		Integer coresArg = Integer.valueOf(args[4]);
		Integer numSolsArg = Integer.valueOf(args[5]);
		
		// We define the Selevminer instance
		Integer population = populationArg;
		Integer generations = generationsArg;
		Integer timeout = 20;
		Integer cores = coresArg;
		
		long start = System.currentTimeMillis();
		
		Selevminer<ProcessModel> selevminer = new Selevminer<ProcessModel>();
		selevminer.miner = new MINERfulMiner(timeout);
		selevminer.pmSelector = new RandomPMSelection<ProcessModel>();
		selevminer.clusterer = new SingleLinkageAgglomerativeClusterer<ProcessModel>();
		selevminer.distanceCalculator = new MINERfulDistanceCalculator();
		selevminer.evOptimizer = new NSGAIIEvolutionaryOptimizer<ProcessModel>(population, generations, cores);

		selevminer.logPath = Main.class.getClassLoader().getResource(logPath).getFile();
		selevminer.numSolutions = numSolsArg;	
		
		List<PMWrapper<ProcessModel>> results = selevminer.selevminerDiscovery(MINERfulPMWrapper.class); 
		
		long end = System.currentTimeMillis();
		Long time = Long.valueOf(end - start);
		
		FileWriter timeFile = new FileWriter(outPath + "/total time.txt");
		timeFile.write(time.toString());
		timeFile.close();
		
		System.out.println("Process models returned: " + results.size());
		
		Integer i = 1;
		String folderName = population + "p_" + generations + "g_" + timeout + "t_" + selevminer.numSolutions + "s";
		String path = outPath + "/" + folderName + "/";
		
		if (!new File(path).mkdirs()) {
			System.err.println("Unable to create path " + path);
			System.exit(0);
		}
				
		File log = new File(selevminer.logPath);
		
		for (PMWrapper<ProcessModel> pm: results) {     
			((MINERfulMiner) selevminer.miner).saveAsCondec(pm.getPm(log, selevminer.miner), path + "Modelo " + i + "_condec.xml");

			FileWriter writer = new FileWriter(path + "Modelo " + i + "_dot.txt");
			writer.write(((MINERfulPMWrapper)pm).getAutomaton(log, selevminer.miner).toDot());
			writer.close();

			FileWriter writer2 = new FileWriter(path + "Modelo " + i + "_metricas.txt");
			writer2.write(pm.getMetrics(log, selevminer.miner).toString());
			writer2.close();
			
			i ++;
		}
				
		System.exit(0);
	}
}
