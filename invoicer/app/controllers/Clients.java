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

@Security.Authenticated(Secured.class)
public class Clients extends Application {

	public static Form<Client> newForm = Form.form(Client.class);

	public static Result index() {
		return ok(index.render(Client.find.all(), newForm));
	}

	public static Result create() {
		Form<Client> filledForm = newForm.bindFromRequest();

		if (filledForm.hasErrors()) {
			Result tmp = badRequest(index.render(Client.find.all(), filledForm));
			if (tmp == null) {
				Logger.info("asdf");
			}
			return tmp;
		} else {
			Client client = filledForm.get();
			client.save();
			return goHome();
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
		Client tmpClient = Client.find.byId(id);

		List<Invoice> list = Invoice.find.where().eq("client_id", id)
				.findList();

		if (list != null) {
			Ebean.delete(list);
		}
		if (tmpClient != null) {
			tmpClient.delete();
			flash("success", "The client: " + tmpClient.name
					+ " was successfully deleted.");
			return goHome();
		} else {
			return badRequest(views.html.clients.index.render(
					Client.find.all(), newForm));
		}

	}

	public static Result sendInvoices(Long id) {
		final String mailUsername = "andreasrolen93"; //TODO Change to users email
		final String mailPassword = "internet1<";
		
		Client client = Client.find.byId(id);
		List<Invoice> invoiceList = Invoice.find.where().eq("client_id", id)
				.findList();

		if (!client.email.isEmpty()) {
			if (invoiceList != null && client != null) {
				MailController.sendAllInvoices(mailUsername, mailPassword,
						client, invoiceList);
				flash("success", "A mail has been sent to: " + client.name);
				return goHome();
			} else {
				flash("fail", "The client doesn't exist or got no invoices");
				return badRequest(index.render(Client.find.all(), newForm));
			}
		} else {
			flash("fail", "The client: " + client.name + " don't got any email");
			return badRequest(index.render(Client.find.all(), newForm));
		}

	}

	private static Result goHome() {
		return redirect(controllers.routes.Clients.index());
	}
}
