/**
*	Invoice.java
*
*	@author Johan Brook
*	@copyright (c) 2013 Johan Brook
*	@license MIT
*/

package models;

import java.util.*;

import play.db.ebean.Model;

public class Invoice extends Model {
	
	public Long id;
	public Date invoiceDate;
	public Date dueDate;
	public Date datePaid;
	
	public boolean isPaid;
	
	public Invoice(Long id) {
		this.id = id;
	}
	
	public static List<Invoice> all() {
		List<Invoice> invoices = new ArrayList<Invoice>();
		
		for(int i = 0; i < 10; i++) {
			invoices.add(new Invoice((long) i));
		}
		
		return invoices;
	}
}
