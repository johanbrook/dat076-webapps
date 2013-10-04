package test.controllers;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Client;

import org.junit.Before;
import org.junit.Test;

import com.avaje.ebean.Ebean;

import play.data.Form;
import play.libs.Yaml;
import play.mvc.Result;
import play.test.Helpers;


public class ClientsTest extends Helpers {
	
	public static Form<Client> newForm;

	@Before
	public void setUp() throws Exception {
		start(fakeApplication(inMemoryDatabase(), fakeGlobal()));
		Ebean.save((List) Yaml.load("test-data.yml"));
	}

	@Test
	public void testIndex() {
		Map<String,String> data = new HashMap<String,String>();
		data.put("name", "testName");
		data.put("orgNumber", "666666-6666");
		
		Result result = callAction(controllers.routes.ref.Clients.index(), fakeRequest().withFormUrlEncodedBody(data));
			    assertEquals(status(result), OK);
			    assertEquals(contentType(result), "text/html");
			    assertEquals(charset(result), "utf-8");
	}

	@Test
	public void testCreate() {
		fail("Not yet implemented");
	}

}
