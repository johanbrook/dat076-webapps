package test.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import test.BaseTest;
import models.BankAccount;
import models.BankAccount.AccountType;

public class BankAccountTest extends BaseTest{
	
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
	
	@Test
	public void testCreateBankAccount() {
		BankAccount ba = new BankAccount("112233-4", AccountType.PG, "Swedbank", "SE0004563215864902", "SWEDSESS");
		ba.save();
		
		assertNotNull(ba);
		assertNotNull(ba.id);
		assertEquals("112233-4", ba.accountNumber);
		assertEquals(AccountType.PG, ba.accountType);
		assertEquals("Swedbank", ba.bank);
		assertEquals("SE0004563215864902", ba.iban);
		assertEquals("SWEDSESS", ba.bic);
	}
	
	@Test(expected = javax.persistence.PersistenceException.class)
	public void testDeleteBankAccount() {
		this.bankAccount.delete();
		BankAccount newBA = BankAccount.find.where().eq("accountNumber", "1234-5678").findUnique();
		assertNull(newBA);
	}

}
