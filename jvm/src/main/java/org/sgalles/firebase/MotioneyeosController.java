package org.sgalles.firebase;

import java.util.concurrent.atomic.AtomicReference;

import org.zeroturnaround.exec.ProcessExecutor;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class MotioneyeosController implements ValueEventListener{

	private static final String MOTIONEYEOS_CHILD = "motioneyeos";
	private static final String FIREWALL_CHILD = "firewall";
	private static final String RUNNING_CHILD = "running";
	private static final String SWITCH_CHILD = "switch";
	
	

	
	private AtomicReference<Boolean> motioneyeosRunning = new AtomicReference<>(null);
	private AtomicReference<Boolean> firewallRunning = new AtomicReference<>(null);
	
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
				checkFirewallRunning();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
	

	@Override
	public void onDataChange(DataSnapshot snapshot) {
		Boolean mustSwitchMotioneyeos = snapshot.child(MOTIONEYEOS_CHILD).child(SWITCH_CHILD).getValue(Boolean.class);
        if(Boolean.TRUE.equals(mustSwitchMotioneyeos)){
        	switchMotioneyeos();
        }
        Boolean mustSwitchFirewall = snapshot.child(FIREWALL_CHILD).child(SWITCH_CHILD).getValue(Boolean.class);
        if(Boolean.TRUE.equals(mustSwitchFirewall)){
        	switchFirewall();
        }
		
	}
	

	private void checkMotioneyeosRunning() {

		try{
			int exitValue = new ProcessExecutor()
					.command("/bin/sh", "-c",
							"wget -q -t 1 -T 2 -O - http://127.0.0.1:$port/static/img/motioneye-logo.svg &>/dev/null")
					.readOutput(true)
					.execute().getExitValue();
			final Boolean updatedMotioneyeosRunning = Boolean.valueOf(exitValue == 0);
			
			if (!updatedMotioneyeosRunning.equals(motioneyeosRunning.get())) {
				motioneyeosRunning.set(updatedMotioneyeosRunning);
				System.out.println("motioneyeosRunning=" + motioneyeosRunning);
				firebaseUpdateMotioneyeosRunning();
			}
		}catch(Exception e){
			throw new IllegalStateException(e);
		}
	}
	
	private void checkFirewallRunning() {

		try{
			int exitValue = new ProcessExecutor()
					.command("/bin/sh", "-c",
							"iptables -L | grep www &>/dev/null")
					.execute().getExitValue();
			final Boolean updatedFirewallRunning = Boolean.valueOf(exitValue == 0);
			
			if (!updatedFirewallRunning.equals(firewallRunning.get())) {
				firewallRunning.set(updatedFirewallRunning);
				System.out.println("firewallRunning=" + firewallRunning);
				firebaseUpdateFirewallRunning();
			}
		}catch(Exception e){
			throw new IllegalStateException(e);
		}
	}
	
	private void switchMotioneyeos(){
		
		try{
			firebaseResetMotioneyeosSwitch();
			if(Boolean.FALSE.equals(motioneyeosRunning.get())){
				System.out.println("Starting motioneye...");
				new ProcessExecutor()
				.command("/etc/init.d/S85motioneye", "start")
				.start();
			}else{
				System.out.println("Stopping motioneye...");
				new ProcessExecutor()
				.command("/etc/init.d/S85motioneye", "stop")
				.start();
			}
		}catch(Exception e){
			throw new IllegalStateException(e);
		}
	}
	
	private void switchFirewall(){
		
		try{
			firebaseResetFirewallSwitch();
			if(Boolean.FALSE.equals(firewallRunning.get())){
				System.out.println("Starting firewall...");
				new ProcessExecutor()
				.command("/usr/sbin/iptables -A INPUT -p tcp --destination-port 80 ! -s localhost -j DROP".split(" +"))
				.start();
			}else{
				System.out.println("Stopping firewall...");
				new ProcessExecutor()
				.command("/usr/sbin/iptables -F".split(" +"))
				.start();
			}
		}catch(Exception e){
			throw new IllegalStateException(e);
		}
	}

	// iptables -A INPUT -p tcp --destination-port 80 -j DROP

	private void firebaseUpdateMotioneyeosRunning() {
		ref.child(MOTIONEYEOS_CHILD).child(RUNNING_CHILD).setValue(motioneyeosRunning.get());
	}
	
	private void firebaseUpdateFirewallRunning() {
		ref.child(FIREWALL_CHILD).child(RUNNING_CHILD).setValue(firewallRunning.get());
	}
	
	private void firebaseResetMotioneyeosSwitch() {
		ref.child(MOTIONEYEOS_CHILD).child(SWITCH_CHILD).setValue(false);
	}
	
	private void firebaseResetFirewallSwitch() {
		ref.child(FIREWALL_CHILD).child(SWITCH_CHILD).setValue(false);
	}
	


	@Override
	public void onCancelled(FirebaseError error) {
		System.out.println("FirebaseError=" + error);
	}

}
