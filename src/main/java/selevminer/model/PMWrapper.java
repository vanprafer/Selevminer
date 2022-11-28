package selevminer.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;

// We need a wrapper in order to reuse computed values of process models
public class PMWrapper<AnyProcessModel> extends DefaultDoubleSolution {
	
	private static final long serialVersionUID = 1L;
	public AnyProcessModel pm;
	public List<Double> metrics;

	public PMWrapper(DefaultDoubleSolution chromosome) {
		super(chromosome);
	}

	public PMWrapper(AnyProcessModel model) {
		super(0, new ArrayList<Bounds<Double>>());
		this.pm = model;
	}

	public AnyProcessModel getPm(File log, PMMiner<AnyProcessModel> miner) {
		if(pm == null) {
			try {
				pm = miner.discover(log, this.variables());
				
			} catch (OutOfMemoryError e) {
				pm = null;
				
			} catch (Exception e) {
				pm = null;
			}
		}
		
		return pm;
	}

	public List<Double> getMetrics(File log, PMMiner<AnyProcessModel> miner) {
		if(pm == null) {
			this.getPm(log, miner);
		}
		
		if(metrics == null && pm != null) {
			try {
				metrics = miner.metrics(pm);	
				
			} catch (OutOfMemoryError e) {
				// Unable to calculate. Use bad fitness
				metrics = new ArrayList<Double>();
				
				for (int i = 0; i < miner.getNumberOfObjectives(); i ++) {
	            	metrics.add(17000.);
				}
				
			} catch (Exception e) {
				// Unable to calculate. Use bad fitness
				metrics = new ArrayList<Double>();
				
				for (int i = 0; i < miner.getNumberOfObjectives(); i ++) {
	            	metrics.add(17000.);
				}
			}
		}
		
		return metrics;
	}
}
