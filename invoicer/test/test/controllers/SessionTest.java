/**
 * 
 */
package test.controllers;

import static org.junit.Assert.*;

import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;

import play.Logger;
import play.mvc.*;
import play.libs.*;
import play.test.*;
import test.BaseTest;
import static play.test.Helpers.*;
import com.avaje.ebean.Ebean;
import com.google.common.collect.ImmutableMap;


public class SessionTest extends BaseTest {
	
	@Test
	public void testCreateSessionSuccess() {
		
	    Result result = callAction(				// Call the following action
		        controllers.routes.ref.			// Get reference to action ('Reverse router')
		        Session.createSession(),		// The action to get a reference to
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
	public void testCreateSessionFailure() {
		
	    Result result = callAction(
	        controllers.routes.ref.Session.createSession(),
	        fakeRequest().withFormUrlEncodedBody(ImmutableMap.of(
	            "username", "robindough",
	            "password", "notsecret"))
	    );
	    
	    assertEquals(400, status(result));
	    assertNull(session(result).get("username"));
	}
	
}
