/**
 * 
 */
package controllers;

import java.util.ArrayList;
import java.util.List;

import models.Client;
import models.User;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

import org.mindrot.jbcrypt.BCrypt;
import views.html.users.*;

/**
 * @author Robin
 *
 */
public class Users extends Application {
	
	public static Form<User> form = Form.form(User.class);
	
	
	/**
	 * (Action called from GET to /createuser)
	 * 
	 * 
	 * @return
	 */
	public static Result index() {
		return ok(index.render(form));
	}
	
	/**
	 * (Action called from POST to /createuser)
	 * 
	 * @return
	 */
	public static Result create() {
		
		Form<User> filledForm = form.bindFromRequest();
		
		// Check accept conditions
        if(!"true".equals(filledForm.field("accept").value())) {
            filledForm.reject("accept", "You must accept the terms and conditions");
        }
        
        // Check repeated password
        if(!filledForm.field("password").valueOr("").isEmpty()) {
            if(!filledForm.field("password").valueOr("").equals(filledForm.field("repeatPassword").value())) {
                filledForm.reject("repeatPassword", "Passwords don't match");
            }
        }
        
    	String username = filledForm.field("username").valueOr("");
    	
    	if(username.equals("admin") || username.equals("guest")) {
            filledForm.reject("username", "'admin' and 'guest' are reserved usernames");
        
        } else if(User.find.where().eq("username", username).findUnique() != null) {
    		filledForm.reject("username", "Username is taken!");
    	}
		
		if(filledForm.hasErrors()) {
			
			Logger.info("Form errors " + filledForm.errors());
			return badRequest(index.render(filledForm));
		}
		
		// Form valid, create user
		User user = filledForm.get();
		
		// set country to null if no country was chosen
		if(filledForm.field("country").value().equals("default")) {
			user.country = null;
		}
		
		// Hash the password with jBCrypt and save to database
		user.password = BCrypt.hashpw(user.password, BCrypt.gensalt());
		user.save();
		
		session("userId", String.valueOf(user.id));
		Logger.info("*** User '" + user.username + "' created ***");
		
		return redirect(controllers.routes.Invoices.index());
		
	}
	
	/**
	 * (Action called from GET to /user)
	 * 
	 * @return
	 */
	public static Result show() {
		return ok(views.html.users.show.render());
	}

}
