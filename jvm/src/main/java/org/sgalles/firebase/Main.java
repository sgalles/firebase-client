package org.sgalles.firebase;

import java.util.Objects;

import com.firebase.client.Firebase;

public class Main {

	public static void main(String[] args) throws Exception{
		
		//Firebase ref = new Firebase(Configuration.instance().getFirebaseAppUrl());
		
		new Thread(new StatusChecker()).start();
		
		waitIndefinitly();
	}

	private static void waitIndefinitly() throws InterruptedException {
		Thread.currentThread().join();
	}
	
}
