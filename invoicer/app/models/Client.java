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
	@Column(nullable=false) // Should this use unique=true?
	public String name;
	
	@Required
	@Column(nullable=false)
	public String address;
	
	@Required
	@Column(nullable=false)
	public String postalCode;
	
	@Required
	@Column(nullable=false)
	public String country;
	
	@Required
	@Column(nullable=false, unique=true)
	public String orgNumber;
	
	@Required
	@Column(nullable=false)
	public String contactPerson;
	
	public static Finder<Long, Client> find = new Finder<Long, Client>(Long.class, Client.class);
	
	public Client(String name, String orgNumber) {
		this.name = name;
		this.orgNumber = orgNumber;
	}

}
