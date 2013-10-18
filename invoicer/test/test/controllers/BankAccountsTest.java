
package test.controllers;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;


import models.BankAccount;
import models.Invoice;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

import play.mvc.*;
import play.test.FakeApplication;
import play.test.Helpers;
import play.test.TestServer;
import static play.test.Helpers.*;

import test.BaseTest;
import util.FileHandler;

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
	            	
	            	assertNull(BankAccount.find.where().eq("ACCOUNT_NUMBER", "1338-1339").findUnique());
	            	
	            	HttpClient httpclient = new DefaultHttpClient();
	        	    HttpPost httppost = new HttpPost("http://localhost:9001/accounts/upload");

	        	    FileBody jsonFile = new FileBody(new File(
	        	    		TEST_FILE_FOLDER + "bankAccountTest.json"), "application/json");

	        	    MultipartEntity reqEntity = new MultipartEntity();
	        	    reqEntity.addPart(FileHandler.FILE_PART_NAME, jsonFile);

	        	    httppost.setEntity(reqEntity);

	        	    HttpResponse response;
	        	    
	        	    try {
	        	        response = httpclient.execute(httppost);
	        	        HttpEntity resEntity = response.getEntity();
	        	        
	        	        assertNotNull(BankAccount.find.where().eq("ACCOUNT_NUMBER", "1338-1339").findUnique());
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
