package selevminer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import minerful.concept.ProcessModel;
import minerful.io.ProcessModelLoader;
import minerful.io.params.InputModelParameters.InputEncoding;
import selevminer.algorithm.MINERfulDistanceCalculator;
import selevminer.algorithm.MINERfulMiner;
import selevminer.algorithm.MINERfulPMWrapper;
import selevminer.algorithm.ManhattanMetricDistanceCalculator;
import selevminer.algorithm.RandomPMSelection;
import selevminer.algorithm.SingleLinkageAgglomerativeClusterer;
import selevminer.model.PMClusterer;
import selevminer.model.PMDistanceCalculator;
import selevminer.model.PMSelector;
import selevminer.model.PMWrapper;

public class ClusteringTests {
	
	static List<PMWrapper<ProcessModel>> readAllModelsFromFolder(String path) throws IOException {
		ProcessModelLoader io = new ProcessModelLoader();
		
		SearchFileByWildcard sfbw = new SearchFileByWildcard();
		List<Path> actual = sfbw.searchWithWc(Paths.get(path), "glob:**/*metricas*.txt");
		
		List<PMWrapper<ProcessModel>> models = new ArrayList<PMWrapper<ProcessModel>>();
		
		for (Path p: actual) {
			System.out.println(p);
			Scanner reader = new Scanner(new File(p.toString()));
			String metrics = reader.nextLine();
			
			List<Double> values = new ArrayList<Double>();
			String valuesStr = metrics.substring(1, metrics.length() - 1);
			String[] numbers = valuesStr.split(", ");
			
			for (String s: numbers) {
				values.add(Double.valueOf(s));
			}
			
			ProcessModel nullModel = null;
			PMWrapper<ProcessModel> wrapper = new MINERfulPMWrapper(new PMWrapper<ProcessModel>(nullModel));
			wrapper.metrics = values;
			
			models.add(wrapper);
		}
		
		System.out.println(models.size());
		
		return models;
	}
	
	static List<PMWrapper<ProcessModel>> readAllModelsFromMetFile(File log, String path) throws IOException {
		List<PMWrapper<ProcessModel>> models = new ArrayList<PMWrapper<ProcessModel>>();
		MINERfulMiner miner = new MINERfulMiner(20);
		
		Scanner reader = new Scanner(new File(path));

		while(reader.hasNextLine()) {
			String metrics = reader.nextLine();
			List<Double> values = null;
			
			if (metrics.contains("|")) {
				String[] parts = metrics.split("\\|");
				metrics = parts[0];
				values = new ArrayList<Double>();
				
				String valuesStr = parts[1].substring(1, parts[1].length() - 1);
				String[] numbers = valuesStr.split(", ");
				
				for (String s: numbers) {
					values.add(Double.valueOf(s));
				}
			}
			
			metrics = metrics.substring(1, metrics.length() - 1);
			String[] numbers = metrics.split(", ");
			List<Double> params = new ArrayList<Double>();
			
			for (String s: numbers) {
				params.add(Double.valueOf(s));
			}
						
			ProcessModel model = null;// miner.discover(log, params);
			
			MINERfulPMWrapper wrapper = new MINERfulPMWrapper(new PMWrapper<ProcessModel>(model));
			
			if (values != null && !values.isEmpty()) {
				wrapper.metrics = values;
			}
			
			models.add(wrapper);
		}
		
		return models;
	}
	
	static void saveAllModels(List<PMWrapper<ProcessModel>> models, String path) throws IOException {		
		if (!new File(path).mkdirs()) {
			System.err.println("Unable to create path " + path);
			System.exit(0);
		}
		
		Integer it = 0;
		
		for (PMWrapper<ProcessModel> model: models) {
			FileWriter writer = new FileWriter(new File(path + "\\metricas_" + it + ".txt"));
			writer.write(model.getMetrics(null, new MINERfulMiner(0)).toString());
			writer.close();
			
			it ++;
		}
	}
	
	public static void main(String[] args) throws IOException {
		// File log = new File("C:\\Users\\Vanessa\\Desktop\\Paper\\Selevminer\\BPI logs\\2013\\files_bpi_challenge_2013\\BPI Challenge 2013, incidents.xes");
		
		PMClusterer<ProcessModel> clusterer = new SingleLinkageAgglomerativeClusterer<ProcessModel>();
		PMSelector<ProcessModel> selector = new RandomPMSelection<ProcessModel>();
		//PMDistanceCalculator<ProcessModel> distance = new MINERfulDistanceCalculator();
		PMDistanceCalculator<ProcessModel> distance = new ManhattanMetricDistanceCalculator<ProcessModel>();
		
		System.out.println("Reading models...");

		// List<PMWrapper<ProcessModel>> models = readAllModelsFromMetFile(null, "C:\\Users\\Vanessa\\Desktop\\Paper\\Clustering\\Closed\\met_optimal_closed.txt");
		// List<PMWrapper<ProcessModel>> models = readAllModelsFromFolder("C:\\Users\\Vanessa\\Desktop\\Paper\\Fuerza bruta\\server data\\incidents\\200p_10g_20t_12s");
		List<PMWrapper<ProcessModel>> models = readAllModelsFromFolder("C:\\Users\\Vanessa\\Desktop\\BPIC 2013 closed problems\\200p_10g_20t_10s");

		Integer it = 0;
		
		for (PMWrapper<ProcessModel> model: models) {
			System.out.println("Calculating model metrics " + it + "...");
			model.getMetrics(null, new MINERfulMiner(0));
			it ++;
		}
		
		System.out.println("Clustering...");
		
		List<Set<PMWrapper<ProcessModel>>> clusters = clusterer.cluster(models, 4, distance);
				
		for (int i = 0; i < 50; i ++) {
			System.out.println("Selecting...");
			
			List<PMWrapper<ProcessModel>> paretoFront = new ArrayList<PMWrapper<ProcessModel>>();
			
			for(Set<PMWrapper<ProcessModel>> cluster: clusters) {
				paretoFront.add(selector.select(cluster));
			}
			
			System.out.println("Finished!");
			
			saveAllModels(paretoFront, "C:\\Users\\Vanessa\\Desktop\\Paper\\Clustering\\Closed\\200p\\test_" + i);	
		}
	}
}
