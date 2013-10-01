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
import com.avaje.ebean.validation.NotNull;

import play.data.validation.Constraints.Pattern;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import scala.collection.parallel.ParIterableLike.Find;

@Entity
public class User extends Model {
	
	@Id
	public Long id;
	@Required @Column(unique=true) @NotNull
	public String login;
	@Required @NotNull
	public String password;
	
	public String name;
	public String address;
	public String postalCode;
	public String country;
	
	@Pattern(value = "^[0-9]{6}-[0-9]{4}$")	//TODO: Verify that constraint works
	public String organizationNumber;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="owner", orphanRemoval=true)
	@PrivateOwned // Needed for real orphan removal to work
	public List<Invoice> invoices = new ArrayList<Invoice>();
	
	public static Finder<Long, User> find = new Finder<Long, User>(Long.class, User.class);
	
	public User(String login, String password) {
		this.login = login;
		this.password = password;
	}
}
