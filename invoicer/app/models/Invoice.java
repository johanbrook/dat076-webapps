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
import com.avaje.ebean.ExpressionList;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import play.data.validation.Constraints.Required;
import play.data.format.*;
import util.CustomDateSerializer;

@Entity
public class Invoice extends AbstractModel {
	
	public String title;
	
	@Required
	@Temporal(TemporalType.DATE)
	@Column(nullable=false)
	@Formats.DateTime(pattern="yyyy-MM-dd")
	@JsonSerialize(using = CustomDateSerializer.class)
	public Date invoiceDate;
	
	@Required
	@Temporal(TemporalType.DATE)
	@Formats.DateTime(pattern="yyyy-MM-dd")
	@JsonSerialize(using = CustomDateSerializer.class)
	public Date dueDate;
	
	@Temporal(TemporalType.DATE)
	@Formats.DateTime(pattern="yyyy-MM-dd")
	@JsonSerialize(using = CustomDateSerializer.class)
	public Date datePaid;
	
	// @Required
	// TODO: should be required, but deactivate for now due to controller problems
	@Column(nullable=false)
	@ManyToOne(cascade=CascadeType.PERSIST)
	@JsonIgnore
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

	public static ExpressionList<Invoice> invoicesOfUser(Long userId) {
		return find.where().like("owner", String.valueOf(userId));
	}
	
	public static List<Invoice> getInvoicesOfUser(Long userId) {
		return invoicesOfUser(userId).findList();
	}

	public static List<Invoice> getOverdueInvoicesOfUser(Long userId) {
		return invoicesOfUser(userId).where()
				.isNull("datePaid")
				.lt("dueDate", DateTime.now())
			.findList();
	}
	
	public boolean wasPaidOnTime() {
		return this.isPaid() && !this.isOverdue(this.datePaid);
	}

	public boolean isOverdue() {
		return !this.isPaid() && this.isOverdue(new Date());
	}

	public boolean isOverdue(Date date) {
		return date.compareTo(this.dueDate) > 0;
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
