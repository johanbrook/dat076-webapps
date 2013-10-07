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
        return ctx.session().get("userId");
    }
	
	/*
	 * Redirect to login screen
	 */
    @Override
    public Result onUnauthorized(Context ctx) {
    	
    	//TODO: return unauthorized status code instead?
    	Logger.info("onUnauthorized");
        return redirect(routes.Session.newSession());
    }

}
