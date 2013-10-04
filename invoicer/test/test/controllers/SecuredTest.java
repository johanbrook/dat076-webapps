/**
 * 
 */
package test.controllers;

import static org.junit.Assert.assertEquals;
import static play.test.Helpers.callAction;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.fakeGlobal;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.header;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.start;
import static play.test.Helpers.status;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.avaje.ebean.Ebean;

import play.libs.Yaml;
import play.mvc.Result;

/**
 * @author Robin
 *
 */
public class SecuredTest {
	
	@Before
	public void createCleanDb() {
		start(fakeApplication(inMemoryDatabase(), fakeGlobal()));
		Ebean.save((List) Yaml.load("test-data.yml"));
	}
	
	/*
	 * Tests so that the Secured class lets the request continue
	 * (Secured test is automatically used for authentication)
	 */
	@Test
	public void testGetUsername() {
		
		// Calls the main page with Session attribute set
	    Result result = callAction(
	        controllers.routes.ref.Application.index(),
	        fakeRequest().withSession("username", "robindough")
	    );
	    assertEquals(303, status(result));
	    assertEquals("/invoices", header("Location", result));
	}
	
	/*
	 * Tests so that the Secured blocks the request and redirects to login page
	 * (Secured test is automatically used for authentication)
	 */
	@Test
	public void testOnUnauthorized() {
		
		// Calls the main page without Session attribute set
	    Result result = callAction(
	        controllers.routes.ref.Application.index(),
	        fakeRequest()
	    );
	    
	    assertEquals(303, status(result));
	    assertEquals("/login", header("Location", result));
	}

}
