package selevminer;

import java.util.List;

import minerful.concept.ProcessModel;
import selevminer.algorithm.MINERfulDistanceCalculator;
import selevminer.model.MINERfulMiner;
import selevminer.model.Selevminer;

public class Main {

	public static void main(String[] args) {
		Selevminer<ProcessModel> selevminer = new Selevminer<ProcessModel>();
		selevminer.logPath = Main.class.getClassLoader().getResource("logs/financial_log.xes").getFile();
		selevminer.miner = new MINERfulMiner();
		selevminer.distanceCalculator = new MINERfulDistanceCalculator();
		selevminer.numClusters = 5;
		
		List<ProcessModel> results = selevminer.selevminerDiscovery(); 
		
		System.out.println("Process models: " + results.size());
		
		for (ProcessModel pm: results) {
			System.out.println("\n");
			System.out.println("---------------------------------------------");
			System.out.println("Your next model is defined down below:");
			System.out.println(pm.buildAutomaton().toDot());
			System.out.println("---------------------------------------------");
			System.out.println("\n");
		}
		
		System.exit(0);
	}
}
