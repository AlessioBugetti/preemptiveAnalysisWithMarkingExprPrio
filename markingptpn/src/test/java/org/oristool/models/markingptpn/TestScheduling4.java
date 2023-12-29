package org.oristool.models.markingptpn;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

//Test con 3 Task con deadline minore su task con periodo maggiore
class TestScheduling4 {
	TaskSet taskSet;
	ArrayList<Task> tasks1;
	
	@BeforeEach
	void setUp() {
		taskSet = new TaskSet();
		tasks1 = new ArrayList<>();
		Task task1 =  new Task(4, 8);
		Task task2 = new Task(5, 6);
		Task task3 = new Task(8, 12);
		task1.addChunk(new Chunk(0, 2));
        task2.addChunk(new Chunk(0, 2));
        task3.addChunk(new Chunk(0, 2));
				
		taskSet.addTask(task1);
		taskSet.addTask(task2);
		taskSet.addTask(task3);
		
		//Copia del taskSet
		for (Task t : taskSet.getTasks()) {
			tasks1.add(t);
		}
	}
	
	@Test
	void testEDF() {
		EDFSchedulingPCEP edf = new EDFSchedulingPCEP();
		ArrayList<double[]> bounds = edf.schedule(taskSet);
		
		int c = 0;
		for(Task t : taskSet.getTasks()) {
			assertTrue(bounds.get(c)[1] <= t.getDeadline());
			c++;
		}
	}
	
	@Test
	void testRM() {
		RMScheduling rm = new RMScheduling();
		ArrayList<double[]> bounds = rm.schedule(taskSet);
		int c = 0;
		for(Task t : taskSet.getTasks()) {
			assertTrue(bounds.get(c)[1] <= t.getDeadline());
			c++;
		}
		
		
	}

}
