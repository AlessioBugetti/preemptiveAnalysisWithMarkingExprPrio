package org.oristool.models.markingptpn;

public class Chunk {
	private int minExecutionTime;
	private int maxExecutionTime;
	private Semaphore semaphore;
	private Task task;
	
	public Chunk(int minExecutionTime, int maxExecutionTime) {
		this.minExecutionTime=minExecutionTime;
		this.maxExecutionTime=maxExecutionTime;
		this.semaphore=null;
	}
	
	public Chunk(int minExecutionTime, int maxExecutionTime, Semaphore semaphore) {
		this.minExecutionTime=minExecutionTime;
		this.maxExecutionTime=maxExecutionTime;
		this.semaphore=semaphore;
	}
	
	public int getMinExecutionTime() {
		return this.minExecutionTime;
	}
	
	public int getMaxExecutionTime() {
		return this.maxExecutionTime;
	}
	
	public boolean useSemaphore() {
		if(semaphore==null)
			return false;
		else
			return true;
	}
	
	public Semaphore getSemaphore() {
		return this.semaphore;
	}
	
	public void setSemaphore(Semaphore semaphore) {
		this.semaphore = semaphore;
	}
	
	public Task getTask() {
		return this.task;
	}
	
	public void setTask(Task task) {
		this.task = task;
	}
}
