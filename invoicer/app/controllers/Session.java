/**
 * 
 */
package controllers;

import models.User;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * @author Robin
 *
 */
public class Session extends Application {
	
	/**
	 * (Action called from GET to /login)
	 * 
	 * Render the login screen
	 * @return A 200 OK simple result
	 */
    public static Result newSession() {
    	
    	// Send the LoginForm class to the form
    	return ok(views.html.session.index.render(Form.form(LoginForm.class)));
    }
    
    /**
     * (Action called from POST to /login)
     * 
     * Authenticates the login request. Sets the Session attribute 'username'.
     * @return Call to index method,
     * else badRequest status code and rerendering of the login form
     */
    public static Result createSession() {
    	
    	// The login form wraps the Login class (inner class below)
    	Form<LoginForm> loginForm = Form.form(LoginForm.class).bindFromRequest();
    	
    	if(loginForm.hasErrors()) {
    		return badRequest(views.html.session.index.render(loginForm));
    	}
    	
    	User user = User.find.where().eq("username", loginForm.get().username).findUnique();
    	
    	// Clear existing session and add logged in user's username
    	session().clear();
    	session("userId", String.valueOf(user.id));
    	
    	Logger.info("*** User '" + user.username + "' logged in ***");
    	
    	return redirect(controllers.routes.Invoices.index());
    }
   
    
    /**
     * (Action called from GET to /logout)
     * 
     * Logs the user out by clearing the session
     * @return
     */
    public static Result destroy() {
    	
    	Logger.info("*** User '" + getCurrentUser().username + "' logged out ***");
    	session().clear();
    	
    	flash("success", "You have successfully logged out");
    	
    	return redirect(controllers.routes.Session.newSession());
    }
    
    /**
     * Returns the user based of the session's userId attribute
     * @return The User instance with the session's Id attribute
     */
    public static User getCurrentUser() {
    	return User.find.where().eq("id", session().get("userId")).findUnique();
    }
    
    /**
     * Inner class for handling user login (sent as parameter to the login form)
     * 
     * @author Robin
     */
    public static class LoginForm {
    	public String username;
    	public String password;
    	
    	/**
    	 * Ad-hoc validation, for username and password
    	 * @return Error message if username or password didn't match, else null
    	 */
    	public String validate() {
    		
    		//TODO: Hash password
    		// Call the User objects authentication
    		if(User.authenticateUser(username, password) == null) {
    			return "Wrong username or password";
    		}
    		
    		return null;
    	}
    }

}
