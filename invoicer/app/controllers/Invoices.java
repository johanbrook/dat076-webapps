/**
*	InvoiceController.java
*
*	@author Johan Brook
*	@copyright (c) 2013 Johan Brook
*	@license MIT
*/

package controllers;

import models.Invoice;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

public class Invoices extends Controller {
	
	public static Form<Invoice> newForm = Form.form(Invoice.class);

	@Security.Authenticated(Secured.class)	
	public static Result index() {
    	return ok(views.html.invoices.index.render(Invoice.all(), newForm));
    }
	
	public static Result create() {
		Form<Invoice> filledForm = newForm.bindFromRequest();
		
		if(filledForm.hasErrors()) {
			return badRequest(views.html.invoices.index.render(Invoice.all(), filledForm));
		}
		else {
			Invoice.create(filledForm.get());
			return redirect(controllers.routes.Invoices.index());
		}
		
	}
}
