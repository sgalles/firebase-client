package org.sgalles.firebase;

import org.zeroturnaround.exec.ProcessExecutor;

public class StatusChecker implements Runnable{
	

	private Boolean motioneyeosRunning = null; 
	
	@Override
	public void run() {
		while(true){
			 try {
				 Thread.sleep(1000);
				//Firebase ref = new Firebase(Configuration.instance().getFirebaseAppUrl());
				int exitValue = new ProcessExecutor().command(
						"/bin/sh", 
						"-c",
						"wget -q -t 1 -T 2 -O - http://127.0.0.1:$port/static/img/motioneye-logo.svg &>/dev/null"
				).readOutput(true).execute().getExitValue();
				boolean motioneyeosRunning = exitValue == 0;
				System.out.println("running=" + motioneyeosRunning);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
