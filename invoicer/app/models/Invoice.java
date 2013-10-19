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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import play.data.validation.Constraints.Required;
import play.data.format.*;
import service.Mailable;
import util.CustomDateSerializer;
import util.DateOverlapException;

@Entity
public class Invoice extends AbstractModel implements Mailable {
	
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
	
	@Required
	@ManyToOne(cascade={CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE})
	public Client client;

	@Required
	@Column(nullable=false)
	@ManyToOne(cascade={CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE})
	public BankAccount bankAccount;

	// Embed the 'starred' attribute directly in the model. If we were to handle
	// many more invoice records, I would've created a new table for keeping track
	// of starred invoices instead of having a redundant column in the Invoices table.
	public boolean starred = false;

	public double totalRate = 0;
	
	// Finder object
	public static Finder<Long, Invoice> find = new Finder<Long, Invoice>(Long.class, Invoice.class);
	
	/**
	 * Create new invoice with invoice date set to current date and
	 * due date set to a month from now.
	 */
	public Invoice() {
		// Default constructor
		this.invoiceDate = new Date();
		this.dueDate = DateTime.now().plusMonths(1).toDate();
	}

	public static com.avaje.ebean.Query<Invoice> invoicesOfUser(Long userId) {
		return Invoice.find.fetch("bankAccount").where().eq("owner_id", userId.toString()).query();
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
	
	public static List<Invoice> getInvoicesOfClient(Client client) {
		return Invoice.find.where().eq("client_id", client.id).findList();
	}
	
	public User getOwner() {
		return this.bankAccount.owner;
	}

	public void setBankAccountFromId(Long accountId) {
		this.bankAccount = BankAccount.find.byId(accountId);
	}

	public void setClientFromId(Long clientId) {
		this.client = Client.find.byId(clientId);
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

	public void toggleStarred() {
		this.starred = !this.starred;
	}

	@Override
	@JsonIgnore
	public String getReceiverAddress() {
		return this.client.email;
	}
	
	/*
	 * Override Model#save() in order to ensure that the due date
	 * is before the invoice date before persisting.
	 * 
	 * @see play.db.ebean.Model#save()
	 * @throws DateOverlapException If the due date is before the invoice date
	 */
	@Override
	public void save() {
		if(this.invoiceDate.compareTo(this.dueDate) > 0) {
			throw new DateOverlapException(this.invoiceDate, this.dueDate);
		}
		else {
			super.save();
		}
	}
}
