package selevminer.algorithm;

import java.io.File;

import dk.brics.automaton.Automaton;
import minerful.concept.ProcessModel;
import selevminer.model.PMMiner;
import selevminer.model.PMWrapper;

public class MINERfulPMWrapper extends PMWrapper<ProcessModel> {
	
	private static final long serialVersionUID = 1L;
	public Automaton automaton;

	public MINERfulPMWrapper(PMWrapper<ProcessModel> chromosome) {
		super(chromosome);
		this.pm = chromosome.pm;
		this.metrics = chromosome.metrics;
	}

	public Automaton getAutomaton(File log, PMMiner<ProcessModel> miner) {
		if(pm == null) {
			this.getPm(log, miner);
		}
		
		if(automaton == null && pm != null) {
			automaton = pm.buildAutomaton();
		}
		
		return automaton;
	}

}
