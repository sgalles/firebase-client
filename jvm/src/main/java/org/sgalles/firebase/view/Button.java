package org.sgalles.firebase.view;

import java.util.concurrent.atomic.AtomicReference;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class Button extends AbstractWidget {

	private final DatabaseReference nodeRunning;
	private final DatabaseReference nodeSwitch;
	private final ButtonModel model;
	private String label;
	
	
	public Button(DatabaseReference ref, String id, String label) {
		super(ref,id);
		this.label = label;
		getRoot().child("label").setValue(label);
		nodeRunning = getRoot().child("running");
		nodeSwitch = getRoot().child("switch");
		nodeSwitch.setValue(false);
		model = new ButtonModel();
	}

	public ButtonModel getModel() {
		return model;
	}
	
	
	public void setListener(final ButtonListener listener) {
		nodeSwitch.addValueEventListener(new ValueEventListener() {
			
			@Override
			public void onDataChange(DataSnapshot snapshot) {
				Boolean mustSwitch = snapshot.getValue(Boolean.class);
		        if(Boolean.TRUE.equals(mustSwitch)){
		        	nodeSwitch.setValue(false);
		        	Boolean running = model.getRunning();
		        	if(running != null){
		        		listener.onSwitch(!running);
		        	}else{
		        		System.out.println("onDataChange : Undetermined state for button " + label);
		        	}
		        }
			}
			
			@Override
			public void onCancelled(DatabaseError error) {
				System.out.println("FirebaseError=" + error);
			}
		});
		
	}

	public class ButtonModel{
		
		private AtomicReference<Boolean> running = new AtomicReference<>(null);
		public Boolean getRunning(){
			return running.get();
		}
		public void setRunning(boolean updatedRunning){
			if(!Boolean.valueOf(updatedRunning).equals(running.get())){
				running.set(updatedRunning);
				nodeRunning.setValue(updatedRunning);
			}
		}
	}
	
	
	@FunctionalInterface
	public interface ButtonListener{
		void onSwitch(boolean start);
	}
}
