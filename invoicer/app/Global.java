import java.lang.reflect.Method;
import java.util.List;

import models.Invoice;
import models.User;

import com.avaje.ebean.Ebean;

import org.mindrot.jbcrypt.BCrypt;

import play.*;
import play.libs.*;
import play.mvc.Action;
import play.mvc.Http;

import service.*;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;

/**
 *	Global application settings class.
 *
 *	@author Johan Brook
 *	@copyright (c) 2013 Johan Brook
 *	@license MIT
 */

public class Global extends GlobalSettings {
	
	private Injector injector;
	
	@Override
	public Action onRequest(Http.Request request, Method actionMethod) {
		// Intercept requests and log them to the console.
		logRequest(request, actionMethod); 
	  return super.onRequest(request, actionMethod);
	}
	
	@Override
	public void onStart(Application app) {
		Logger.info("Starting Invoicer ... ");
		
		injector = Guice.createInjector();
		
		if(Invoice.find.findRowCount() == 0) {
			Ebean.save((List) Yaml.load("initial-data.yml"));
			
			// TODO: Input hashed passwords directly in yaml file?
			List<User> users = User.find.all();
			
			for(User user : users) {
				user.password = BCrypt.hashpw(user.password, BCrypt.gensalt());
				user.save();
			}
		}
	}
	
	@Override
	public <T> T getControllerInstance(Class<T> clazz) throws Exception {
		return injector.getInstance(clazz);
	}

	/*
		Log all incoming requests on the format:

			METHOD /path as 'type'
			  --> class controllers.Controller#action
	*/
	protected void logRequest(Http.Request request, Method actionMethod) {
		String types = (request.acceptedTypes().isEmpty()) ? "" : "'"+request.acceptedTypes().get(0)+"'"; 
		System.out.println(request.toString() + " as "+types);
		System.out.println("  --> "+actionMethod.getDeclaringClass()+"#"+actionMethod.getName());
		System.out.println();
	}
}
