
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
				.withSession("userId", super.userId)
				.withFormUrlEncodedBody(ImmutableMap.of(
						"accountNumber", "1234-5555",
						"accountType", "BG",
						"iban", "SE0004563215864902",
						"bic", "SWEDSESS"
						))
				);

		assertEquals(303, status(create));
		assertEquals("/accounts", header("Location", create));

		BankAccount created = BankAccount.find.where().eq("accountNumber", "1234-5555").findUnique();

		assertNotNull(created);


	}

	@Test
	public void testUpdate() {
		BankAccount existing = BankAccount.find.all().get(0);

		Result update = callAction(
				controllers.routes.ref.BankAccounts.update(existing.id),
				fakeRequest()
				.withSession("userId", super.userId)
				.withFormUrlEncodedBody(ImmutableMap.of(
						"accountNumber", "1234-1",
						"accountType", "PG",
						"iban", "FI0004563215864902",
						"bic", "NDEAFIHH"
						))
				);
		BankAccount updated = BankAccount.find.where().eq("accountNumber", "1234-1").findUnique();
		assertNotNull(updated);
		assertEquals("1234-1", updated.accountNumber);

		assertEquals(303, status(update));
		assertEquals("/accounts/"+existing.id, header("Location", update));
	}

	@Test
	public void testDestroy() {
	}
}
