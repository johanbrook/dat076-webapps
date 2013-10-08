/**
*	InvoiceController.java
*
*	@author Johan Brook
*	@copyright (c) 2013 Johan Brook
*	@license MIT
*/

package controllers;

import views.html.invoices.*;
import models.Client;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import models.Invoice;
import play.api.templates.Html;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;

@Security.Authenticated(Secured.class)
public class Invoices extends Application {
	
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

	public static Result index() {
		
    	return respondTo(new Responder() {
			
			@Override
			public Result json() {
				return ok(Json.toJson(Invoice.find.all()));
			}
			
			@Override
			public Result html() {
				return ok(index.render(invoicesOfCurrentUser(), paidInvoicesOfCurrentUser(), overdueInvoicesOfCurrentUser(), form));
			}
			@Override
			public Result script() { return noContent();}
		});
    }
	
	public static Result show(Long id) {
		return respondTo(Invoice.find.byId(id), show.ref(), null);
	}
	
	public static Result create() {
		final Form<Invoice> filledForm = form.bindFromRequest();
		
		if(filledForm.hasErrors()) {
			
			return respondTo(new Responder() {
				
				@Override
				public Result json() {
					return badRequest();
				}
				
				@Override
				public Result html() {
					flash("error", "There were errors in your form.");
					return badRequest(index.
							render(invoicesOfCurrentUser(), paidInvoicesOfCurrentUser(), overdueInvoicesOfCurrentUser(), filledForm));
				}
				@Override
				public Result script() {return badRequest();}
			});
		}
		else {
			final Invoice in = filledForm.get();
			
			in.owner = Session.getCurrentUser();
			in.client = Client.find.byId( Long.parseLong( Form.form().bindFromRequest().get("client.id") ) );
			in.setPaid(Form.form().bindFromRequest().get("ispaid") != null);

			in.save();
			
			return respondTo(new Responder() {
				
				@Override
				public Result json() {
					setLocationHeader(in);
					return created(Json.toJson(in));
				}
				
				@Override
				public Result html() {
					flash("success", "Invoice was created!");
					return goHome();
				}
				@Override
				public Result script() {
					return created(views.js.invoices.create.render(in));
				}
			});
		}
		
	}
	
	public static Result edit(Long id) {
		Invoice invoice = Invoice.find.byId(id);
		Form<Invoice> editForm = form.fill(invoice);
		
		return ok(edit.render(invoice, editForm));
	}
	
	public static Result update(Long id) {
		Invoice invoice = Invoice.find.byId(id);
		Form<Invoice> filledForm = form.bindFromRequest();
		
		if(filledForm.hasErrors()) {
			return badRequest(views.html.invoices.edit.render(invoice, filledForm));
		}
		
		/*
		 * TODO: this doesn't work for now.
		 * 
		 *  See: http://stackoverflow.com/questions/19177077/how-to-properly-update-a-model-with-nested-models-in-play-framework-2-2-0
		 */
		
//		invoice.client.id = filledForm.get().client.id;
		
		if(filledForm.get().title != null)	
			invoice.title = filledForm.get().title;
		
		if(filledForm.get().dueDate != null)
			invoice.dueDate = filledForm.get().dueDate;
		
		if(Form.form().bindFromRequest().get("ispaid") != null)
			invoice.setPaid( Form.form().bindFromRequest().get("ispaid") != null );
		
		invoice.update(id);

		flash("success", "Invoice " + invoice.title + " was updated!");
		
		return redirect(controllers.routes.Invoices.show(id));
	}
	
	public static Result destroy(Long id) {
		final Invoice invoice = Invoice.find.byId(id);
		
		if(invoice != null) {
			invoice.delete();

			return respondTo(new Responder() {
				@Override
				public Result json() {
					return noContent();
				}
				
				@Override
				public Result html() {
					flash("success", "The invoice was deleted");
					return goHome();
				}
				@Override
				public Result script() {
					return ok(views.js.invoices.destroy.render(invoice));
				}
			});
		}
		else {
			return notFound(show.render(invoice));
		}
	}
	
	
	private static Result goHome() {
		return redirect(controllers.routes.Invoices.index());
	}
}
