package org.oristool.models.markingptpn;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// Test con 4 Task
class TestScheduling2 {
	TaskSet taskSet;
	ArrayList<Task> tasks1;
	
	@BeforeEach
	void setUp() {
		taskSet = new TaskSet();
		tasks1 = new ArrayList<>();
		Task task1 = new Task(10, 10);
		Task task2 =  new Task(8, 15);
		Task task3 = new Task(18, 18);
		Task task4 = new Task(25, 30);
		task1.addChunk(new Chunk(0, 3));
        task2.addChunk(new Chunk(0, 3));
        task3.addChunk(new Chunk(0, 3));
        task4.addChunk(new Chunk(0, 3));

		taskSet.addTask(task1);
		taskSet.addTask(task2);
		taskSet.addTask(task3);
		taskSet.addTask(task4);

		
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