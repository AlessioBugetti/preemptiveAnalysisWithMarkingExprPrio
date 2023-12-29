package org.oristool.models.markingpt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.util.ArithmeticUtils;
import org.oristool.models.tpn.TimedTransitionFeature;
import org.oristool.petrinet.Marking;
import org.oristool.petrinet.PetriNet;
import org.oristool.petrinet.Place;
import org.oristool.petrinet.Resource;
import org.oristool.petrinet.Transition;
import org.oristool.models.ptpn.TimelinessAnalysis.PreemptiveTimelinessAnalysis;
import org.oristool.analyzer.graph.SuccessionGraph;
import org.oristool.models.markingptpn.MarkingPreemptiveTransitionFeature;
import org.oristool.models.markingptpn.MarkingResourcePriority;
import org.oristool.models.markingptpn.MarkingPreemptiveAnalysis;


public class EDFSchedulingPCEP implements Scheduling{
	Map<Integer, ArrayList<Integer>> transitionPlacesDict = new HashMap<Integer, ArrayList<Integer>>();
	
	@Override
	public ArrayList<double[]> schedule(TaskSet taskSet) {
		ArrayList<Integer> times = priorityTimeCalculator(taskSet.getTasks());
		
		PetriNet pn = new PetriNet();
		Resource cpu = new Resource("cpu");
		
		// Crea la sottorete di petri del tempo
		int i = 0;
		for(int t : times) {
			Transition tmpT = pn.addTransition("timeTransition"+i);
			String str = Integer.toString(t);
			tmpT.addFeature(new TimedTransitionFeature(str, str));
			Place tmpP = pn.addPlace("timePlace"+i);
			pn.addPostcondition(tmpT, tmpP);
			if(i == times.size()-1) {
				pn.addPrecondition(tmpP, pn.getTransition("timeTransition0"));
			}
			if (i != 0){
				String tmp = Integer.toString(i-1);
				pn.addPrecondition(pn.getPlace("timePlace"+tmp), tmpT);
			}
			i++;
		}
		int n = pn.getPlaces().size();
		
		// Crea la sottorete dei task
		i = 0;
		for (Task t : taskSet.getTasks()) {
			Transition tmpTransition1 = pn.addTransition("transition"+i+"0");
			Place tmpPlace = pn.addPlace("place"+i+"0");
			String periodString = Integer.toString(t.getPeriod());
			tmpTransition1.addFeature(new TimedTransitionFeature(periodString, periodString));
			pn.addPostcondition(tmpTransition1, tmpPlace);
			
			// Stringa dei place
			String priority = "";
			
			// Estrae i places trovati in time calculator
			ArrayList<Integer> places = transitionPlacesDict.get(t.getId());
			
			// I Place vengono usati per creare la stringa della MarkingExpr
			for(int j=0; j<n; j++) {
				boolean found = false;
				if(places != null) {
					for (int k=0; k<places.size(); k++) {
						if(j == places.get(k)) {
							priority += ("timePlace"+j+"*0");
							found = true;
							break;
						}
					}
					if (!found) {
						priority += ("timePlace"+j+"*99");
					}
					
				}else {
					priority += ("timePlace"+j+"*99");
				}
				if(j!=n-1) {
					priority += "+";
				}
			}
			
			Place lastPlace = tmpPlace;
			int j = 1;
			int k = 1;
			
			for(Chunk c: t.getChunks()) {
			Transition tmpTransition2 = pn.addTransition("transition"+i+j);
			tmpTransition2.addFeature(new TimedTransitionFeature(Integer.toString(c.getMinExecutionTime()), Integer.toString(c.getMaxExecutionTime())));
			tmpTransition2.addFeature(new MarkingPreemptiveTransitionFeature(cpu, 
					new MarkingResourcePriority(99, priority, pn)));
			pn.addPrecondition(lastPlace, tmpTransition2);
			if(j < t.getChunks().size()) {
				Place fooPlace = pn.addPlace("place"+i+k);
				lastPlace = fooPlace;
				pn.addPostcondition(tmpTransition2, lastPlace);
				k++;
			}
			j++;
			}
			i++;
		}
		
		Marking m = new Marking();
		m.addTokens(pn.getPlace("timePlace0"), 1);
		/*
		for(int z=0; z<taskSet.getTasks().size();z++) {
			m.addTokens(pn.getPlace("place"+z+"0"),1);
		}
		*/
		
		System.out.println(pn);
		
		MarkingPreemptiveAnalysis analysis = new MarkingPreemptiveAnalysis();	
		SuccessionGraph graph = analysis.compute(pn, m);
		
		PreemptiveTimelinessAnalysis analyzer = PreemptiveTimelinessAnalysis.builder().build();
		ArrayList<double[]> bounds = new ArrayList<double[]>();
		int nTasks = taskSet.getTasks().size();
		for(int numTask=0; numTask<nTasks; numTask++) {
			int count = 0;
			for(Transition tr: pn.getTransitions()) {
				if(tr.getName().startsWith("transition"+numTask))
					count++;
			}
			bounds.add(analyzer.compute(pn, graph, pn.getTransition("transition"+numTask+"0"), pn.getTransition("transition"+numTask+(count-1))));
		}
		
		
		
		int c = 0;
		for (Task t : taskSet.getTasks()) {
			System.out.println();
			System.out.println("Task: "+ c);
			System.out.println("La deadline è: "+ t.getDeadline());
			System.out.println("Il minimo tempo di completamento è: "+bounds.get(c)[0]);
			System.out.println("Il massimo tempo di completamento è: "+bounds.get(c)[1]);
			c++;
		}
		
		
		return bounds;
		
	}
	
	private ArrayList<Integer> priorityTimeCalculator(ArrayList<Task> tasks) {
		int mcm=1;
		for (Task t : tasks) {
			mcm = ArithmeticUtils.lcm(mcm, t.getPeriod());	
		}
		System.out.println("MCM: "+mcm);
		ArrayList<Integer> times = new ArrayList<Integer>();
		int prev = 0;
		int min;
		int chosen = 0;
		int sum = 0;
		int i = 0;
		
		while(true) {
			min = 9999;
			for (Task t : tasks) {
				if(calculateTime(t) - prev < min && calculateTime(t) - prev > 0) {
					min = calculateTime(t) - prev;
					chosen = t.getId();
				}
				if(calculateTime2(t) - prev < min){
					min = calculateTime2(t) - prev;
					chosen = t.getId();
				}
			}
			sum += min;
			if (sum > mcm)
				break;
			for (Task t : tasks) {
				if(t.getId() == chosen) {
					ArrayList<Integer> places;
					if(transitionPlacesDict.containsKey(t.getId())) {
						places = transitionPlacesDict.get(t.getId());
					}else {
						places = new ArrayList<Integer>();
					}
					places.add(i);
					transitionPlacesDict.put(t.getId(), places);
					t.counterUp();
					break;
				}
			}
			prev += min;
			times.add(min);
			i++;
		}
		System.out.println(times);
		
		return times;
	}
	
	private int calculateTime(Task t) {
		return (t.getCounter()*t.getPeriod() + t.getDeadline());
	}
	
	private int calculateTime2(Task t) {
		return ((t.getCounter()+1)*t.getPeriod() + t.getDeadline());
	}
}
