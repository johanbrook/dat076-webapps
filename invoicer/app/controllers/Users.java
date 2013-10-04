/**
 * 
 */
package controllers;

import models.Client;
import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * @author Robin
 *
 */
public class Users extends Controller {
	
	public static Form<User> form = Form.form(User.class);
	
	
	/**
	 * (Action called from GET to /createuser)
	 * 
	 * 
	 * @return
	 */
	public static Result index() {
		return ok(views.html.users.index.render(form));
	}
	
	/**
	 * (Action called from POST to /createuser)
	 * 
	 * @return
	 */
	public static Result create() {
		/*
		Form<User> filledForm = form.bindFromRequest();
		
		if(filledForm.hasErrors()) {
			return badRequest(views.html.views.user.render());
		}
		
		User user = filledForm.get();
		user.save();
		*/
		
		return null;
		
	}

}
