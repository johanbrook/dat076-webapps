package controllers;

import models.User;
import play.*;
import play.data.Form;
import play.mvc.*;

public class Application extends Controller {
  
	@Security.Authenticated(Secured.class)
    public static Result index() {
        return redirect(controllers.routes.Invoices.index());
    }
    
    public static Result login() {
    	return ok(views.html.login.render(Form.form(Login.class)));
    }
    
    public static Result authenticate() {
    	Form<Login> loginForm = Form.form(Login.class).bindFromRequest();
    	if(loginForm.hasErrors()) {
    		return badRequest(views.html.login.render(loginForm));
    	}
    	session().clear();
    	session("user", loginForm.get().username);
    	return index();
    }
    
    /**
     * Inner class for handling user login
     * 
     * @author Robin
     */
    public static class Login {
    	public String username;
    	public String password;
    	
    	/**
    	 * Validates the username and password against existing users in database
    	 * @return Error message if username or password didn't match, else null
    	 */
    	public String validate() {
    		if(User.authenticateUser(username, password) == null) {
    			return "Wrong username or password";
    		}
    		
    		return null;
    	}
    }
}
