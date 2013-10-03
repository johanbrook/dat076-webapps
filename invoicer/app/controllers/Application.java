package controllers;

import models.User;
import play.*;
import play.data.Form;
import play.mvc.*;

public class Application extends Controller {
  
	// All calls to this method is authenticated by the Secured class
	@Security.Authenticated(Secured.class)	
    public static Result index() {
        return redirect(controllers.routes.Invoices.index());
    }
    
	
}
