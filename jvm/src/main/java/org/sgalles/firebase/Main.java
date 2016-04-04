package org.sgalles.firebase;

public class Main {

	public static void main(String[] args) throws Exception{
		
		//System.out.println(Configuration.instance().getFirebaseAppUrl());
		new Thread(new StatusChecker()).start();
		
		waitIndefinitly();
	}

	private static void waitIndefinitly() throws InterruptedException {
		Thread.currentThread().join();
	}
	
}
