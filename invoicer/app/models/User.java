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
import play.data.validation.Constraints.Pattern;
import play.data.validation.Constraints.Required;

@Entity
public class User extends AbstractModel {
	
	@Id
	public Long id;
	@Required @Column(unique=true)
	public String username;
	@Required
	public String password;
	
	public String name;
	public String address;
	public String postalCode;
	public String country;
	
	@OneToMany(orphanRemoval=true, mappedBy="owner", cascade=CascadeType.ALL)
	public List<Invoice> invoices;
	
	@Pattern(value = "^[0-9]{6}-[0-9]{4}$")	//TODO: Verify that constraint works
	public String organizationNumber;
	
	public static Finder<Long, User> find = new Finder<Long, User>(Long.class, User.class);
	
	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	/**
	 * Authenticate username and password
	 * @param username The user's username
	 * @param password The user's password
	 * @return A User object matching the username and password, else null
	 */
	public static User authenticateUser(String username, String password) {
		return find.where().eq("username", username).
				eq("password", password).findUnique();
	}
}
