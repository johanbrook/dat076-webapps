package test.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.Test;
import models.BankAccount;
import models.BankAccount.AccountType;

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
	
	@Test
	public void testCreateBankAccount() {
		BankAccount ba = new BankAccount("112233-4", AccountType.PG);
		ba.save();
		
		assertNotNull(ba);
		assertNotNull(ba.id);
		assertEquals(AccountType.PG, ba.accountType);
	}
	
	@Test
	public void testDeleteBankAccount() {
		this.bankAccount.delete();
		BankAccount newBA = BankAccount.find.where().eq("accountNumber", "1234-5678").findUnique();
		assertNull(newBA);
	}

}
