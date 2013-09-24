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

import models.Invoice;
import models.User;

import org.junit.*;

import com.avaje.ebean.Ebean;

import play.Logger;
import play.libs.Yaml;
import play.test.WithApplication;
import static play.test.Helpers.*;

public class UserTest extends WithApplication {
	
	private User user;

	@Before
	public void setUp() throws Exception {
		start(fakeApplication(inMemoryDatabase()));
		Ebean.save((List) Yaml.load("test-data.yml"));
		
		this.user = User.find.where().eq("login", "johndoe").findUnique();
	}
	

	@Test
	public void testRetrieveUser() {
		assertNotNull(this.user);
		assertEquals("johndoe", this.user.login);
	}
	
	@Test
	public void testCreateUser() {
		User johan = User.create(new User("johandoe", "secret"));
		
		assertNotNull(johan);
		assertNotNull(johan.id);
		assertEquals("johandoe", johan.login);
	}
	
	@Test
	public void testDeleteUser() {
		this.user.delete();
		
		User john = User.find.where().eq("login", "johndoe").findUnique();
		assertNull(john);
	}
	
	@Test
	public void testUserHasInvoice() {
		assertNotNull(this.user.invoices);
		assertEquals(1, this.user.invoices.size());
		assertEquals("Test invoice", this.user.invoices.get(0).title);
	}
	
	/**
	 * Deleting a User should also delete the User's Invoices.
	 */
	@Test
	public void testDeleteUserShouldDeleteInvoices() {
		this.user.delete();
		
		assertNull( Invoice.find.where().eq("title", "Test invoice").findUnique() );
		assertNull( Invoice.find.where().eq("owner_id", this.user.id).findUnique() );
		
	}
	
	@Test
	public void authenticateUser() {
		
	}

}
