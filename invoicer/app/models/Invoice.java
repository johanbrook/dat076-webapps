/**
*	Invoice.java
*
*	@author Johan Brook
*	@copyright (c) 2013 Johan Brook
*	@license MIT
*/

package models;

import java.util.*;

import javax.persistence.*;

import play.data.validation.Constraints.Required;
import play.data.format.*;
import play.db.ebean.Model;

@Entity
public class Invoice extends Model {
	
	@Id
	public Long id;
	public String title;
	
	@Required
	@Temporal(TemporalType.DATE)
	@Column(nullable=false)
	@Formats.DateTime(pattern="yyyy-MM-dd")
	public Date invoiceDate;
	
	@Required
	@Temporal(TemporalType.DATE)
	@Formats.DateTime(pattern="yyyy-MM-dd")
	public Date dueDate;
	
	@Temporal(TemporalType.DATE)
	@Formats.DateTime(pattern="yyyy-MM-dd")
	public Date datePaid;
	
	@Required
	@Column(nullable=false)
	@ManyToOne(cascade = CascadeType.ALL)
	public User owner;
	
	// Finder object
	public static Model.Finder<Long, Invoice> find = new Model.Finder<Long, Invoice>(Long.class, Invoice.class);
	
	public Invoice() {
		// Default constructor
		this.invoiceDate = new Date();
	}
	
	public Invoice(Date invoiceDate, User owner) {
		this.invoiceDate = invoiceDate;
		this.owner = owner;
	}

	public Invoice(User owner) {
		this(new Date(), owner);
	}
	
	public static List<Invoice> all() {
		return find.all();
	}
	
	public static Invoice create(Invoice invoice) {
		invoice.save();
		return invoice;
	}
	
	public boolean wasPaidOnTime() {
		return this.isPaid() && (this.datePaid.compareTo(this.dueDate) <= 0);
	}
	
	public boolean isPaid() {
		return (this.datePaid != null);
	}
}
