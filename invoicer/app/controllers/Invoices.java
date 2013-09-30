/**
*	InvoiceController.java
*
*	@author Johan Brook
*	@copyright (c) 2013 Johan Brook
*	@license MIT
*/

package controllers;

import models.Invoice;
import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.helper.form;

public class Invoices extends Controller {
	
	public static Form<Invoice> newForm = Form.form(Invoice.class);

	public static Result index() {
    	return ok(views.html.invoices.index.render(Invoice.find.all(), newForm));
    }
	
	public static Result create() {
		Form<Invoice> filledForm = newForm.bindFromRequest();
		
		if(filledForm.hasErrors()) {
			return badRequest(views.html.invoices.index.render(Invoice.find.all(), filledForm));
		}
		else {
			Invoice in = filledForm.get();
			//TODO @Robin: replace with currentUser later when auth works
			in.owner = User.find.all().get(0);
			in.save();
			return redirect(controllers.routes.Invoices.index());
		}
		
	}
}
