/**
 *	MailController.java
 *
 *	@author Andreas Rolén
 *	@copyright (c) 2013 Andreas Rolén
 *	@license MIT
 */
package controllers;

import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import play.Logger;

import util.GoogleMail;
import models.BankAccount;
import models.Client;
import models.Invoice;

public class MailController {

	public static void sendOneInvoice(Invoice invoice) {
		StringBuilder message = new StringBuilder();
		message.append("Hi " + invoice.client.name + "\n");
		message.append("You got a new invoice to pay" + "\n");
		message.append("Invoice name: " + invoice.title + "\n" + "Due date: "
				+ invoice.dueDate + "\n");
		message.append("Account type: "
				+ invoice.bankAccount.accountType.getName() + "\nBank: "
				+ invoice.bankAccount.bank + "\nAccount nr: "
				+ invoice.bankAccount.accountNumber + "\nBIC: "
				+ invoice.bankAccount.bic + "\nIBAN: " + invoice.bankAccount.iban
				+ "\n");
		message.append("Sum to pay: " + invoice.totalRate);

		try {
			GoogleMail.send(invoice.client.email, "You got a new invoice",
					message.toString());
		} catch (AddressException e) {
			Logger.info("Email address parse failed");
			e.printStackTrace();
		} catch (MessagingException e) {
			Logger.info("Connection is dead");
			e.printStackTrace();
		}
	}

	public static void sendAllInvoices(List<Invoice> invoiceList) {
		StringBuilder message = new StringBuilder();
		if (invoiceList.size() > 0) {
			message.append("Hi " + invoiceList.get(0).client.name + "! \n\n");
			message.append("Here is all your invoices to pay from the user "
					+ invoiceList.get(0).owner.name + "\n\n--------------------------------- \n");
			for (Invoice i : invoiceList) {
				message.append("Invoice name: " + i.title + "\n" + "Due date: "
						+ i.dueDate + "\n\n");
				message.append("Account type: "
						+ i.bankAccount.accountType.getName() + "\nBank: "
						+ i.bankAccount.bank + "\nAccount nr: "
						+ i.bankAccount.accountNumber + "\nBIC: "
						+ i.bankAccount.bic + "\nIBAN: " + i.bankAccount.iban
						+ "\n");
				message.append("Sum to pay: " + i.totalRate);
				message.append("\n--------------------------------- \n");
			}
		} else {
			message.append("Congratulations you don't have any invoices to pay");
		}
		try {
			GoogleMail.send(invoiceList.get(0).client.email,
					"All your invoices", message.toString());
		} catch (AddressException e) {
			Logger.info("Email address parse failed");
			e.printStackTrace();
		} catch (MessagingException e) {
			Logger.info("Connection is dead");
			e.printStackTrace();
		}
	}

	public static void sendReminder(Invoice invoice) {
		StringBuilder message = new StringBuilder("Hi " + invoice.client.name
				+ "\n");
		message.append("Remember that you got an unpayed invoice (" + invoice.title + ") to pay due to "
				+ invoice.dueDate + "\n");
		message.append("Amount to pay: " + invoice.totalRate);
		try {
			GoogleMail.send(invoice.client.email, "Reminder: Invoice to pay",
					message.toString());
		} catch (AddressException e) {
			Logger.info("Email address parse failed");
			e.printStackTrace();
		} catch (MessagingException e) {
			Logger.info("Connection is dead");
			e.printStackTrace();
		}
	}
}
