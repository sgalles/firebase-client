package org.sgalles.firebase;

import java.util.concurrent.TimeUnit;

import org.zeroturnaround.exec.ProcessExecutor;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class MotioneyeosController implements ValueEventListener{

	private static final String MOTIONEYEOS_CHILD = "motioneyeos";
	private static final String RUNNING_CHILD = "running";
	private static final String SWITCH_CHILD = "switch";
	
	private Boolean motioneyeosRunning = null;
	private final Firebase ref;

	public static void main(String[] args) throws Exception {
		new MotioneyeosController().loop();
	}
	
	public MotioneyeosController(){
		ref = new Firebase(Configuration.instance().getFirebaseAppUrl());
		ref.addValueEventListener(this);
	}


	public void loop() {
		while (true) {
			try {
				Thread.sleep(1000);
				checkMotioneyeosRunning();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
	

	@Override
	public void onDataChange(DataSnapshot snapshot) {
		System.out.println("onDataChange=" + snapshot);
		Boolean mustSwitch = snapshot.child(MOTIONEYEOS_CHILD).child(SWITCH_CHILD).getValue(Boolean.class);
        if(Boolean.TRUE.equals(mustSwitch)){
        	switchMotioneyeos();
        }
		
	}
	

	private synchronized void checkMotioneyeosRunning() {

		try{
			int exitValue = new ProcessExecutor()
					.command("/bin/sh", "-c",
							"wget -q -t 1 -T 2 -O - http://127.0.0.1:$port/static/img/motioneye-logo.svg &>/dev/null")
					.readOutput(true)
					.execute().getExitValue();
			final Boolean updatedMotioneyeosRunning = Boolean.valueOf(exitValue == 0);
			
			if (!updatedMotioneyeosRunning.equals(motioneyeosRunning)) {
				motioneyeosRunning = updatedMotioneyeosRunning;
				System.out.println("motioneyeosRunning=" + motioneyeosRunning);
				firebaseUpdateMotioneyeosRunning();
			}
		}catch(Exception e){
			throw new IllegalStateException(e);
		}
	}

	private void firebaseUpdateMotioneyeosRunning() {
		System.out.println("Firebaseset value : " + RUNNING_CHILD); 
		ref.child(MOTIONEYEOS_CHILD).child(RUNNING_CHILD).setValue(motioneyeosRunning);
	}
	
	private void firebaseResetSwitch() {
		System.out.println("Firebaseset value : " + SWITCH_CHILD); 
		ref.child(MOTIONEYEOS_CHILD).child(SWITCH_CHILD).setValue(false);
	}
	
	private synchronized void switchMotioneyeos(){
		
		try{
			firebaseResetSwitch();
			if(!motioneyeosRunning){
				System.out.println("Starting motioneye...");
				new ProcessExecutor()
				.command("/etc/init.d/S85motioneye", "start")
				.start();
				System.out.println("Starting motioneye...done");
			}else{
				System.out.println("Stopping motioneye...");
				new ProcessExecutor()
				.command("/etc/init.d/S85motioneye", "stop")
				.start();
				System.out.println("Stopping motioneye...done");
			}
		}catch(Exception e){
			throw new IllegalStateException(e);
		}
	}


	@Override
	public void onCancelled(FirebaseError error) {
		System.out.println("FirebaseError=" + error);
	}

}
