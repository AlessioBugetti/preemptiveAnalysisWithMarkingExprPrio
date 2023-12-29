package org.oristool.models.markingptpn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.oristool.analyzer.graph.SuccessionGraph;
import org.oristool.models.ptpn.PreemptiveAnalysis;
import org.oristool.models.ptpn.PreemptiveTransitionFeature;
import org.oristool.models.ptpn.TimelinessAnalysis.PreemptiveTimelinessAnalysis;
import org.oristool.models.tpn.TimedTransitionFeature;
import org.oristool.petrinet.*;

public class RMScheduling implements Scheduling {

    @Override
    public ArrayList<double[]> schedule(TaskSet taskSet) {
        PetriNet pn = new PetriNet();
        Resource cpu = new Resource("cpu");
        ArrayList<Task> tasks = new ArrayList<>(taskSet.getTasks());

        // Copy TaskSet
        ArrayList<Task> tmpTasks = new ArrayList<>(tasks);

        // Calculate priorities for tasks
        int priority = 1;
        Map<Task, Integer> priorities = new HashMap<>();
        while (!tmpTasks.isEmpty()) {
            Task minTask = Collections.min(tmpTasks, Comparator.comparingInt(Task::getPeriod));
            priorities.put(minTask, priority);
            priority++;
            tmpTasks.remove(minTask);
        }

        // Create pTPN
        int taskIndex = 0;
        Map<Semaphore, ArrayList<Transition>> waitTransition = new HashMap<>();
        Map<Semaphore, ArrayList<Transition>> signalTransition = new HashMap<>();

        for (Task task : tasks) {
            int transitionSubIndex = 0;
            int placeSubIndex = 0;
            String periodString = Integer.toString(task.getPeriod());

            Transition periodTransition = pn.addTransition("transition" + taskIndex + transitionSubIndex++);
            periodTransition.addFeature(new TimedTransitionFeature(periodString, periodString));

            Place periodPlace = pn.addPlace("place" + taskIndex + placeSubIndex++);
            pn.addPostcondition(periodTransition, periodPlace);
            Place lastCreatedPlace = periodPlace;

            int numChunk = 0;
            for (Chunk chunk : task.getChunks()) {
                numChunk++;
                Transition computeTransition;
                if (chunk.useSemaphore()) {
                    Transition acquireSemaphoreTransition;
                    int chunkPriority = priorities.get(chunk.getTask());

                    if (chunkPriority > getCeiling(chunk.getSemaphore(), taskSet, priorities)) {
                        Transition increasePriorityTransition = pn.addTransition("transition" + taskIndex + transitionSubIndex++);
                        increasePriorityTransition.addFeature(new TimedTransitionFeature("0", "0"));
                        increasePriorityTransition.addFeature(new PreemptiveTransitionFeature(cpu, new ResourcePriority(chunkPriority)));
                        pn.addPrecondition(lastCreatedPlace, increasePriorityTransition);

                        Place increasePriorityPlace = pn.addPlace("place" + taskIndex + placeSubIndex++);
                        lastCreatedPlace = increasePriorityPlace;
                        pn.addPostcondition(increasePriorityTransition, increasePriorityPlace);

                        acquireSemaphoreTransition = pn.addTransition("transition" + taskIndex + transitionSubIndex++);
                        acquireSemaphoreTransition.addFeature(new TimedTransitionFeature("0", "0"));
                        acquireSemaphoreTransition.addFeature(new PreemptiveTransitionFeature(cpu, new ResourcePriority(getCeiling(chunk.getSemaphore(), taskSet, priorities))));
                        pn.addPrecondition(lastCreatedPlace, acquireSemaphoreTransition);

                        Place acquireSemaphorePlace = pn.addPlace("place" + taskIndex + placeSubIndex++);
                        lastCreatedPlace = acquireSemaphorePlace;
                        pn.addPostcondition(acquireSemaphoreTransition, acquireSemaphorePlace);

                        computeTransition = pn.addTransition("transition" + taskIndex + transitionSubIndex++);
                        String earliestFiringTime = Integer.toString(chunk.getMinExecutionTime());
                        String latestFiringTime = Integer.toString(chunk.getMaxExecutionTime());
                        computeTransition.addFeature(new TimedTransitionFeature(earliestFiringTime, latestFiringTime));
                        computeTransition.addFeature(new PreemptiveTransitionFeature(cpu, new ResourcePriority(getCeiling(chunk.getSemaphore(), taskSet, priorities))));
                        pn.addPrecondition(lastCreatedPlace, computeTransition);
                    } else {
                        acquireSemaphoreTransition = pn.addTransition("transition" + taskIndex + transitionSubIndex++);
                        acquireSemaphoreTransition.addFeature(new TimedTransitionFeature("0", "0"));
                        acquireSemaphoreTransition.addFeature(new PreemptiveTransitionFeature(cpu, new ResourcePriority(chunkPriority)));
                        pn.addPrecondition(lastCreatedPlace, acquireSemaphoreTransition);

                        Place acquireSemaphorePlace = pn.addPlace("place" + taskIndex + placeSubIndex++);
                        lastCreatedPlace = acquireSemaphorePlace;
                        pn.addPostcondition(acquireSemaphoreTransition, acquireSemaphorePlace);

                        computeTransition = pn.addTransition("transition" + taskIndex + transitionSubIndex++);
                        String earliestFiringTime = Integer.toString(chunk.getMinExecutionTime());
                        String latestFiringTime = Integer.toString(chunk.getMaxExecutionTime());
                        computeTransition.addFeature(new TimedTransitionFeature(earliestFiringTime, latestFiringTime));
                        computeTransition.addFeature(new PreemptiveTransitionFeature(cpu, new ResourcePriority(chunkPriority)));
                        pn.addPrecondition(lastCreatedPlace, computeTransition);
                    }

                    // Add transitions to wait and signal lists
                    waitTransition.computeIfAbsent(chunk.getSemaphore(), k -> new ArrayList<>()).add(acquireSemaphoreTransition);
                    signalTransition.computeIfAbsent(chunk.getSemaphore(), k -> new ArrayList<>()).add(computeTransition);

                } else {
                    computeTransition = pn.addTransition("transition" + taskIndex + transitionSubIndex++);
                    String earliestFiringTime = Integer.toString(chunk.getMinExecutionTime());
                    String latestFiringTime = Integer.toString(chunk.getMaxExecutionTime());
                    computeTransition.addFeature(new TimedTransitionFeature(earliestFiringTime, latestFiringTime));
                    computeTransition.addFeature(new PreemptiveTransitionFeature(cpu, new ResourcePriority(priorities.get(task))));
                    pn.addPrecondition(lastCreatedPlace, computeTransition);
                }

                // Add next place if there are more chunks
                if (numChunk < task.getChunks().size()) {
                    Place nextPlace = pn.addPlace("place" + taskIndex + placeSubIndex++);
                    lastCreatedPlace = nextPlace;
                    pn.addPostcondition(computeTransition, nextPlace);
                }
            }

            taskIndex++;
        }

        // Insert marking
        Marking m = new Marking();
        for (int c = 0; c < tasks.size(); c++) {
            m.addTokens(pn.getPlace("place" + c + "0"), 1);
        }

        int numMutex = 0;
        for (Semaphore semaphore : waitTransition.keySet()) {
            Place mutex = pn.addPlace("placeMutex" + numMutex);
            waitTransition.get(semaphore).forEach(wTransition -> pn.addPrecondition(mutex, wTransition));
            signalTransition.get(semaphore).forEach(sTransition -> pn.addPostcondition(sTransition, mutex));
            m.addTokens(pn.getPlace("placeMutex" + numMutex), 1);
            numMutex++;
        }

        // Perform analysis
        PreemptiveAnalysis preeAnalysis = PreemptiveAnalysis.builder().build();
        SuccessionGraph graph = preeAnalysis.compute(pn, m);
        PreemptiveTimelinessAnalysis analysis = PreemptiveTimelinessAnalysis.builder().build();

        // Calculate bounds
        ArrayList<double[]> bounds = new ArrayList<>();
		for(int numTask=0; numTask<taskSet.getTasks().size(); numTask++) {
			int count = 0;
			String transitionNamePrefix = "transition"+numTask;
			for(Transition tr: pn.getTransitions()) {
				if(tr.getName().startsWith(transitionNamePrefix))
					count++;
			}
			String transitionNameStart = transitionNamePrefix + "0";
		    String transitionNameEnd = transitionNamePrefix + (count - 1);
		    bounds.add(analysis.compute(pn, graph, pn.getTransition(transitionNameStart), pn.getTransition(transitionNameEnd)));
		}

        // Print results
        taskIndex = 0;
        for (Task task : taskSet.getTasks()) {
            System.out.println();
            System.out.println("Task: " + taskIndex);
            System.out.println("Deadline: " + task.getDeadline());
            System.out.println("Il minimo tempo di completamento è: " + bounds.get(taskIndex)[0]);
            System.out.println("Il massimo tempo di completamento è: " + bounds.get(taskIndex)[1]);
            taskIndex++;
        }

        return bounds;
    }

    private int getCeiling(Semaphore semaphore, TaskSet taskSet, Map<Task, Integer> priorities) {
        int ceiling = Integer.MAX_VALUE;

        for (Task task : taskSet.getTasks()) {
            for (Chunk chunk : task.getChunks()) {
                if (chunk.getSemaphore() == semaphore) {
                    int taskPriority = priorities.get(task);
                    ceiling = Math.min(ceiling, taskPriority);
                }
            }
        }

        return ceiling;
    }

}
