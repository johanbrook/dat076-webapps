/**
*	InvoicesTest.java
*
*	@author Johan Brook
*	@copyright (c) 2013 Johan Brook
*	@license MIT
*/

package test.controllers;

import static org.junit.Assert.*;

import javax.annotation.concurrent.Immutable;

import models.Invoice;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

import play.mvc.*;
import static play.test.Helpers.*;
import play.test.*;

import test.BaseTest;

public class InvoicesTest extends BaseTest {

	@Test
	public void testIndex() {
		Result index = callAction(controllers.routes.ref.Invoices.index());
		
		assertEquals(303, status(index));
	}
	
	@Test
	public void testShow() {
		Long existingId = Invoice.find.all().get(0).id;
		
		Result show = callAction(controllers.routes.ref.Invoices.show(existingId));
		assertEquals(200, status(show));
	}
	
	@Test
	public void testCreate() {
		
		Result create = callAction(
				controllers.routes.ref.Invoices.create(),
				fakeRequest().withFormUrlEncodedBody(ImmutableMap.of(
						"title", "Test",
						"invoiceDate", "2013-10-10",
						"dueDate", "2013-11-30",
						"client.id", "1"
				))
			);
		
		assertEquals(303, status(create));
		assertEquals("/invoices", header("Location", create));
		
		Invoice created = Invoice.find.where().eq("title", "Test").findUnique();
		
		assertNotNull(created);
		
	}
	
	@Test
	public void testUpdate() {
		
	}
	
	@Test
	public void testDestroy() {
		Long existingId = Invoice.find.all().get(0).id;
		Result destroy = callAction(controllers.routes.ref.Invoices.destroy(existingId));
		
		assertEquals(303, status(destroy));
		assertEquals("/invoices", header("Location", destroy));
		
		assertNull(Invoice.find.byId(existingId));
	}
}
