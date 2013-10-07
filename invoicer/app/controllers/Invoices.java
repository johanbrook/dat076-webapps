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
import org.joda.time.DateTime;

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

	private static List<Invoice> invoicesOfCurrentUser() {
		return Invoice.getInvoicesOfUser(Session.getCurrentUser().id);
	}

	private static List<Invoice> paidInvoicesOfCurrentUser() {
		return Invoice.invoicesOfUser(Session.getCurrentUser().id)
			.where().isNotNull("datePaid").findList();
	}

	private static List<Invoice> overdueInvoicesOfCurrentUser() {
		return Invoice.getOverdueInvoicesOfUser(Session.getCurrentUser().id);
	}

	@Security.Authenticated(Secured.class)	
	public static Result index() {
    	return ok(views.html.invoices.index.render(invoicesOfCurrentUser(), paidInvoicesOfCurrentUser(), overdueInvoicesOfCurrentUser(), form));
    }
	
	@Security.Authenticated(Secured.class)
	public static Result show(Long id) {
		return ok(views.html.invoices.show.render(Invoice.find.byId(id)));
	}
	
	@Security.Authenticated(Secured.class)
	public static Result create() {
		Form<Invoice> filledForm = form.bindFromRequest();
		
		if(filledForm.hasErrors()) {
			flash("error", "There were errors in your form.");
			return badRequest(views.html.invoices.index.
					render(invoicesOfCurrentUser(), paidInvoicesOfCurrentUser(), overdueInvoicesOfCurrentUser(), filledForm));
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
	
	@Security.Authenticated(Secured.class)
	public static Result edit(Long id) {
		Invoice invoice = Invoice.find.byId(id);
		Form<Invoice> editForm = form.fill(invoice);
		
		return ok(views.html.invoices.edit.render(invoice, editForm));
	}
	
	@Security.Authenticated(Secured.class)
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
	
	
	@Security.Authenticated(Secured.class)
	public static Result destroy(Long id) {
		Invoice invoice = Invoice.find.byId(id);
		
		if(invoice != null) {
			invoice.delete();
			flash("success", "The invoice was deleted.");
			return goHome();
		}
		else {
			return badRequest(views.html.invoices.index.render(invoicesOfCurrentUser(), paidInvoicesOfCurrentUser(), overdueInvoicesOfCurrentUser(), form));
		}
	}
	
	
	private static Result goHome() {
		return redirect(controllers.routes.Invoices.index());
	}
}
