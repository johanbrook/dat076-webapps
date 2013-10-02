/**
*	User.java
*
*	@author Johan Brook
*	@copyright (c) 2013 Johan Brook
*	@license MIT
*/

package models;

import java.util.*;

import javax.persistence.*;

import com.avaje.ebean.annotation.PrivateOwned;
import play.data.validation.Constraints.Required;

@Entity
public class User extends AbstractModel {
	
	@Required @Column(unique=true)
	public String login;
	@Required
	public String password;
	
	@OneToMany(orphanRemoval=true, mappedBy="owner", cascade=CascadeType.ALL)
	public List<Invoice> invoices;
	
	public static Finder<Long, User> find = new Finder<Long, User>(Long.class, User.class);
	
	public User(String login, String password) {
		this.login = login;
		this.password = password;
	}
	
	public static List<User> all() {
		return find.all();
	}
	
	public static User create(String login, String password) {
		return create(new User(login, password));
	}
	
	public static User create(User user) {
		user.save();
		return user;
	}
	
	public static void update(Long id, User user) {
		find.ref(id).update(user);
	}
}
