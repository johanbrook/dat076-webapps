/**
 * 
 */
package test.controllers;

import static org.junit.Assert.*;

import models.User;

import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;

import play.Logger;
import play.mvc.*;
import play.libs.*;
import play.test.*;
import test.BaseTest;
import static play.test.Helpers.*;

import org.mindrot.jbcrypt.BCrypt;

import com.avaje.ebean.Ebean;
import com.google.common.collect.ImmutableMap;

import controllers.Session;


public class UsersTest extends BaseTest {
	
	@Test
	public void testIndex() {
		Result index = callAction(controllers.routes.ref.Users.index());
		assertEquals(200, status(index));
	}
	
	@Test
	public void testCreate() {
		
		Map<String,String> formData = new HashMap<String, String>();
		formData.put("username", "robinhodough");
		formData.put("password", "secret");
		formData.put("repeatPassword", "secret");
		formData.put("name", "Robin Dough");
		formData.put("address", "Dough road 43");
		formData.put("postalCode", "41121 Dough");
		formData.put("organizationNumber", "999999-9999");
		formData.put("country", "Sweden");
		formData.put("accept", "true");
		
		Result result = callAction( controllers.routes.ref.
				Users.create(), fakeRequest().withFormUrlEncodedBody(		
		        			formData));
		
		String userId = Helpers.session(result).get("userId");
	    User user = User.find.where().eq("username", "robinhodough").findUnique();
	    
	    assertEquals(303, status(result));
	    assertEquals("robinhodough", user.username);
	    assertTrue(BCrypt.checkpw("secret", user.password));
	    assertEquals("Robin Dough", user.name);
	    assertEquals("Dough road 43", user.address);
	    assertEquals("41121 Dough", user.postalCode);
	    assertEquals("999999-9999", user.organizationNumber);
	    assertEquals("Sweden", user.country);
		
	}
	
	
}