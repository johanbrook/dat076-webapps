package test.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import models.BankAccount;

public class BankAccountTest extends BaseModelTest{
	
	private BankAccount bankAccount;
	
	@Before
	public void setUp() throws Exception {
		 
		this.bankAccount = BankAccount.find.where().eq("accountNumber", "1234-5678").findUnique();
	}
	
	@Test
	public void testRetrieveBankAccount() {
		assertNotNull(this.bankAccount);
		assertEquals("1234-5678", this.bankAccount.accountNumber);
	}

}
