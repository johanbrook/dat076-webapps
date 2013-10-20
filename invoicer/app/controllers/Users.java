/**
 * 
 */
package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.Client;
import models.Invoice;
import models.User;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import org.mindrot.jbcrypt.BCrypt;

import com.avaje.ebean.Ebean;

import controllers.Application.Responder;

import views.html.users.*;

/**
 * @author Robin
 *
 */
public class Users extends Application {
	
	public static Form<User> form = Form.form(User.class);
	
	
	/**
	 * (Action called from GET to /user)
	 * 
	 * 
	 * @return
	 */
	public static Result index() {
		return respondTo(new Responder() {

			@Override
			public Result json() {
				return badRequest();
			}

			@Override
			public Result html() {
				return ok(index.render(form));
			}

			@Override
			public Result script() {
				return badRequest();
			}
		});
	}
	
	/**
	 * (Action called from POST to /user)
	 * 
	 * @return
	 */
	public static Result create() {
		
		final Form<User> filledForm = form.bindFromRequest();
		
		// Check accept conditions
        if(!"true".equals(filledForm.field("accept").value())) {
            filledForm.reject("accept", "You must accept the terms and conditions");
        }
        
        // Check repeated password
        if(!filledForm.field("password").valueOr("").isEmpty()) {
            if(!filledForm.field("password").valueOr("").equals(filledForm.field("repeatPassword").value())) {
                filledForm.reject("repeatPassword", "Passwords don't match");
            }
        }
        
    	String username = filledForm.field("username").valueOr("");
    	
    	if(username.equals("admin") || username.equals("guest")) {
            filledForm.reject("username", "'admin' and 'guest' are reserved usernames");
        
        } else if(User.find.where().eq("username", username).findUnique() != null) {
    		filledForm.reject("username", "Username is taken!");
    	}
		
		if(filledForm.hasErrors()) {
			
			Logger.info("Form errors " + filledForm.errors());
			
			return respondTo(new Responder() {

				@Override
				public Result json() {
					return badRequest();
				}

				@Override
				public Result html() {
					flash("error", "There were errors in your form.");
					return badRequest(index.render(filledForm));
				}

				@Override
				public Result script() {
					return badRequest();
				}
			});
		}
		
		// Form valid, create user
		final User user = filledForm.get();
		
		// set country to null if no country was chosen
		if(filledForm.field("country").value().equals("default")) {
			user.country = null;
		}
		
		// Hash the password with jBCrypt and save to database
		user.password = BCrypt.hashpw(user.password, BCrypt.gensalt());
		user.save();
		
		session("userId", String.valueOf(user.id));
		Logger.info("*** User '" + user.username + "' created ***");
		
		return respondTo(new Responder() {

			@Override
			public Result json() {
				return created(Json.toJson(user));
			}

			@Override
			public Result html() {
				flash("success", "You have signed up!");
				return redirect(controllers.routes.Invoices.index());
			}

			@Override
			public Result script() {
				return badRequest();
			}
		});
		
	}
	
	/**
	 * (Action called from GET to /user/show)
	 * 
	 * @return
	 */
	@Security.Authenticated(Secured.class)
	public static Result show() {
		
		return respondTo(new Responder() {

			@Override
			public Result json() {
				
				User user = Session.getCurrentUser();
				setLocationHeader(user);
				return ok(Json.toJson(user));
			}

			@Override
			public Result html() {
				return ok(show.render());
			}

			@Override
			public Result script() {
				return badRequest();
			}
		});
	}
	
	/**
	 * (Action called from GET to /user/edit)
	 * 
	 * @return
	 */
	@Security.Authenticated(Secured.class)
	public static Result edit() {
		
		return respondTo(new Responder() {

			@Override
			public Result json() {
				return badRequest();
			}

			@Override
			public Result html() {
				return ok(edit.render(Form.form(UserEditForm.class)));
			}

			@Override
			public Result script() {
				return badRequest();
			}
		});
	}
	
	
	// TODO: Use id in action for users or just always use the one stored in session?
	/**
	 * (Action called from POST to /user/edit)
	 * 
	 * @return
	 */
	@Security.Authenticated(Secured.class)
	public static Result update() {
		
		final Form<UserEditForm> filledForm = Form.form(UserEditForm.class).bindFromRequest();
		Map<String, String> userMap = filledForm.data();
		
		// Input needed key-value pair
		userMap.put("password", Session.getCurrentUser().password);
		
        // Check password
		String oldPassword = filledForm.field("oldPassword").value();
		String newPassword = filledForm.field("newPassword").value();
		String newRepeatedPassword = filledForm.field("newRepeatedPassword").value();
		 
		if(oldPassword != null && !oldPassword.equals("")) {
			
			// Check if old password was correct
			if(User.authenticateUser(filledForm.field("username").value(), oldPassword) != null) {
				
				// Check if new password has been entered
				if(newPassword.equals("") || newPassword == null) {
					filledForm.reject("newPassword", "You need to enter a new password");
					
				// Check if new password was repeated correctly
				} else if(!newPassword.equals(newRepeatedPassword)) {
					filledForm.reject("newRepeatedPassword", "Passwords don't match");
				}
		    	
		    	// Successfull password change
		    	else {
		    	
		        	// Insert hashed password key-value pair
		        	userMap.put("password",
		        			BCrypt.hashpw(filledForm.field("newPassword").value(),
		        					BCrypt.gensalt()));
		    	}
			}
			
			else {
				filledForm.reject("oldPassword", "Incorrect password!");
			}
			
		// Reject if new password inserted but not the old one
		} else if(!(newPassword.equals("") || newPassword == null) ||
				!(newRepeatedPassword.equals("") || newRepeatedPassword == null)) {
			
			filledForm.reject("oldPassword", "You need to fill in your old password");
		}
		
		if(filledForm.hasErrors()) {
			
			Logger.info("Form errors " + filledForm.errors());
			
			return respondTo(new Responder() {

				@Override
				public Result json() {
					return badRequest(Json.toJson(filledForm));
				}

				@Override
				public Result html() {
					flash("fail",
							"Something went wrong when trying to update your information");
					return badRequest(edit.render(filledForm));
				}

				@Override
				public Result script() {
					return badRequest();
				}
			});
		}
		
		// null all non set keys
		for(String key : userMap.keySet()) {
			if(userMap.get(key).equals("")) {
				userMap.put(key, null);
			}
		}
		
		// "Fill in" the original User forms fields
		form = form.bind(userMap);
		final User user = form.get();
		
		user.id = Session.getCurrentUser().id;
		
		// Use Ebean.update to persist null fields
		Ebean.update(user, userMap.keySet());
		
		session("userId", String.valueOf(user.id));
		Logger.info("*** User '" + user.username + "' edited ***");
		
		return respondTo(new Responder() {

			@Override
			public Result json() {
				return ok(Json.toJson(user));
			}

			@Override
			public Result html() {
				flash("success", "User updated successfully!");
				return redirect(controllers.routes.Users.show());
			}

			@Override
			public Result script() {
				return badRequest();
			}
		});
	}
	
	/**
     * Inner class for handling user edit (sent as parameter to the edit form)
     * Used because field requirements are different than User model
     * 
     * @author Robin
     */
    public static class UserEditForm {
    	public String username;
    	public String name;
    	public String address;
    	public String postalCode;
    	public String organizationNumber;
    	public String oldPassword;
    	public String newPassword;
    	public String newRepeatedPassword;
    	public String country;
    }

}
