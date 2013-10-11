
package test.controllers;

import static org.junit.Assert.*;


import models.BankAccount;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

import play.mvc.*;
import static play.test.Helpers.*;

import test.BaseTest;

public class BankAccountsTest extends BaseTest {

	
	@Test
	public void testIndex() {
		Result index = callAction(controllers.routes.ref.BankAccounts.index());
		
		assertEquals(303, status(index));
	}
	
	@Test
	public void testShow() {
		Long existingId = BankAccount.find.all().get(0).id;
		
		Result show = callAction(controllers.routes.ref.BankAccounts.show(existingId));
		
		assertEquals(303, status(show));
	}
	
	@Test
	public void testCreate() {
		Result create = callAction(
				controllers.routes.ref.BankAccounts.create(),
				fakeRequest()
				.withSession("userId", "1")
				.withFormUrlEncodedBody(ImmutableMap.of(
						"accountNumber", "1234-5678",
						"accountType", "PG"
				))
			);
		
		assertEquals(303, status(create));
		assertEquals("/bankaccounts", header("Location", create));
		
		BankAccount created = BankAccount.find.where().eq("accountNumber", "1234-5678").findUnique();
		
		assertNotNull(created);
		
	}
	
	@Test
	public void testUpdate() {
		
	}
	
	@Test
	public void testDestroy() {
		
	}
}
