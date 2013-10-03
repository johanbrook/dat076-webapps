/**
 * 
 */
package test.controllers;

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
	public void testLoginSuccess() {
		
	    Result result = callAction(				// Call the following action
		        controllers.routes.ref.			// Get reference to action ('Reverse router')
		        Session.authenticate(),		// The action to get a reference to
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
	public void testLoginFailure() {
		
	    Result result = callAction(
	        controllers.routes.ref.Session.authenticate(),
	        fakeRequest().withFormUrlEncodedBody(ImmutableMap.of(
	            "username", "robindough",
	            "password", "notsecret"))
	    );
	    
	    assertEquals(400, status(result));
	    assertNull(session(result).get("username"));
	}
	
	@Test
	public void testSessionAuthenticationSuccess() {
	    Result result = callAction(
	        controllers.routes.ref.Application.index(),
	        fakeRequest().withSession("username", "robindough")
	    );
	    assertEquals(303, status(result));
	    assertEquals("/invoices", header("Location", result));
	}
	
	
	
	@Test
	public void testSessionAuthenticationFailure() {
	    Result result = callAction(
	        controllers.routes.ref.Application.index(),
	        fakeRequest()
	    );
	    
	    assertEquals(303, status(result));
	    assertEquals("/login", header("Location", result));
	}
	
	
}