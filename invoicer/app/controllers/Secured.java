/**
 * 
 */
package controllers;

import controllers.Session.LoginForm;
import play.Logger;
import play.mvc.*;
import play.mvc.Http.*;
import play.mvc.Security.Authenticator;

import models.*;

public class Secured extends Authenticator {
	
	/*
	 * If this method returns a value then the authenticator considers the user
	 * to be logged in and lets the request proceed.
	 * 
	 * If the method returns null then the authenticator will block the request
	 * and instead invoke onUnathorized
	 */
	@Override
    public String getUsername(Context ctx) {
		
		try {
			String username = User.find.byId(
					Long.parseLong(ctx.session().get("userId"))).username;
			
			
			return username;
			
		} catch (NumberFormatException e) {
			Logger.info("User Id not set or unvalid in session");
			return null;
		}
    }
	
	/*
	 * Redirect to login screen
	 */
    @Override
    public Result onUnauthorized(Context ctx) {
    	
    	//TODO: return unauthorized status code instead?
        return redirect(routes.Session.newSession());
    }

}
