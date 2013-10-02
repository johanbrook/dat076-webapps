/**
 * 
 */
package controllers;

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
        return ctx.session().get("username");
    }
	
	/*
	 * Redirect to login screen
	 */
    @Override
    public Result onUnauthorized(Context ctx) {
        return redirect(routes.Application.login());
    }

}
