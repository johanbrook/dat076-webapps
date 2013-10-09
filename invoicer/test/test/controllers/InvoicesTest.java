/**
*	InvoicesTest.java
*
*	@author Johan Brook
*	@copyright (c) 2013 Johan Brook
*	@license MIT
*/

package test.controllers;

import static org.junit.Assert.*;

import models.Invoice;

import org.fest.assertions.Assertions;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import play.mvc.*;
import static play.test.Helpers.*;

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
		
		assertEquals(303, status(show));
	}
	
	@Test
	public void testCreate() {
		
		Result create = callAction(
				controllers.routes.ref.Invoices.create(),
				fakeRequest()
				.withSession("userId", "1")
				.withFormUrlEncodedBody(ImmutableMap.of(
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
		Invoice existing = Invoice.find.all().get(0);
		
		Result update = callAction(
			controllers.routes.ref.Invoices.update(existing.id),
			fakeRequest()
				.withSession("userId", "1")
				.withFormUrlEncodedBody(ImmutableMap.of(
					"title", "New title",
					"dueDate", "2013-12-30",
					"client.id", "1"
				))
		);
		Invoice updated = Invoice.find.where().eq("title", "New title").findUnique();
		assertNotNull(updated);
		assertEquals("New title", updated.title);
		
		assertEquals(303, status(update));
		assertEquals("/invoices/"+existing.id, header("Location", update));
	}
	
	@Test
	public void testDestroy() {
		Long existingId = Invoice.find.all().get(0).id;
		Result destroy = callAction(
			controllers.routes.ref.Invoices.destroy(existingId),
			fakeRequest().withSession("userId", "1")
		);
		
		assertEquals(303, status(destroy));
		assertEquals("/invoices", header("Location", destroy));
		
		assertNull(Invoice.find.byId(existingId));
	}
	
	@Test
	public void testRequestJSON() {
		Result index = callAction(
			controllers.routes.ref.Invoices.index(),
			fakeRequest()
				.withSession("userId", "1")
				.withHeader(ACCEPT, "application/json")
		);
		
		
		assertEquals(OK, status(index));
		assertEquals("application/json", contentType(index));
		
		Invoice i = Invoice.find.all().get(0);
		
		Result show = callAction(
				controllers.routes.ref.Invoices.show(i.id),
				fakeRequest()
					.withSession("userId", "1")
					.withHeader(ACCEPT, "application/json")
			);
		
		assertEquals(OK, status(show));
		assertEquals("application/json", contentType(show));
		Assertions.assertThat(contentAsString(show)).contains("\"title\":\""+i.title+"\"");
	}

	@Test
	public void testToggleStarred() {
		Invoice invoice = Invoice.find.all().get(0);
		boolean isStarred = invoice.starred;

		Result starred = callAction(
			controllers.routes.ref.Invoices.toggleStarred(invoice.id),
			fakeRequest()
				.withSession("userId", "1")
				.withHeader(ACCEPT, "application/script")
		);

		assertEquals(OK, status(starred));
		assertEquals(!isStarred, invoice.starred);
	}
}

