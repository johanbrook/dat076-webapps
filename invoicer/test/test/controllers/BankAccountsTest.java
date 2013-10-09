
package test.controllers;

import static org.junit.Assert.*;


import models.BankAccount;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

import play.mvc.*;
import static play.test.Helpers.*;
import play.test.*;

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
		
		
	}
	
	@Test
	public void testUpdate() {
		
	}
	
	@Test
	public void testDestroy() {
		
	}
}
