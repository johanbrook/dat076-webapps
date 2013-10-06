/**
*	Client.java
*
*	@author Andreas Rolén
*	@copyright (c) 2013 Andreas Rolén
*	@license MIT
*/

package models;

import javax.persistence.Column;
import javax.persistence.Entity;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model.Finder;

@Entity
public class Client extends AbstractModel {
	
	@Required
	@Column(nullable=false) //TODO Should this use unique=true?
	public String name;
	
	public String address;
	
	public String postalCode;
	
	public String country;
	
	@Required
	@Column(nullable=false, unique=true)
	//TODO: @rolen - should validate format of org.nr. string as well
	// try using regular expressions to validate a string on format "XXXXXX-XXXX"
	// where the Xes are numbers.
	public String orgNumber;
	
	public String contactPerson;
	
	
	public static Finder<Long, Client> find = new Finder<Long, Client>(Long.class, Client.class);
	
	public Client(String name, String orgNumber) {
		this.name = name;
		this.orgNumber = orgNumber;
	}

}
