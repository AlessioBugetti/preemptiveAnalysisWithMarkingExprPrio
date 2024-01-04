package org.oristool.models.markingptpn;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// Test con 5 Task
class TestScheduling3 {
    TaskSet taskSet;

    @BeforeEach
    void setUp() {
        taskSet = new TaskSet();

        Task task1 = new Task(8, 10);
        Task task2 = new Task(14, 15);
        Task task3 = new Task(20, 20);
        Task task4 = new Task(20, 24);
        Task task5 = new Task(30, 30);
        task1.addChunk(new Chunk(0, 2));
        task2.addChunk(new Chunk(0, 2));
        task3.addChunk(new Chunk(0, 2));
        task4.addChunk(new Chunk(0, 2));
        task5.addChunk(new Chunk(0, 2));

        taskSet.addTask(task1);
        taskSet.addTask(task2);
        taskSet.addTask(task3);
        taskSet.addTask(task4);
        taskSet.addTask(task5);
    }

    @Test
    void testEDF() {
        EDFSchedulingPCEP edf = new EDFSchedulingPCEP();
        ArrayList<double[]> bounds = edf.schedule(taskSet);

        int c = 0;
        for (Task t : taskSet.getTasks()) {
            assertTrue(bounds.get(c)[1] <= t.getDeadline());
            c++;
        }
    }

    @Test
    void testRM() {
        RMScheduling rm = new RMScheduling();
        ArrayList<double[]> bounds = rm.schedule(taskSet);
        int c = 0;
        for (Task t : taskSet.getTasks()) {
            assertTrue(bounds.get(c)[1] <= t.getDeadline());
            c++;
        }
    }
}