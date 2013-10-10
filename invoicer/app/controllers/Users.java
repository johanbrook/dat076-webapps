/**
 * 
 */
package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.Client;
import models.Invoice;
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
	 * (Action called from GET to /user)
	 * 
	 * 
	 * @return
	 */
	public static Result index() {
		return ok(index.render(form));
	}
	
	/**
	 * (Action called from POST to /user)
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
	 * (Action called from GET to /user/show)
	 * 
	 * @return
	 */
	public static Result show() {
		return ok(views.html.users.show.render());
	}
	
	/**
	 * (Action called from GET to /user/edit)
	 * 
	 * @return
	 */
	public static Result edit() {
		form = form.fill(Session.getCurrentUser());
		
		return ok(views.html.users.edit.render(form));
	}
	
	
	/**
	 * (Action called from POST to /user/edit)
	 * @return
	 */
	public static Result update() {
		
		Form<User> filledForm = form.bindFromRequest();
		
		Logger.info("Form errors " + filledForm.errors());
		
        // Check password
        if(!filledForm.field("oldPassword").valueOr("").isEmpty()) {
        	
        	if(!BCrypt.checkpw(filledForm.field("oldPassword").value(), Session.getCurrentUser().password)) {
        		filledForm.reject("oldPassword", "Incorrect password");
        	}
        	
        	else if(!filledForm.field("newPassword").valueOr("").equals(filledForm.field("repeatNewPassword").value())) {
                filledForm.reject("repeatNewPassword", "Passwords don't match");
            }
        	
        	else {
        	
	        	Map<String, String> newForm = filledForm.data();
	        	
	        	newForm.put("password", filledForm.field("newPassword").value());
	        	
	        	filledForm = filledForm.bind(newForm);
        	}
        }
        
        // Observe that username not able to edit (for now), so these are redundant
        /*
    	String username = filledForm.field("username").valueOr("");
    	
    	if(username.equals("admin") || username.equals("guest")) {
            filledForm.reject("username", "'admin' and 'guest' are reserved usernames");
        
        } else if(!username.equals(Session.getCurrentUser().username) &&
        		User.find.where().eq("username", username).findUnique() != null) {
    		filledForm.reject("username", "Username is taken!");
    	}
    	*/
		
		if(filledForm.hasErrors()) {
			
			Logger.info("Form errors " + filledForm.errors());
			return badRequest(edit.render(filledForm));
		}
		
		// Form valid, create user
		User user = filledForm.get();
		
		// TODO: Do this somewhere else, conventions?
		// set country to null if no country was chosen
		if(filledForm.field("country").value().equals("default")) {
			user.country = null;
		}
		
		user.id = Session.getCurrentUser().id;
		
		// Hash the password with jBCrypt and save to database
		user.password = BCrypt.hashpw(user.password, BCrypt.gensalt());
		
		Logger.info(user.id + " " + user.username + " " + user.address + " " + user.country + " " + user.name + " " + user.organizationNumber);
		user.update();
		
		session("userId", String.valueOf(user.id));
		Logger.info("*** User '" + user.username + "' edited ***");
		
		return redirect(controllers.routes.Users.show());
	}

}
