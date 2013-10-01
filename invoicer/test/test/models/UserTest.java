/**
*	UserTest.java
*
*	@author Johan Brook
*	@copyright (c) 2013 Johan Brook
*	@license MIT
*/

package test.models;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import models.Invoice;
import models.User;

import org.junit.*;

import com.avaje.ebean.Ebean;

import play.Logger;
import play.libs.Yaml;
import play.test.WithApplication;
import static play.test.Helpers.*;

public class UserTest extends BaseModelTest {
	
	private User user;

	@Before
	public void setUp() throws Exception {
		 
		this.user = User.find.where().eq("login", "johndoe").findUnique();
	}
	

	@Test
	public void testRetrieveUser() {
		assertNotNull(this.user);
		assertEquals("johndoe", this.user.username);
	}
	
	@Test
	public void testCreateSimpleUser() {
		(new User("johandoe", "secret")).save();
		
		User johan = User.find.where().eq("login", "johandoe").findUnique();
		
		assertNotNull(johan);
		assertNotNull(johan.id);
		assertEquals("johandoe", johan.username);
	}
	
	@Test
	public void testCreateUser() {
		User johan = new User("johandoe", "secret");
		johan.name = "Johan Doe";
		johan.address = "Doe Street 43";
		johan.postalCode = "999999";
		johan.country = "Sweden";
		johan.organizationNumber = "999999-9999";
		johan.save();
		
		User dbJohan = User.find.where().eq("login", "johandoe").findUnique(); 
		
		assertNotNull(dbJohan);
		assertNotNull(dbJohan.id);
		assertEquals(dbJohan.name, "Johan Doe");
		assertEquals(dbJohan.username, "johandoe");
		assertEquals(dbJohan.address, "Doe Street 43");
		assertEquals(dbJohan.postalCode, "999999");
		assertEquals(dbJohan.country, "Sweden");
		assertEquals(dbJohan.organizationNumber, "999999-9999");
		
	}
	
	@Test
	public void testDeleteUser() {
		this.user.delete();
		
		User john = User.find.where().eq("login", "johndoe").findUnique();
		assertNull(john);
	}
	
	@Test
	public void testUserHasInvoice() {
		User userWithOneInvoice = new User("johnny", "password");
		userWithOneInvoice.save();
		Invoice i = Invoice.create(new Invoice());
		userWithOneInvoice.invoices.add(i);
		userWithOneInvoice.save();
		
		assertNotNull(userWithOneInvoice.invoices);
		assertNotNull(i.owner);
		assertEquals(userWithOneInvoice, i.owner);
		assertEquals(1, userWithOneInvoice.invoices.size());
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
