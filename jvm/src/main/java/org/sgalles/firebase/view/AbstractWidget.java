package org.sgalles.firebase.view;

import com.firebase.client.Firebase;

public class AbstractWidget {

	private static final String CHILD_WIDGETS = "widgets";
	private static final String CHILD_TYPE = "type";
	
	private final Firebase root;

	public AbstractWidget(Firebase ref, String id) {
		super();
		this.root = ref.child(CHILD_WIDGETS).child(id);
		this.root.child(CHILD_TYPE) .setValue(getClass().getSimpleName());
	}

	public Firebase getRoot() {
		return root;
	}
	
	
	
}
