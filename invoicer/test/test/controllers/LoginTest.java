/**
 * 
 */
package controllers;

import static org.junit.Assert.*;

import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;

import play.mvc.*;
import play.libs.*;
import play.test.*;
import static play.test.Helpers.*;
import com.avaje.ebean.Ebean;
import com.google.common.collect.ImmutableMap;


public class LoginTest {
	
	@Before
	public void createCleanDb() {
		start(fakeApplication(inMemoryDatabase(), fakeGlobal()));
		Ebean.save((List) Yaml.load("test-data.yml"));
	}
	
	@Test
	public void authenticateSuccess() {
		
	    Result result = callAction(				// Call the following action
		        controllers.routes.ref.			// Get reference to action ('Reverse router')
		        Application.authenticate(),		// The action to get a reference to
		        fakeRequest().					// Create a fake request..
		        	withFormUrlEncodedBody(		// ..with a form body..
		        			ImmutableMap.of(	// ..with username and password
		            "username", "robindough",
		            "password", "secret"))
		    );
	    
	    assertEquals(303, status(result));
	    assertEquals("robindough", session(result).get("username"));
	    
	}
	
	@Test
	public void authenticateFailure() {
		
	    Result result = callAction(
	        controllers.routes.ref.Application.authenticate(),
	        fakeRequest().withFormUrlEncodedBody(ImmutableMap.of(
	            "username", "robindough",
	            "password", "notsecret"))
	    );
	    
	    assertEquals(400, status(result));
	    assertNull(session(result).get("username"));
	}
	
}
