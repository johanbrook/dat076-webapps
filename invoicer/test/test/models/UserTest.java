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

import models.BankAccount;
import models.Invoice;
import models.User;

import org.junit.*;

import com.avaje.ebean.Ebean;

import org.mindrot.jbcrypt.BCrypt;

import play.Logger;
import play.libs.Yaml;
import play.test.WithApplication;
import test.BaseTest;
import static play.test.Helpers.*;

public class UserTest extends BaseTest {
	
	private User user;

	@Before
	public void setUp() throws Exception {
		 
		this.user = User.find.where().eq("username", "johndoe").findUnique();
	}
	
	@Test
	public void testRetrieveUser() {
		assertNotNull(this.user);
		assertEquals("johndoe", this.user.username);
	}
	
	@Test
	public void testCreateSimpleUser() {
		(new User("johandoe", "secret")).save();
		
		User johan = User.find.where().eq("username", "johandoe").findUnique();
		
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
		
		User dbJohan = User.find.where().eq("username", "johandoe").findUnique(); 
		
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
	public void testUserHasInvoice() {
		
		User userWithOneInvoice = new User("johnny", "password");
		BankAccount account = new BankAccount(userWithOneInvoice, "4444-4444", BankAccount.AccountType.BG);
		userWithOneInvoice.save();
		
		Invoice invoice = new Invoice();
		invoice.bankAccount = account;
		invoice.save();
		
		assertNotNull(account.owner);
		assertNotNull(invoice.bankAccount);
		
		assertEquals(userWithOneInvoice, invoice.getOwner());
		assertEquals(1, Invoice.getInvoicesOfUser(userWithOneInvoice.id).size());
	}
	
	
	@Test
	public void authenticateUser() {
		
		User user = new User("Robin", BCrypt.hashpw("password", BCrypt.gensalt()));
		user.save();
		
		User dbUser = User.authenticateUser("Robin", "password");
		User wrongDBUser = User.authenticateUser("Robin", "wrongpassword");
		
		// TODO: Better testing?
		
		assertNotNull(dbUser);
		assertNull(wrongDBUser);
		
	}
	
	@Test
	public void testUserCountries() {
		List<String> countries = User.getCountries();
		
		assertNotNull(countries);
		assertFalse(countries.isEmpty());
		assertTrue(countries.contains("United Kingdom"));
	}

}
