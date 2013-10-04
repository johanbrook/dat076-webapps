/**
*	InvoiceTest.java
*
*	@author Johan Brook
*	@copyright (c) 2013 Johan Brook
*	@license MIT
*/

package test.models;

import static org.junit.Assert.*;

import java.util.Date;

import models.Invoice;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import test.BaseTest;

public class InvoiceTest extends BaseTest {
	
	private Invoice invoice;
	private Invoice newInvoice;

	@Before
	public void setUp() throws Exception {
		this.invoice = Invoice.find.where().eq("title", "Test invoice").findUnique();
		this.newInvoice = new Invoice();
	}

	@Test
	public void testRetrieveInvoice() {
		assertNotNull(this.invoice);
		assertNotNull(this.invoice.id);
	}
	
	@Test
	public void testCreateInvoice() {
		Invoice i = new Invoice();
		i.save();
		assertNotNull(i.id);
	}
	
	@Test
	public void testDeleteInvoice() {
		this.invoice.delete();
		assertNull(Invoice.find.where().eq("title", "Test invoice").findUnique());
	}
	
	@Test
	public void testInvoiceShouldHaveOwner() {
		assertNotNull(this.invoice.owner);
	}
	
	@Test
	public void testInvoiceShouldHaveClient() {
		assertNotNull(this.invoice.client);
	}
	
	@Test
	public void testInvoiceShouldAutoGenerateDate() {
		Invoice i = new Invoice();
		i.save();
		assertNotNull(i.invoiceDate);
	}
	
	@Test(expected = javax.persistence.PersistenceException.class)
	public void testInvoiceDueDateShouldBeAfterDate() {
		Invoice i = new Invoice();
		i.dueDate = new Date(System.currentTimeMillis() - 1000);
		
		i.save();
	}
	
	@Test
	public void testInvoiceShouldBePaidOnTime() {
		Invoice i = Invoice.find.where().eq("title", "Invoice was paid on time").findUnique();
		Invoice i2 = Invoice.find.where().eq("title", "Invoice was not paid on time").findUnique();
		
		assertNotNull(i);
		assertNotNull(i2);
		
		assertTrue(i.isPaid());
		assertTrue(i.wasPaidOnTime());
		
		assertTrue(i2.isPaid());
		assertFalse(i2.wasPaidOnTime());
		
		newInvoice.dueDate = DateTime.now().plusMonths(1).toDate();
		newInvoice.datePaid = DateTime.now().plusMonths(2).toDate();
		
		assertTrue(newInvoice.isPaid());
		assertFalse(newInvoice.wasPaidOnTime());
	}
	
	@Test
	public void testInvoiceSetPaid() {
		newInvoice.dueDate = DateTime.now().plusMonths(1).toDate();
		newInvoice.setPaid();
		
		assertNotNull(newInvoice.datePaid);
		
		assertTrue(newInvoice.isPaid());
		assertTrue(newInvoice.wasPaidOnTime());
	}

}
