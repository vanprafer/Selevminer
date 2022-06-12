package selevminer.graph;

import java.util.ArrayList;

import selevminer.algorithm.MINERfulDistanceCalculator;

/**
 * Created by Sashika on 8/26/2014.
 */
public class Main {

    public static void main(String[] args) {
    	
    	MINERfulDistanceCalculator a = new MINERfulDistanceCalculator();
    	a.distance(null, null);
    	
        try {
            Integer[][] graphASource = new Integer[][]{{0, 1, 0},
                    {0, 0, 1},
                    {0, 0, 0}};

            Integer[][] graphBSource = new Integer[][]{
            		{0, 1, 0, 0, 0, 0},
                    {0, 0, 0, 1, 1, 0},
                    {0, 0, 0, 1, 0, 0},
                    {0, 0, 0, 0, 1, 0},
                    {0, 0, 0, 0, 0, 1},
                    {0, 0, 0, 0, 0, 0,}};

            Integer[][] graphCSource = new Integer[][]{
            		{0, 1, 0, 0, 0, 0},
                    {0, 0, 0, 1, 1, 0},
                    {0, 0, 0, 1, 0, 0},
                    {0, 0, 0, 0, 1, 0},
                    {0, 0, 0, 0, 0, 1},
                    {1, 0, 0, 0, 0, 0,}};

            Graph graphA = new Graph(graphASource);
            Graph graphB = new Graph(graphBSource);
            Graph graphC = new Graph(graphCSource);

            NMSimilarity similarityMeasure = new NMSimilarity(graphB, graphC, 0.0001);
            System.out.println(similarityMeasure.getGraphSimilarity());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
