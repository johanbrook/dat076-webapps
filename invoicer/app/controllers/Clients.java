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

public class Clients extends Controller {

	public static Form<Client> newForm = Form.form(Client.class);
	
	public static Result index() {
		return ok(views.html.clients.index.render(Client.find.all(), newForm));
	}
	
	public static Result show(Long id) {
		return ok();
	}

	public static Result create() {
		Form<Client> filledForm = newForm.bindFromRequest();
		
		if(filledForm.hasErrors()) {
			return badRequest(views.html.clients.index.render(Client.find.all(), filledForm));
		}
		else {
			Client client = filledForm.get();
			client.save();
			return redirect(controllers.routes.Clients.index());
		}
		
	}
}
