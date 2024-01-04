package org.oristool.models.markingptpn;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestScheduling10 {
    TaskSet taskSet;
    Semaphore semaphore;

    @BeforeEach
    void setUp() {
        taskSet = new TaskSet();
        semaphore = new Semaphore();

        Task task1 = new Task(20, 20);
        Task task2 = new Task(22, 25);
        Task task3 = new Task(20, 22);
        task1.addChunk(new Chunk(1, 2, semaphore));
        task2.addChunk(new Chunk(2, 4, semaphore));
        task3.addChunk(new Chunk(3, 4, semaphore));
        task3.addChunk(new Chunk(3, 5));

        taskSet.addTask(task1);
        taskSet.addTask(task2);
        taskSet.addTask(task3);
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