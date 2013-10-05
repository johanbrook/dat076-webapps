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
		
		Form<User> filledForm = form.bindFromRequest();
		
		// Check accept conditions
        if(!"true".equals(filledForm.field("accept").value())) {
            filledForm.reject("accept", "You must accept the terms and conditions");
        }
        
        // Check repeated password
        if(!filledForm.field("password").valueOr("").isEmpty()) {
            if(!filledForm.field("password").valueOr("").equals(filledForm.field("repeatPassword").value())) {
                filledForm.reject("repeatPassword", "Password don't match");
            }
        }
        
        // Check if the username is valid
        if(!filledForm.hasErrors()) {
        	
        	String username = filledForm.get().username;
        	
            if(username.equals("admin") || username.equals("guest")) {
                filledForm.reject("username", "'admin' and 'guest' are reserved usernames");
                
            } else {
            
            	List<User> users = User.find.findList();
            	
            	// See if username is taken
            	if(User.find.where().eq("username", username).findUnique() != null) {
            		filledForm.reject("username", "Username is taken!");
            	}
            }
            
        }
		
		if(filledForm.hasErrors()) {
			
			Logger.info("Errors " + filledForm.errors());
			return badRequest(views.html.users.index.render(form));
		}
		
		User user = filledForm.get();
		user.save();
		
		session("userId", String.valueOf(user.id));
		
		Logger.info("User: " + user.username + " created");
		
		return Application.index();
		
	}

}
