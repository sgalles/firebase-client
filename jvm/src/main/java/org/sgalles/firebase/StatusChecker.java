package org.sgalles.firebase;

import org.zeroturnaround.exec.ProcessExecutor;

import com.firebase.client.Firebase;

public class StatusChecker implements Runnable{
	

	private Boolean motioneyeosRunning = null; 
	
	@Override
	public void run() {
		Firebase ref = new Firebase(Configuration.instance().getFirebaseAppUrl());
		while(true){
			 try {
				Thread.sleep(1000);
				int exitValue = new ProcessExecutor().command(
						"/bin/sh", 
						"-c",
						"wget -q -t 1 -T 2 -O - http://127.0.0.1:$port/static/img/motioneye-logo.svg &>/dev/null"
				).readOutput(true).execute().getExitValue();
				final Boolean updatedMotioneyeosRunning = Boolean.valueOf(exitValue == 0);
				if(!updatedMotioneyeosRunning.equals(motioneyeosRunning)){
					motioneyeosRunning = updatedMotioneyeosRunning;
					System.out.println("motioneyeosRunning=" + motioneyeosRunning);
					ref.child("motioneyeosRunning").setValue(motioneyeosRunning);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
