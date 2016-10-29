package org.sgalles.firebase.view;

import com.google.firebase.database.DatabaseReference;

public class AbstractWidget {

	private static final String CHILD_WIDGETS = "widgets";
	private static final String CHILD_TYPE = "type";
	
	private final DatabaseReference firebaseRef;
	private final DatabaseReference root;
	private final String id;

	public AbstractWidget(DatabaseReference ref, String id) {
		super();
		this.id = id;
		this.firebaseRef = ref;
		this.root = ref.child(CHILD_WIDGETS).child(id);
		this.root.child(CHILD_TYPE) .setValue(getClass().getSimpleName());
	}

	public DatabaseReference getRoot() {
		return root;
	}

	public DatabaseReference getFirebaseRef() {
		return firebaseRef;
	}

	public String getId() {
		return id;
	}
	
	
	
	
	
}
