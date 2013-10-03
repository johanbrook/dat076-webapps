/**
 * 
 */
package controllers;

import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * @author Robin
 *
 */
public class Session extends Controller {
	
	/**
	 * (Action called from GET to /login)
	 * Render the login screen
	 * @return A 200 OK simple result
	 */
    public static Result login() {
    	
    	// Send the LoginForm class to the form
    	return ok(views.html.login.render(Form.form(LoginForm.class)));
    }
    
    /**
     * (Action called from POST to /login)
     * Authenticates the login request. Sets the Session attribute 'username'.  
     * 
     * @return Call to index method,
     * else badRequest status code and rerendering of the login form
     */
    public static Result authenticate() {
    	
    	// The login form wraps the Login class (inner class below)
    	Form<LoginForm> loginForm = Form.form(LoginForm.class).bindFromRequest();
    	
    	if(loginForm.hasErrors()) {
    		return badRequest(views.html.login.render(loginForm));
    	}
    	
    	// Clear existing session and add logged in user's username
    	session().clear();
    	session("username", loginForm.get().username);
    	
    	return redirect(controllers.routes.Invoices.index());
    }
    
    /**
     * Logs the user out by clearing the session, passing a logout success-message
     * through a flash message and redirecting to login page
     * @return
     */
    public static Result logout() {
    	session().clear();
    	flash("success", "You have successfully logged out");
    	return redirect(controllers.routes.Session.login());
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
