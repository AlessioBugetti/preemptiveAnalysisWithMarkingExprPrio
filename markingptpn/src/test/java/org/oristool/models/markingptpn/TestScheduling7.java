package org.oristool.models.markingptpn;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestScheduling7 {
    TaskSet taskSet;
    Semaphore semaphore;

    @BeforeEach
    void setUp() {
        taskSet = new TaskSet();
        semaphore = new Semaphore();

        Task task1 = new Task(10, 10);
        Task task2 = new Task(8, 15);
        task1.addChunk(new Chunk(1, 2, semaphore));
        task2.addChunk(new Chunk(1, 2, semaphore));

        taskSet.addTask(task1);
        taskSet.addTask(task2);
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