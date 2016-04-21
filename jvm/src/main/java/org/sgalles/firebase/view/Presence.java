package org.sgalles.firebase.view;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class Presence extends AbstractWidget {

	
	public Presence(Firebase ref, String id) {
		super(ref,id);
		Firebase connectedChild = getRoot().child("connected");
		Firebase amOnline = ref.child(".info/connected");
		amOnline.addValueEventListener(new ValueEventListener() {
			
			@Override
			public void onDataChange(DataSnapshot amOnlineSnapshot) {
				if (amOnlineSnapshot.exists()) {
					connectedChild.onDisconnect().setValue(false);
					connectedChild.setValue(true);
				}
				
			}
			
			@Override
			public void onCancelled(FirebaseError error) {
				System.out.println("FirebaseError=" + error);
			}
		});
	}

	
}
