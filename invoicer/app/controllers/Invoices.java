/**
*	InvoiceController.java
*
*	@author Johan Brook
*	@copyright (c) 2013 Johan Brook
*	@license MIT
*/

package controllers;

import models.Invoice;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

public class Invoices extends Controller {

	public static Result index() {
    	return ok(index.render(Invoice.all()));
    }
}
