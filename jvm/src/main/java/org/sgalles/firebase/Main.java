package org.sgalles.firebase;

public class Main {

	public static void main(String[] args) throws Exception{
		
		System.out.println(Configuration.instance().getFirebaseAppUrl());
		
		waitIndefinitly();
	}

	private static void waitIndefinitly() throws InterruptedException {
		Thread.currentThread().join();
	}
	
}
