/**
 * 
 */
package test.controllers;

import static play.test.Helpers.*;
import static org.junit.Assert.assertEquals;

import java.util.List;

import models.User;

import org.junit.Before;
import org.junit.Test;

import com.avaje.ebean.Ebean;

import controllers.Session;

import play.libs.Yaml;
import play.mvc.Result;
import test.BaseTest;

/**
 * @author Robin
 *
 */
public class SecuredTest extends BaseTest {
	
	/*
	 * Tests so that the Secured class lets the request continue
	 * (Secured test is automatically used for authentication)
	 */
	@Test
	public void testGetUsername() {
		
		// Calls a secured page with Session attribute set
	    Result result = callAction(
	        controllers.routes.ref.Users.show(),
	        fakeRequest().withSession("userId", super.userId).withHeader(ACCEPT, "text/html"));
	    
	    assertEquals(200, status(result));
	}
	
	/*
	 * Tests so that the Secured blocks the request and redirects to login page
	 * (Secured test is automatically used for authentication)
	 */
	@Test
	public void testOnUnauthorized() {
		
		// Calls the main page without Session attribute set
	    Result result = callAction(
	        controllers.routes.ref.Invoices.index(),
	        fakeRequest()
	    );
	    
	    assertEquals(303, status(result));
	    assertEquals("/login", header(LOCATION, result));
	}

}
