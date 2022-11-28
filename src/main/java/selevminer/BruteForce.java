package selevminer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import minerful.concept.ProcessModel;
import selevminer.algorithm.MINERfulMiner;

public class BruteForce {

	public static void main(String[] args) throws IOException {
				
		Integer timeout = 20;
		String logPath = args[0]; //Main.class.getClassLoader().getResource("logs/financial_log.xes").getFile();
		File log = new File(logPath);
		
		MINERfulMiner miner = new MINERfulMiner(timeout);
		
		Integer p0i = 0;
		Integer p1i = 0;
		Integer p2i = 0;
		Integer p3i = 0;
		Integer p4i = 0;
		
		long time = 0;
				
		File savedData = new File(args[1]); // new File("C:\\Users\\Vanessa\\Desktop\\SavedDataBruteForce\\currentIteration.txt");
		File metrics = new File(args[2]); // new File("C:\\Users\\Vanessa\\Desktop\\SavedDataBruteForce\\metrics.txt");

		FileWriter metricsWriter = new FileWriter(metrics, true);
		PrintWriter printWriter = new PrintWriter(metricsWriter);
		
		if(savedData.exists()) {
			Scanner reader = new Scanner(savedData);
			String data = reader.nextLine();
			String[] pieces = data.split(",");
			
			p0i = Integer.valueOf(pieces[0]);
			p1i = Integer.valueOf(pieces[1]);
			p2i = Integer.valueOf(pieces[2]);
			p3i = Integer.valueOf(pieces[3]);
			p4i = Integer.valueOf(pieces[4]);

			time = Integer.valueOf(pieces[5]);
			
			reader.close();
		}
		
		Integer nDiv = 20;
		Integer nDivP0 = 2;
		Integer nDivP2 = 1;
		
		for(Integer p0 = p0i; p0 <= nDivP0; p0++) {
			for(Integer p1 = p1i; p1 <= nDiv; p1++) {
				for(Integer p2 = p2i; p2 <= nDivP2; p2++) {
					for(Integer p3 = p3i; p3 <= nDiv; p3++) {
						for(Integer p4 = p4i; p4 <= nDiv; p4++) {
							try {
								long start = System.currentTimeMillis();
								
								FileWriter writer = new FileWriter(savedData);
								writer.write(p0 + "," + p1 + "," + p2 + "," + p3 + "," + p4 + "," + time);
								writer.close();
								
								System.out.println(p0 + "," + p1 + "," + p2 + "," + p3 + "," + p4);
								
								List<Double> configParams = new ArrayList<Double>();
								configParams.add((p0*1.0)/(nDivP0*1.0));
								configParams.add((p1*1.0)/(nDiv*1.0));
								configParams.add((p2*1.0)/(nDivP2*1.0));
								configParams.add((p3*1.0)/(nDiv*1.0));
								configParams.add((p4*1.0)/(nDiv*1.0));
								
								ProcessModel pmDiscovered = miner.discover(log, configParams);
								
								if (pmDiscovered == null) {
									System.out.println("Skipped metrics...");
									
									long error = System.currentTimeMillis();									
									time += error - start;
									
									continue;
								}
								
								List<Double> pmMetrics = miner.metrics(pmDiscovered);

								long end = System.currentTimeMillis();
								
								time += end - start;

								printWriter.println(configParams.toString() + "|" + pmMetrics.toString());
								printWriter.flush();
								
							} catch (OutOfMemoryError e) {
								System.out.println("Out of memory!");
							}
						}
					}
				}
			}
		}
		
		printWriter.close();
		
		System.out.println("Time: " + time);
	}
}
