/**
*	ClientController.java
*
*	@author Andreas Rolén
*	@copyright (c) 2013 Andreas Rolén
*	@license MIT
*/

package controllers;

import models.Client;
import models.Invoice;
import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

@Security.Authenticated(Secured.class)
public class Clients extends Controller {

	public static Form<Client> newForm = Form.form(Client.class);
	
	public static Result index() {
		return ok(views.html.clients.index.render(Client.find.all(), newForm));
	}

	public static Result create() {
		Form<Client> filledForm = newForm.bindFromRequest();
		
		if(filledForm.hasErrors()) {
			return badRequest(views.html.clients.index.render(Client.find.all(), filledForm));
		}
		else {
			Client client = filledForm.get();
			client.save();
			return goHome();
		}
		
	}
	
	public static Result show(Long id) {
		return ok(views.html.clients.show.render(Client.find.byId(id)));
	}
	
	public static Result edit(Long id) {
		Client client = Client.find.byId(id);
		Form<Client> form = newForm.fill(client);
		
		return ok(views.html.clients.edit.render(client, form));
	}
	
	public static Result update(Long id) {
		Client client = Client.find.byId(id);
		Form<Client> form = newForm.bindFromRequest();
		
		if(form.hasErrors()) {
			flash("fail", "The form has errors.");
			return badRequest(views.html.clients.index.render(Client.find.all(), form));
		} else {
			client.name = form.get().name;
			client.address = form.get().address;
			client.country = form.get().country;
			client.postalCode = form.get().postalCode;
			client.orgNumber = form.get().orgNumber;
			client.contactPerson = form.get().contactPerson;
			
			client.update(id);
			
			flash("success", "The Client: " + client.name + " was successfully updated");
			
			return goHome();
		}
	}
	
	public static Result destroy(Long id) {
		Client tmpClient = Client.find.byId(id);
		
		if(tmpClient != null) {
			tmpClient.delete();
			flash("success", "The Client was successfully deleted.");
			return goHome();
		} else {
			return badRequest(views.html.clients.index.render(Client.find.all(), newForm));
		}
	}
	
	private static Result goHome() {
		return redirect(controllers.routes.Clients.index());
	}
}
