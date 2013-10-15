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

import play.data.validation.Constraints.Pattern;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model.Finder;
import service.Mailable;

@Entity
public class Client extends AbstractModel implements Mailable {
	
	@Required
	@Column(nullable=false)
	public String name;
	
	public String address;
	
	public String postalCode;
	
	public String country;
	
	@Required
	@Column(nullable=false, unique=true)
	@Pattern(value = "^[0-9]{6}-[0-9]{4}$")
	public String orgNumber;
	
	public String contactPerson;
	
	@Pattern(value = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
	public String email;
	
	
	public static Finder<Long, Client> find = new Finder<Long, Client>(Long.class, Client.class);
	
	public Client(String name, String orgNumber) {
		this.name = name;
		this.orgNumber = orgNumber;
	}

	@Override
	public String getReceiverAddress() {
		return this.email;
	}

}
