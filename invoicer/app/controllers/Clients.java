/**
 *	ClientController.java
 *
 *	@author Andreas Rolén
 *	@copyright (c) 2013 Andreas Rolén
 *	@license MIT
 */

package controllers;

import java.util.List;

import com.avaje.ebean.Ebean;

import models.Client;
import models.Invoice;
import play.Logger;
import play.data.Form;
import play.mvc.Result;
import play.mvc.Security;
import views.html.clients.*;
import play.libs.Json;
import service.GMailService;
import service.Mailer;
import com.google.inject.Inject;

@Security.Authenticated(Secured.class)
public class Clients extends Application {

	public static Form<Client> newForm = Form.form(Client.class);
	
	@Inject
	private Mailer<Invoice> mailer;

	public static Result index() {
		return ok(index.render(Client.find.all(), newForm));
	}

	public static Result create() {
		Form<Client> filledForm = newForm.bindFromRequest();

		if (filledForm.hasErrors()) {
			return badRequest(index.render(Client.find.all(), filledForm));

		} else {
			final Client client = filledForm.get();
			client.save();
			
			return respondTo(new Responder() {

				@Override
				public Result json() {
					setLocationHeader(client);
					return created(Json.toJson(client));
				}

				@Override
				public Result html() {
					flash("success", "Client was created!");
					return goHome();
				}

				@Override
				public Result script() {
					return created(views.js.clients.create.render(client));
				}
			});

		}

	}

	public static Result show(Long id) {
		return ok(show.render(Client.find.byId(id)));
	}

	public static Result edit(Long id) {
		Client client = Client.find.byId(id);
		Form<Client> form = newForm.fill(client);

		return ok(edit.render(client, form));
	}

	public static Result update(Long id) {
		Client client = Client.find.byId(id);
		Form<Client> form = newForm.bindFromRequest();

		if (form.hasErrors()) {
			flash("fail", "The form has errors.");
			return badRequest(index.render(Client.find.all(), newForm));
		} else {
			client.name = form.get().name;
			client.address = form.get().address;
			client.country = form.get().country;
			client.postalCode = form.get().postalCode;
			client.orgNumber = form.get().orgNumber;
			client.contactPerson = form.get().contactPerson;
			client.email = form.get().email;

			client.update(id);

			flash("success", "The Client: " + client.name
					+ " was successfully updated");

			return goHome();
		}
	}

	public static Result destroy(Long id) {
		final Client tmpClient = Client.find.byId(id);
		final List<Invoice> list = Invoice.find.where().eq("client_id", id).findList();

		if (list != null) {
			Ebean.delete(list);
		}
		if (tmpClient != null) {
			tmpClient.delete();

			return respondTo(new Responder() {
				@Override
				public Result json() {
					return noContent();
				}

				@Override
				public Result html() {
					flash("success", "The client was deleted (along with "+list.size()+" invoices");
					return goHome();
				}

				@Override
				public Result script() {
					return ok(views.js.clients.destroy.render(tmpClient));
				}
			});
		} else {
			return badRequest(views.html.clients.index.render(
					Client.find.all(), newForm));
		}

	}

	public Result sendInvoices(Long id) {
		
		final Client client = Client.find.byId(id);
		final List<Invoice> invoiceList = Invoice.invoicesOfUser(Session.getCurrentUser().id)
				.where().eq("client_id", client.id).findList();
		
		if (client != null) {
			if (!invoiceList.isEmpty()) {
				final boolean didSend = mailer.sendMany(client, invoiceList, "You have new invoices", views.html.mails.invoiceList.ref());
				
				return respondTo(new Responder() {
					
					@Override
					public Result script() {
						if(didSend) {
							return ok(views.js.invoices.send_invoices.render(invoiceList, didSend));
						}
						else {
							return internalServerError(views.js.invoices.send_invoices.render(invoiceList, didSend));
						}
					}
					
					@Override
					public Result json() {
						return noContent();
					}
					
					@Override
					public Result html() {
						if(didSend) {
							flash("success", "A mail has been sent to: " + client.name);
							return goHome();
						}
						else {
							flash("fail", "An error occurred when trying to send the e-mail");
							return internalServerError(index.render(Client.find.all(), newForm));
						}
						
					}
				});
				
			} else {
				return respondTo(new Responder() {
					
					@Override
					public Result script() {
						return badRequest("No invoices for client");
					}
					
					@Override
					public Result json() {
						return badRequest("No invoices for client");
					}
					
					@Override
					public Result html() {
						flash("fail", "The client doesn't own any invoices");
						return badRequest(index.render(Client.find.all(), newForm));
					}
				});
			}
		}
		
		return badRequest(index.render(Client.find.all(), newForm));
	}

	private static Result goHome() {
		return redirect(controllers.routes.Clients.index());
	}
}
