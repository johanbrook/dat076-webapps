/**
 *	ClientsTest.java
 *
 *	@author Andreas Rolén
 *	@copyright (c) 2013 Andreas Rolén
 *	@license MIT
 */

package test.controllers;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Client;
import models.Invoice;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Before;
import org.junit.Test;

import com.avaje.ebean.Ebean;
import com.google.common.collect.ImmutableMap;

import play.Logger;
import play.data.Form;
import play.libs.Yaml;
import play.mvc.Result;
import play.test.FakeApplication;
import play.test.Helpers;
import play.test.TestServer;
import static play.test.Helpers.*;
import test.BaseTest;
import util.FileHandler;

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
	
	@Test
	public void testUpload() {
		
		/*
		 * TODO: Pass along session with fake request when Play allows to add
		 * MultipartFormData to fakeRequest.
		/*
		
		/*
		 * No support in play for MultipartFormData, therefore instead use
		 * apache DefaultHttpCLient to send request.
		 *
		 * Observe! Need to remove @Security.Authenticated(Secured.class)
		 * from Clients controller to test this
		 * 
		 * Need server to listen to port in order for this to work
		 */
		FakeApplication application = Helpers.fakeApplication(Helpers.inMemoryDatabase()); 
	    TestServer testServer = testServer(9001, application); 
	
	    running(testServer, new Runnable() { 
	
	            @Override 
	            public void run() { 
	            	
	            	assertNull(Client.find.where().eq("NAME", "JSON Client").findUnique());
	            	
	            	HttpClient httpclient = new DefaultHttpClient();
	        	    HttpPost httppost = new HttpPost("http://localhost:9001/clients/upload");

	        	    FileBody jsonFile = new FileBody(new File(
	        	    		TEST_FILE_FOLDER + "clientTest.json"), "application/json");

	        	    MultipartEntity reqEntity = new MultipartEntity();
	        	    reqEntity.addPart(FileHandler.FILE_PART_NAME, jsonFile);

	        	    httppost.setEntity(reqEntity);

	        	    HttpResponse response;
	        	    
	        	    try {
	        	        response = httpclient.execute(httppost);
	        	        HttpEntity resEntity = response.getEntity();
	        	        
	        	        List<Client> list = Client.find.all();
	        	        
	        	        assertNotNull(Client.find.where().eq("NAME", "JSON Client").findUnique());
	        	        assertEquals(response.getStatusLine().getStatusCode(), 200);
	        	        
	        	    } catch (ClientProtocolException e) {
	        	        e.printStackTrace();
	        		} catch (IOException e) {
	        		        e.printStackTrace();
	        		}
	            } 
	    }); 
	    
	    Helpers.stop(testServer);
	}

}
