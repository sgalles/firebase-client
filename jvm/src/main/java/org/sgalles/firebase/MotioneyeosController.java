package org.sgalles.firebase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;

import org.sgalles.firebase.view.Button;
import org.sgalles.firebase.view.Presence;
import org.zeroturnaround.exec.ProcessExecutor;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger.Level;

public class MotioneyeosController {
	
	private DatabaseReference ref = null;
	private final File motionEyeOsService;
	private Button buttonMotioneyeos;
	private Button buttonFirewall;
	@SuppressWarnings("unused")
	private Presence presence;

	public static void main(String[] args) throws Exception {
		System.out.println("Starting MotioneyeosController");
		MotioneyeosController controller = new MotioneyeosController();
		controller.loop();
	}
	
	public MotioneyeosController(){
		motionEyeOsService = findMotionEyeOsService();
		initFirewall();
		initFirebase();
		buttonMotioneyeos = new Button(ref, "motioneyeos", "MotionEyeOs");
		buttonFirewall = new Button(ref, "firewall", "Firewall");
		buttonFirewall = new Button(ref, "firewall", "Firewall");
		presence = new Presence(ref,"presence");
		updateModels();
		buttonMotioneyeos.setListener(this::switchMotionEyeOs);
		buttonFirewall.setListener(this::switchFirewall);
	}
	
	private static File findMotionEyeOsService(){
		File servicesDir = new File("/etc/init.d");
		FilenameFilter findMotionEyeOsService = (File parent, String name) ->  name.endsWith("motioneye");
		return new File(servicesDir, servicesDir.list(findMotionEyeOsService)[0]);
	}
	
	public void initFirewall(){
		if(!isFirewallRunning()){
			switchFirewall(true);
		}
	}
	
	public void initFirebase(){
		try {
			FirebaseOptions options = new FirebaseOptions.Builder()
					.setServiceAccount(new FileInputStream(Configuration.instance().getFirebaseServiceAccountFile()))
					.setDatabaseUrl(Configuration.instance().getFirebaseAppUrl())
					.build();
					
			FirebaseApp.initializeApp(options);
			//FirebaseDatabase.getInstance().setLogLevel(Level.DEBUG);
			ref = FirebaseDatabase.getInstance().getReference();
		} catch (FileNotFoundException e) {
			throw new IllegalStateException(e);
		}		
	}


	public void loop() {
		while (true) {
			try {
				Thread.sleep(1000);
				updateModels();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	private void updateModels() {
		buttonMotioneyeos.getModel().setRunning(isMotionEyeRunning());
		buttonFirewall.getModel().setRunning(isFirewallRunning());
	}
	
	private boolean isMotionEyeRunning(){
		try{
			int exitValue = new ProcessExecutor()
					.command("/bin/sh", "-c",
							"wget -q -t 1 -T 2 -O - http://127.0.0.1:$port/static/img/motioneye-logo.svg &>/dev/null")
					.readOutput(true)
					.execute().getExitValue();
			return Boolean.valueOf(exitValue == 0);
			
		}catch(Exception e){
			throw new IllegalStateException(e);
		}
	}
	
	
	private boolean isFirewallRunning(){
		try{
			int exitValue = new ProcessExecutor()
					.command("/bin/sh", "-c",
							"iptables -L | grep www &>/dev/null")
					.execute().getExitValue();
			return Boolean.valueOf(exitValue == 0);
		}catch(Exception e){
			throw new IllegalStateException(e);
		}
	}
	

	private void switchMotionEyeOs(boolean start) {
		try{
			new ProcessExecutor()
			.command(motionEyeOsService.getAbsolutePath(), start ? "start" : "stop")
			.start();
		}catch(Exception e){
			throw new IllegalStateException(e);
		}
	}

	private void switchFirewall(boolean start) {
		try {
			if (start) {
				System.out.println("Starting firewall...");
				new ProcessExecutor().command(
						"/usr/sbin/iptables -A INPUT -p tcp --destination-port 80 ! -s localhost -j DROP".split(" +"))
						.start();
			} else {
				System.out.println("Stopping firewall...");
				new ProcessExecutor().command("/usr/sbin/iptables -F".split(" +")).start();
			}
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}




}
