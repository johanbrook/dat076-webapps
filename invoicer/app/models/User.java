/**
*	User.java
*
*	@author Johan Brook
*	@copyright (c) 2013 Johan Brook
*	@license MIT
*/

package models;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

import javax.persistence.*;

import com.avaje.ebean.annotation.PrivateOwned;

import play.Logger;
import play.data.validation.Constraints.Pattern;
import play.data.validation.Constraints.Required;

import org.mindrot.jbcrypt.BCrypt;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

@Entity
public class User extends AbstractModel {
	
	@Required @Column(unique=true)
	public String username;
	@Required
	public String password;
	
	public String name;
	public String address;
	public String postalCode;
	public String country;
	
	@Pattern(value = "^[0-9]{6}-[0-9]{4}$", message = "error.organizationNumberPattern")
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
	
	
	/**
	 * (Used in views.users to display list of available countries)
	 * Returns a list of available countries
	 * @return A list of available countries
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getCountries() {
		// This is done because we don't want the countries to persist
		
		try {
			InputStream input = new FileInputStream(
					new File("conf/countries.yml"));
			
			Yaml yaml = new Yaml(new SafeConstructor());
			List<String> list = (List<String>) yaml.load(input);
			
			return list;
			
		} catch(FileNotFoundException e) {
			Logger.info("file 'countries.yml' not found");
			return null;
		}
		
        
	}
}
