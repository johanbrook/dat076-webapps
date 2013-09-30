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
import javax.persistence.Id;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
public class Client extends Model {
	
	@Id
	private long id;
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
	
	public Client(String name, String orgNumber) {
		this.name = name;
		this.orgNumber = orgNumber;
	}
	
	public static Client create(String name, String orgNumber) {
		return create(new Client(name, orgNumber));
		
	}
	
	public static Client create(Client client){ 
		client.save();
		return client;
	}
	
	public Long getId() {
		return this.id;
	}

}