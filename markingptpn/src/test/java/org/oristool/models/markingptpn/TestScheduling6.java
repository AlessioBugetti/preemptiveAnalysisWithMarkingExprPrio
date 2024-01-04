package org.oristool.models.markingptpn;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// Test con 4 Task con deadline minore su task con periodo maggiore
class TestScheduling6 {
    TaskSet taskSet;

    @BeforeEach
    void setUp() {
        taskSet = new TaskSet();

        Task task1 = new Task(50, 50);
        Task task2 = new Task(55, 60);
        Task task3 = new Task(50, 90);
        Task task4 = new Task(50, 100);
        task1.addChunk(new Chunk(1, 15));
        task2.addChunk(new Chunk(1, 15));
        task3.addChunk(new Chunk(1, 15));
        task4.addChunk(new Chunk(1, 15));

        taskSet.addTask(task1);
        taskSet.addTask(task2);
        taskSet.addTask(task3);
        taskSet.addTask(task4);
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