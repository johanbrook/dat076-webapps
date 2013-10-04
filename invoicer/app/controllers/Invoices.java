/**
*	InvoiceController.java
*
*	@author Johan Brook
*	@copyright (c) 2013 Johan Brook
*	@license MIT
*/

package controllers;

import models.Client;
import java.util.List;

import com.avaje.ebean.ExpressionList;

import models.Invoice;
import models.User;
import play.Logger;
import play.data.Form;
import play.db.ebean.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

public class Invoices extends Controller {
	
	public static Form<Invoice> form = Form.form(Invoice.class);

	@Security.Authenticated(Secured.class)	
	public static Result index() {
		List<Invoice> list = Invoice.find.where().like
				("owner", String.valueOf(Session.getCurrentUser().id)).findList();
		
    	return ok(views.html.invoices.index.render(list, form));
    }
	
	public static Result show(Long id) {
		return ok(views.html.invoices.show.render(Invoice.find.byId(id)));
	}
	
	public static Result create() {
		Form<Invoice> filledForm = form.bindFromRequest();
		
		if(filledForm.hasErrors()) {
			flash("error", "There were errors in your form.");
			return badRequest(views.html.invoices.index.
					render(Invoice.find.all(), filledForm));
		}
		else {
			Invoice in = filledForm.get();
			
			in.owner = Session.getCurrentUser();
			in.client = Client.find.byId( Long.parseLong( Form.form().bindFromRequest().get("client.id") ) );
			in.save();
			
			flash("success", "Invoice was created!");
			return goHome();
		}
		
	}
	
	public static Result edit(Long id) {
		Invoice invoice = Invoice.find.byId(id);
		Form<Invoice> editForm = form.fill(invoice);
		
		return ok(views.html.invoices.edit.render(invoice, editForm));
	}
	
	public static Result update(Long id) {
		Invoice invoice = Invoice.find.byId(id);
		Form<Invoice> filledForm = form.bindFromRequest();
		
		if(filledForm.hasErrors()) {
			return badRequest(views.html.invoices.edit.render(invoice, filledForm));
		}
		
		invoice.title = filledForm.get().title;
		/*
		 * TODO: this doesn't work for now.
		 * 
		 *  See: http://stackoverflow.com/questions/19177077/how-to-properly-update-a-model-with-nested-models-in-play-framework-2-2-0
		 */
		
//		invoice.client.id = filledForm.get().client.id;
		
		invoice.title = filledForm.get().title;
		invoice.dueDate = filledForm.get().dueDate;
		
		invoice.setPaid( Form.form().bindFromRequest().get("ispaid") != null );
		
		invoice.update(id);

		flash("success", "Invoice " + invoice.title + " was updated!");
		
		return goHome();
	}
	
	private static Result goHome() {
		return redirect(controllers.routes.Invoices.index());
	}
}
