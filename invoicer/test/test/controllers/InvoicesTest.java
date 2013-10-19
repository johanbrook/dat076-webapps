/**
*	InvoicesTest.java
*
*	@author Johan Brook
*	@copyright (c) 2013 Johan Brook
*	@license MIT
*/

package test.controllers;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import models.Invoice;
import models.User;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.fest.assertions.Assertions;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;

import controllers.Events;
import play.Logger;
import play.libs.Json;
import play.mvc.*;
import play.test.FakeApplication;
import play.test.Helpers;
import play.test.TestServer;
import static play.test.Helpers.*;

import akka.actor.Props;

import test.BaseTest;
import util.FileHandler;

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
				.withSession("userId", super.userId)
				.withFormUrlEncodedBody(ImmutableMap.of(
						"title", "Test",
						"invoiceDate", "2013-10-10",
						"dueDate", "2013-11-30",
						"client.id", "1",
						"bankAccount.id", "1"
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
				.withSession("userId", super.userId)
				.withFormUrlEncodedBody(ImmutableMap.of(
					"title", "New title",
					"dueDate", "2013-12-30",
					"client.id", "1",
					"bankAccount.id", "1"
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
			fakeRequest().withSession("userId", super.userId)
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
				.withSession("userId", super.userId)
				.withHeader(ACCEPT, "application/json")
		);
		
		
		assertEquals(OK, status(index));
		assertEquals("application/json", contentType(index));
		
		Invoice i = Invoice.find.all().get(0);
		
		Result show = callAction(
				controllers.routes.ref.Invoices.show(i.id),
				fakeRequest()
					.withSession("userId", super.userId)
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
			fakeRequest(PUT, controllers.routes.Invoices.toggleStarred(invoice.id).url())
				.withSession("userId", super.userId)
		);
		
		invoice.refresh();

		assertEquals(303, status(starred));
		assertEquals(!isStarred, invoice.starred);
	}

	@Test
	public void testStarredInvoices() {
		Result starred = callAction(
					controllers.routes.ref.Invoices.starred(),
					fakeRequest()
						.withSession("userId", super.userId)
				);

		assertEquals(OK, status(starred));
		assertEquals("/invoices/starred", header("Location", starred));
		Assertions.assertThat(contentAsString(starred)).contains("Test invoice");
	}

	@Test
	public void testEventSetPaid() {
		// Find unpaid invoice
		Invoice unPaidInvoice = Invoice.find.where().eq("title", "Test invoice").findUnique();
		
		assertNull(unPaidInvoice.datePaid);

		Result paid = callAction(
					controllers.routes.ref.Invoices.setPaid(unPaidInvoice.id),
					fakeRequest()
						.withSession("userId", super.userId)
				);

		assertEquals(OK, status(paid));
	}
	
	@Test
	public void testUpload() {
		
		//TODO: Session needs to be set somehow to auth the method call
		
		/*
		 * No support in play for MultipartFormData, therefore instead use
		 * apache DefaultHttpCLient to send request.
		 * 
		 * Need server to listen to port in order for this to work
		 */
		FakeApplication application = Helpers.fakeApplication(Helpers.inMemoryDatabase());
	    TestServer testServer = testServer(9001, application);
	
	    running(testServer, new Runnable() { 
	
	            @Override 
	            public void run() { 
	            	
	            	assertNull(Invoice.find.where().eq("title", "Json Invoice").findUnique());
	            	
	            	HttpClient httpclient = new DefaultHttpClient();
	        	    HttpPost httppost = new HttpPost("http://localhost:9001/invoices/upload");

	        	    FileBody jsonFile = new FileBody(new File(
	        	    		TEST_FILE_FOLDER + "invoiceTest.json"), "application/json");

	        	    MultipartEntity reqEntity = new MultipartEntity();
	        	    reqEntity.addPart(FileHandler.FILE_PART_NAME, jsonFile);

	        	    httppost.setEntity(reqEntity);

	        	    HttpResponse response;
	        	    
	        	    try {
	        	        response = httpclient.execute(httppost);
	        	        HttpEntity resEntity = response.getEntity();
	        	        
	        	        assertNotNull(Invoice.find.where().eq("title", "Json Invoice").findUnique());
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

