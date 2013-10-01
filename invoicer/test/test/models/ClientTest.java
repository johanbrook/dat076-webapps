package test.models;

import static org.junit.Assert.*;

import models.Client;

import org.junit.Before;
import org.junit.Test;

public class ClientTest extends BaseModelTest {

	private Client client;
	
	@Before
	public void setUp() throws Exception {
		client = this.client.find.where().eq("orgNumber", "556479-5598").findUnique();
	}

	@Test
	public void testRetriveClient() {
		assertNotNull(this.client);
		assertEquals("556479-5598", this.client.orgNumber);
	}
	
	@Test
	public void testCreateClient() {
		Client carlsberg = new Client("carlsberg", "559823-5523");
		
		assertNotNull(carlsberg);
		assertNotNull(carlsberg.id);
		assertEquals("559823-5523", carlsberg.orgNumber);
	}
	
	@Test
	public void testDeleteClient() {
		this.client.delete();
		
		Client tmp = Client.find.where().eq("orgNumber", "556479-5598").findUnique();
		assertNull(tmp);
	}

}
