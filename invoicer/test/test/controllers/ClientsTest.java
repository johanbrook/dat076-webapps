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

	@Test
	public void testCreate() {

		Result result = callAction(
				controllers.routes.ref.Clients.create(),
				fakeRequest().withSession("userId", "1")
						.withFormUrlEncodedBody(
								(ImmutableMap.of("name", "testName",
										"orgNumber", "666666-6666"))));
		assertEquals(303, status(result));
		assertEquals("/clients", header("Location", result));

		Client newClient = Client.find.where().eq("name", "testName")
				.findUnique();

		assertNotNull(newClient);
	}

	@Test
	public void testUpdate() {
		Client oldClient = Client.find.all().get(0);
		String oldName = oldClient.name;
		String orgNumber = oldClient.orgNumber;

		Result result = callAction(
				controllers.routes.ref.Clients.update(oldClient.id),
				fakeRequest().withSession("userId", "1")
						.withFormUrlEncodedBody(
								(ImmutableMap.of("name", "newName",
										"orgNumber", "777777-7777"))));
		
		assertEquals(303, status(result));
		assertEquals("/clients", header("Location", result));
		
		Client newClient = Client.find.byId(oldClient.id);
		
		assertEquals("newName", newClient.name);
		assertEquals("777777-7777", newClient.orgNumber);
	}

	@Test
	public void testDestroy() {
		Long id = Client.find.where().eq("name", "client Without Invoice").findUnique().id;
		Result result = callAction(controllers.routes.ref.Clients.destroy(id),
				fakeRequest().withSession("userId", "1"));

		assertEquals(303, status(result));
		assertEquals("/clients", header("Location", result));

		assertNull(Client.find.byId(id));
	}

}
