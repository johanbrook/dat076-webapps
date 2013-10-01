package controllers;

import play.*;
import play.data.Form;
import play.mvc.*;

public class Application extends Controller {
  
    public static Result index() {
        return redirect(controllers.routes.Invoices.index());
    }
    
    public static Result login() {
    	return ok(views.html.login.render(Form.form(Login.class)));
    }
    
    public static Result authenticate() {
    	Form<Login> loginForm = Form.form(Login.class).bindFromRequest();
    	return ok();
    }
    
    
    public static class Login {
    	public String username;
    	public String password;
    	
    }
}
