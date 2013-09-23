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

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import scala.collection.parallel.ParIterableLike.Find;

@Entity
public class User extends Model {
	
	@Id
	public Long id;
	@Required @Column(unique=true)
	public String login;
	@Required
	public String password;
	
	@OneToMany(cascade = CascadeType.REMOVE, mappedBy="owner")
	public List<Invoice> invoices = new ArrayList<Invoice>();
	
	public static Finder<Long, User> find = new Finder<Long, User>(Long.class, User.class);
	
	public User(String login, String password) {
		this.login = login;
		this.password = password;
	}
	
	public static List<User> all() {
		return find.all();
	}
	
	public static void create(User user) {
		user.save();
	}
	
	public static void delete(Long id) {
		find.ref(id).delete();
	}
	
	public static void update(Long id, User user) {
		find.ref(id).update(user);
	}
}