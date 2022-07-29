package selevminer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import minerful.concept.ProcessModel;
import selevminer.algorithm.MINERfulDistanceCalculator;
import selevminer.algorithm.MINERfulMiner;
import selevminer.algorithm.RandomPMSelection;
import selevminer.algorithm.SingleLinkageAgglomerativeClusterer;
import selevminer.model.NSGAIIEvolutionaryOptimizer;
import selevminer.model.Selevminer;

public class Main {

	public static void main(String[] args) throws IOException {
		
		// NOTE: ProcessModel exists in MINERful library, but AnyProcessModel is just a template in order to extend Selevminer
		
		// We define the Selevminer instance
		Integer population = 15;
		Integer generations = 10;
		Integer timeout = 20;
		Integer cores = 4;
		
		Selevminer<ProcessModel> selevminer = new Selevminer<ProcessModel>();
		selevminer.miner = new MINERfulMiner(timeout);
		selevminer.pmSelector = new RandomPMSelection<ProcessModel>();
		selevminer.clusterer = new SingleLinkageAgglomerativeClusterer<ProcessModel>();
		selevminer.distanceCalculator = new MINERfulDistanceCalculator();
		selevminer.evOptimizer = new NSGAIIEvolutionaryOptimizer<ProcessModel>(population, generations, cores);

		selevminer.logPath = Main.class.getClassLoader().getResource("logs/financial_log.xes").getFile();
		selevminer.numSolutions = 10;	
		
		List<ProcessModel> results = selevminer.selevminerDiscovery(); 
		
		System.out.println("Process models returned: " + results.size());
		
		Integer i = 1;
		String folderName = population + "p_" + generations + "g_" + timeout + "t_" + selevminer.numSolutions + "s";
		String path = "C:/Users/Vanessa/Desktop/modelos/Experimentacion Selevminer/" + folderName + "/";
		
		if (!new File(path).mkdirs()) {
			System.err.println("Unable to create path " + path);
			System.exit(0);
		}
				
		for (ProcessModel pm: results) {     
			((MINERfulMiner) selevminer.miner).saveAsCondec(pm, path + "Modelo " + i + "_condec.xml");

			FileWriter writer = new FileWriter(path + "Modelo " + i + "_dot.txt");
			writer.write(pm.buildAutomaton().toDot());
			writer.close();
			
			i ++;
		}
		
		System.exit(0);
	}
}
