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

import play.Logger;
import play.data.validation.Constraints.Pattern;
import play.data.validation.Constraints.Required;

import org.mindrot.jbcrypt.BCrypt;

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
	
	@Pattern(value = "^[0-9]{6}-[0-9]{4}$", message = "error.organizationNumberPattern")	//TODO: Verify that constraint works
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
		
		User user = find.where().eq("username", username).findUnique();
		
		// Check the entered password against the hashed password stored in DB
		if(user != null && BCrypt.checkpw(password, user.password)){
			return user;
		}
		
		return null;
	}
	
	
	// TODO: Handle countries better?
	/**
	 * (Used in views.users to display list of available countries)
	 * Returns a list of available countries
	 * @return A list of available countries
	 */
	public static List<String> getCountries() {
		// This is done because we don't want the countries to persist
		List<String> countries = new ArrayList<String>();
        countries.add("France");
        countries.add("Austria");
        countries.add("Belgium");
        countries.add("Bulgaria");
        countries.add("Cyprus");
        countries.add("Czech Republic");
        countries.add("Denmark");
        countries.add("Estonia");
        countries.add("Finland");
        countries.add("French Guiana");
        countries.add("Germany");
        countries.add("Gibraltar");
        countries.add("Greece");
        countries.add("Guadeloupe");
        countries.add("Hungary");
        countries.add("Ireland");
        countries.add("Italy");
        countries.add("Latvia");
        countries.add("Lithuania");
        countries.add("Luxembourg");
        countries.add("Malta");
        countries.add("Martinique");
        countries.add("Netherlands");
        countries.add("Poland");
        countries.add("Portugal");
        countries.add("Reunion");
        countries.add("Romania");
        countries.add("Slovak (Republic)");
        countries.add("Slovenia");
        countries.add("Spain");
        countries.add("Sweden");
        countries.add("United Kingdom");
        return countries;
	}
}
