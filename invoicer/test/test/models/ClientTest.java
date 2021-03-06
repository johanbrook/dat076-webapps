/**
*	ClientTest.java
*
*	@author Andreas Rolén
*	@copyright (c) 2013 Andreas Rolén
*	@license MIT
*/

package test.models;

import static org.junit.Assert.*;

import models.Client;

import org.junit.Before;
import org.junit.Test;

import test.BaseTest;


public class ClientTest extends BaseTest {

	private Client client;
	
	@Before
	public void setUp() throws Exception {
		client = Client.find.where().eq("orgNumber", "556479-5598").findUnique();
	}

	@Test
	public void testRetriveClient() {
		assertNotNull(this.client);
		assertEquals("556479-5598", this.client.orgNumber);
	}
	
	@Test
	public void testCreateClient() {
		Client carlsberg = new Client("carlsberg", "559823-5523");
		carlsberg.save();
		
		assertNotNull(carlsberg);
		assertNotNull(carlsberg.id);
		assertEquals("559823-5523", carlsberg.orgNumber);
	}
	
	@Test(expected = javax.persistence.PersistenceException.class)
	public void testDeleteClient() {
		this.client.delete();
		
		Client tmp = Client.find.where().eq("orgNumber", "556479-5598").findUnique();
		assertNull(tmp);
	}

}
