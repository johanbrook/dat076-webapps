/**
*	UserTest.java
*
*	@author Johan Brook
*	@copyright (c) 2013 Johan Brook
*	@license MIT
*/

package test.models;

import static org.junit.Assert.*;

import java.util.List;

import models.User;

import org.junit.*;

import com.avaje.ebean.Ebean;

import play.Logger;
import play.libs.Yaml;
import play.test.WithApplication;
import static play.test.Helpers.*;

public class UserTest extends WithApplication {

	@Before
	public void setUp() throws Exception {
		start(fakeApplication(inMemoryDatabase()));
		Ebean.save((List) Yaml.load("test-data.yml"));
	}
	

	@Test
	public void testRetrieveUser() {
		User john = User.find.where().eq("login", "johndoe").findUnique();
		assertNotNull(john);
		assertEquals("johndoe", john.login);
	}
	
	@Test
	public void testCreateUser() {
		User.create(new User("johandoe", "secret"));
		User johan = User.find.where().eq("login", "johandoe").findUnique();
		assertNotNull(johan);
		assertEquals("johandoe", johan.login);
	}
	
	@Test
	public void testDeleteUser() {
		Long id = User.find.where().eq("login", "johndoe").findUnique().id;
		User.delete(id);
		
		User john = User.find.where().eq("login", "johndoe").findUnique();
		assertNull(john);
	}
	
	@Test
	public void authenticateUser() {
		
	}

}
