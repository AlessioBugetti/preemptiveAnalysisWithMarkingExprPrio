package org.oristool.models.markingptpn;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// Test con 3 Task con deadline minore su task con periodo maggiore
class TestScheduling5 {
    TaskSet taskSet;

    @BeforeEach
    void setUp() {
        taskSet = new TaskSet();

        Task task1 = new Task(20, 40);
        Task task2 = new Task(22, 50);
        Task task3 = new Task(24, 25);
        task1.addChunk(new Chunk(1, 6));
        task2.addChunk(new Chunk(1, 6));
        task3.addChunk(new Chunk(1, 6));

        taskSet.addTask(task1);
        taskSet.addTask(task2);
        taskSet.addTask(task3);
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