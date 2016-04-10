package org.sgalles.firebase;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;
import org.aeonbits.owner.ConfigFactory;

@Sources({ 
	"file:/data/opt/firebase.properties",
	"file:~/firebase.properties"
	})
public interface Configuration extends Config{

	
	@Key("firebase.url")
	String getFirebaseAppUrl();
	
	@Key("firebase.secret")
	String getFirebaseSecret();
	
	static Configuration instance(){
		return (Configuration)ConfigFactory.create(Configuration.class);
	}
}
