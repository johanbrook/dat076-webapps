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

import org.joda.time.DateTime;

import play.data.validation.Constraints.Required;
import play.data.format.*;

@Entity
public class Invoice extends AbstractModel {
	
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
	
	// @Required
	// TODO: should be required, but deactivate for now due to controller problems
	@Column(nullable=false)
	@ManyToOne(cascade=CascadeType.PERSIST)
	public User owner;
	
	@Required
	@ManyToOne(cascade={CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE})
	public Client client;
	
	// Finder object
	public static Finder<Long, Invoice> find = new Finder<Long, Invoice>(Long.class, Invoice.class);
	
	public Invoice() {
		// Default constructor
		this.invoiceDate = new Date();
	}
	
	public Invoice(Date invoiceDate, User owner, Client client) {
		this.invoiceDate = invoiceDate;
		this.owner = owner;
		this.client = client;
	}

	public Invoice(User owner, Client client) {
		this(new Date(), owner, client);
	}
	
	public static List<Invoice> invoicesOfUser(Long userId) {
		return find.where().like("owner", String.valueOf(userId)).findList();
	}

	public static List<Invoice> getPaid() {
		return find.where().isNotNull("datePaid").findList();
	}
	
	public boolean wasPaidOnTime() {
		return this.isPaid() && (this.datePaid.compareTo(this.dueDate) <= 0);
	}
	
	public boolean isPaid() {
		return (this.datePaid != null);
	}
	
	public void setPaid() {
		this.setPaid(true);
	}
	
	public void setPaid(boolean paid) {
		this.datePaid = paid ? DateTime.now().toDate() : null;
	}
}
