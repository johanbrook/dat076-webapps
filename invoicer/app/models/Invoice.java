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
import play.db.ebean.Model;

@Entity
public class Invoice extends Model {
	
	@Id
	public Long id;
	public String title;
	public Date invoiceDate;
	public Date dueDate;
	public Date datePaid;
	
	public boolean isPaid = false;
	
	// Finder object
	public static Model.Finder<Long, Invoice> find = new Model.Finder<Long, Invoice>(Long.class, Invoice.class);
	
	public Invoice(String title) {
		this.title = title;
	}
	
	public static List<Invoice> all() {
		return find.all();
	}
	
	public static void create(Invoice invoice) {
		invoice.save();
	}
	
	public static void delete(Long id) {
		find.ref(id).delete();
	}
}
