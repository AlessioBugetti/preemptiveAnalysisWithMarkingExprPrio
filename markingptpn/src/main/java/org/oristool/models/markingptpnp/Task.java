package org.oristool.models.markingptpn;

import java.util.ArrayList;

public class Task {
	private int id;
	private int deadline;
	private int period;
	private int counter = 0;
	private ArrayList<Chunk> chunks;
	
	public Task(int deadline, int period) {
		this.deadline = deadline;
		this.period = period;
		this.chunks = new ArrayList<>();
		this.id = (int) ((deadline + period)*System.currentTimeMillis());
	}
	
	public Task(int deadline, int period, ArrayList<Chunk> chunks) {
		this.deadline = deadline;
		this.period = period;
		this.chunks = chunks;
		this.id = (int) ((deadline + period)*System.currentTimeMillis());
	}
	
	public void counterUp() {
		this.counter++;
	}
	
	public int getPeriod() {
		return period;
	}
	
	public int getId() {
		return id;
	}
	
	public int getCounter() {
		return counter;
	}
	
	public int getDeadline() {
		return deadline;
	}
	
	public void addChunk(Chunk chunk) {
		chunk.setTask(this);
		this.chunks.add(chunk);
	}
	
	public ArrayList<Chunk> getChunks()	{
		return this.chunks;
	}
}
