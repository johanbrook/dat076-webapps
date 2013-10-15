/**
 *	MailController.java
 *
 *	@author Andreas Rolén
 *	@copyright (c) 2013 Andreas Rolén
 *	@license MIT
 */
package service;

import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import play.Logger;
import play.api.templates.Html;
import play.api.templates.Template1;

import util.GoogleMail;
import models.AbstractModel;
import models.BankAccount;
import models.Client;
import models.Invoice;

public class Mailer<T extends Mailable> {
	

	public boolean send(T item, String subject, Template1<T, Html> template) {
		Html html = template.render(item);

		try {
			GoogleMail.send(item.getRecieverAddress(), subject, html.toString());
			return true;
			
		} catch (AddressException e) {
			Logger.info("Email address parse failed");
			e.printStackTrace();
			return false;
			
		} catch (MessagingException e) {
			Logger.info("Connection is dead");
			e.printStackTrace();
			return false;
		}
	}

	public boolean sendAllInvoices(List<Invoice> invoiceList) {
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
			GoogleMail.send(invoiceList.get(0).client.email, "All your invoices", message.toString());
			return true;
			
		} catch (AddressException e) {
			Logger.info("Email address parse failed");
			e.printStackTrace();
			return false;
			
		} catch (MessagingException e) {
			Logger.info("Connection is dead");
			e.printStackTrace();
			return false;
		}
	}
}
