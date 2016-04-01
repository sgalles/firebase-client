package org.sgalles.firebase;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;
import org.aeonbits.owner.ConfigFactory;

@Sources({ "file:~/firebase.properties"})
public interface Configuration extends Config{

	
	@Key("firebase.url")
	String getFirebaseAppUrl();
	
	static Configuration instance(){
		return (Configuration)ConfigFactory.create(Configuration.class);
	}
}
