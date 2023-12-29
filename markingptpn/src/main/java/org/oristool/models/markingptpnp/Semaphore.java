package org.oristool.models.markingptpnpcep;

import java.util.Random;

public class Semaphore {
	private int id;
	
	public Semaphore() {
		Random random = new Random();
		int randomNumber1 = random.nextInt(100) + 1;
        int randomNumber2 = random.nextInt(100) + 1;
		this.id = (int) ((randomNumber1 + randomNumber2)*System.currentTimeMillis());
	}
	
	public int getId() {
		return this.id;
	}
}
