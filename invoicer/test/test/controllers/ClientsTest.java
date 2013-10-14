/**
 *	ClientsTest.java
 *
 *	@author Andreas Rolén
 *	@copyright (c) 2013 Andreas Rolén
 *	@license MIT
 */

package test.controllers;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Client;
import models.Invoice;

import org.junit.Before;
import org.junit.Test;

import com.avaje.ebean.Ebean;
import com.google.common.collect.ImmutableMap;

import play.data.Form;
import play.libs.Yaml;
import play.mvc.Result;
import static play.test.Helpers.*;
import test.BaseTest;

public class ClientsTest extends BaseTest {

	public static Form<Client> newForm;

	@Test
	public void testIndex() {
		Result index = callAction(controllers.routes.ref.Clients.index());

		assertEquals(303, status(index));
	}

	/**
	 * Test for creating Clients
	 */
	@Test
	public void testCreate() {
		// Testing to add a correct Client
		Result result = callAction(
				controllers.routes.ref.Clients.create(),
				fakeRequest().withSession("userId", super.userId)
						.withFormUrlEncodedBody(
								(ImmutableMap.of("name", "testName",
										"orgNumber", "666666-6666")))
						.withHeader(ACCEPT, "text/html"));
		
		// Testing the validation of the orgNumber
		Result badResult = callAction(
				controllers.routes.ref.Clients.create(),
				fakeRequest().withSession("userId", super.userId)
						.withFormUrlEncodedBody(
								(ImmutableMap.of("name", "badInput",
										"orgNumber", "444-444")))
						.withHeader(ACCEPT, "text/html"));
		
		assertEquals(BAD_REQUEST, status(badResult));

		assertEquals(303, status(result));
		assertEquals("/clients", header("Location", result));

		Client newClient = Client.find.where().eq("name", "testName")
				.findUnique();

		assertNotNull(newClient);
	}

	/**
	 * Testing to update an existing Client
	 */
	@Test
	public void testUpdate() {
		Client oldClient = Client.find.all().get(0);
		String oldName = oldClient.name;
		String orgNumber = oldClient.orgNumber;

		Result result = callAction(
				controllers.routes.ref.Clients.update(oldClient.id),
				fakeRequest().withSession("userId", super.userId)
						.withFormUrlEncodedBody(
								(ImmutableMap.of("name", "newName",
										"orgNumber", "777777-7777")))
						.withHeader(ACCEPT, "text/html"));

		assertEquals(303, status(result));
		assertEquals("/clients", header("Location", result));

		Client newClient = Client.find.byId(oldClient.id);

		assertEquals("newName", newClient.name);
		assertEquals("777777-7777", newClient.orgNumber);
	}

	/**
	 * Testing to remove a Client with and without invoices
	 */
	@Test
	public void testDestroy() {
		// Testing to remove a Client
		Long id = Client.find.where().eq("name", "client Without Invoice")
				.findUnique().id;
		Result resultWithoutInvoice = callAction(
				controllers.routes.ref.Clients.destroy(id), fakeRequest()
						.withSession("userId", super.userId)
						.withHeader(ACCEPT, "text/html"));

		// Testing to remove a Client with invoices (The invoices should also be deleted)
		Long invoiceId = Invoice.find.where()
				.eq("title", "Test client destroy").findUnique().id;
		Result resultWithInvoice = callAction(
				controllers.routes.ref.Invoices.destroy(invoiceId),
				fakeRequest().withSession("userId", super.userId)
					.withHeader(ACCEPT, "text/html"));

		Invoice tmpInvoice = Invoice.find.where()
				.eq("title", "Test client destroy").findUnique();

		assertEquals(303, status(resultWithoutInvoice));
		assertEquals("/clients", header("Location", resultWithoutInvoice));

		assertNull(Client.find.byId(id));

		assertNull(Client.find.byId(invoiceId));
		assertNull(tmpInvoice);
	}

}
