package controllers;

import play.*;
import play.mvc.*;

public class Application extends Controller {
  
    public static Result index() {
        return redirect(controllers.routes.Invoices.index());
    }
    
    public static Result login() {
    	return ok(views.html.login.render());
    }
}
