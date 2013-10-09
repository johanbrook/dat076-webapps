/**
 *	MailController.java
 *
 *	@author Andreas Rolén
 *	@copyright (c) 2013 Andreas Rolén
 *	@license MIT
 */
package controllers;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import play.Logger;

import util.GoogleMail;
import models.Client;
import models.Invoice;

public class MailController {

	public static void sendOneInvoice(String userName, String pw,
			Client client, Invoice invoice) {
		StringBuilder message = new StringBuilder();
		message.append("Hi " + client.name + "\n");
		message.append("You got a new invoice to pay" + "\n");
		message.append("Invoice name: " + invoice.title + "\n" + "Due date: "
				+ invoice.dueDate + "\n");
		// TODO Update model with this
		//message.append("To bank account type: " + invoice.owner.bankAccounts[0].type + "\n" + "Bank: " + invoice.owner.bankAccount[0].bank + "\n" + "Account number: " + invoice.owner.bankAccount[0].accountNumber)
		//message.append("Sum to pay: " + invoice.amount);
		
		try {
			GoogleMail.send(userName, pw, client.email, "You got a new invoice", message.toString());
		} catch (AddressException e) {
			Logger.info("Email address parse failed");
			e.printStackTrace();
		} catch (MessagingException e) {
			Logger.info("Connection is dead");
			e.printStackTrace();
		}

	}
}
