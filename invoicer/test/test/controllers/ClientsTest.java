package test.controllers;

import static org.junit.Assert.*;

import models.Client;

import org.junit.Before;
import org.junit.Test;

import play.data.Form;
import play.mvc.Result;
import play.test.Helpers;

public class ClientsTest extends Helpers {
	
	public static Form<Client> newForm;

	@Before
	public void setUp() throws Exception {
		newForm = Form.form(Client.class);
	}

	@Test
	public void testIndex() {
		Result result = callAction(controllers.routes.ref.Clients.index());
			    assertEquals(status(result), OK);
			    assertEquals(contentType(result), "text/html");
			    assertEquals(charset(result), "utf-8");
	}

	@Test
	public void testCreate() {
		fail("Not yet implemented");
	}

}
