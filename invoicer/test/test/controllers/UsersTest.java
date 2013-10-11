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
	    assertEquals("123456-7890", user.organizationNumber);
	    assertEquals("Sweden", user.country);
		
	}
	
	@Test
	public void testShow() {
		Result show = callAction(controllers.routes.ref.Users.show());
		assertEquals(200, status(show));
	}
	
	@Test
	public void testEdit() {
		Result edit = callAction(controllers.routes.ref.Users.edit());
		assertEquals(200, status(edit));
	}
	
	@Test
	public void testUpdate() {
		
		Result update = callAction(
				controllers.routes.ref.Users.create(),
				fakeRequest()
				.withSession("userId", "2")
				.withFormUrlEncodedBody(ImmutableMap.of(
						"username", "robindough",
						"name", "Robin Dough",
						"address", "Dough road 43",
						"postalCode", "41121 Dough",
						"organizationNumber", "123456-7890",
						"oldPassword", "secret",
						"newPassword", "not",
						"newRepeatedPassword", "not",
						"country", "Sweden"
				))
			);
		
		String userId = Helpers.session(update).get("userId");
	    User user = User.find.where().eq("username", "robindough").findUnique();
	    
	    assertEquals(303, status(update));
	    assertEquals("robindough", user.username);
	    assertTrue(BCrypt.checkpw("not", user.password));
	    assertEquals("Robin Dough", user.name);
	    assertEquals("Dough road 43", user.address);
	    assertEquals("41121 Dough", user.postalCode);
	    assertEquals("123456-7890", user.organizationNumber);
	    assertEquals("Sweden", user.country);
	    
	    Result update = callAction(
				controllers.routes.ref.Users.create(),
				fakeRequest()
				.withSession("userId", "2")
				.withFormUrlEncodedBody(ImmutableMap.of(
						"username", "robindough",
						"name", "Robin",
						"address", "Doughroad 43",
						"postalCode", "009911 Dough",
						"organizationNumber", "098765-4321",
						"oldPassword", "not",
						"newPassword", "secret",
						"newRepeatedPassword", "secret",
						"country", "Sweden"
				))
			);
	    
	    assertEquals("robindough", user.username);
	    assertTrue(BCrypt.checkpw("not", user.password));
	    assertEquals("Robin", user.name);
	    assertEquals("Doughroad 43", user.address);
	    assertEquals("009911 Dough", user.postalCode);
	    assertEquals("098765-4321", user.organizationNumber);
	    assertEquals("Sweden", user.country);
		
	}
	
	
}
