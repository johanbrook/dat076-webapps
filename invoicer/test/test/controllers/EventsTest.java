/**
*	EventsTest.java
*
*	@author Johan Brook
*	@copyright (c) 2013 Johan Brook
*	@license MIT
*/

package test.controllers;

import static org.junit.Assert.*;

import models.Invoice;
import controllers.Events;

import org.fest.assertions.Assertions;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

import play.mvc.*;
import static play.test.Helpers.*;

import test.BaseTest;

public class EventsTest extends BaseTest {

	@Test
	public void testGetPaid() {

		Invoice unPaidInvoice = Invoice.find.where().isNull("datePaid").findList().get(0);

		// Make fakerequest to Invoices controller action
		Result paid = Events.getPaid();

		assertEquals(OK, status(paid));
		assertEquals("text/event-stream", contentType(paid));
	}
	
}

