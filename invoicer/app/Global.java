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

/**
 *	Global.java
 *
 *	@author Johan Brook
 *	@copyright (c) 2013 Johan Brook
 *	@license MIT
 */

public class Global extends GlobalSettings {
	
	@Override
	public Action onRequest(Http.Request request, Method actionMethod) {
	   System.out.println(request.toString() + " as '"+request.acceptedTypes().get(0)+"'");
	   System.out.println("  --> "+actionMethod.getDeclaringClass()+"#"+actionMethod.getName());
	   System.out.println();
	   
	   return super.onRequest(request, actionMethod);
	}
	
	@Override
	public void onStart(Application app) {
		Logger.info("Starting Invoicer ... ");
		
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
}
