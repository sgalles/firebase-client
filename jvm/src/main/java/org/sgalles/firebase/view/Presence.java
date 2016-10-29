package org.sgalles.firebase.view;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class Presence extends AbstractWidget {

	
	public Presence(DatabaseReference ref, String id) {
		super(ref,id);
		DatabaseReference connectedChild = getRoot().child("connected");
		DatabaseReference amOnline = ref.child(".info/connected");
		amOnline.addValueEventListener(new ValueEventListener() {
			
			@Override
			public void onDataChange(DataSnapshot amOnlineSnapshot) {
				if (amOnlineSnapshot.exists()) {
					connectedChild.onDisconnect().setValue(false);
					connectedChild.setValue(true);
				}
				
			}
			
			@Override
			public void onCancelled(DatabaseError error) {
				System.out.println("FirebaseError=" + error);
			}
		});
	}

	
}
