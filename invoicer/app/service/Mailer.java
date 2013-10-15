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
import play.api.templates.Template2;

import util.GoogleMail;
import models.AbstractModel;
import models.BankAccount;
import models.Client;
import models.Invoice;

public class Mailer<T extends Mailable> {
	
	private boolean send(String receiver, String subject, String message) {
		try {
			GoogleMail.send(receiver, subject, message);
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
	

	public boolean send(T item, String subject, Template1<T, Html> template) {
		Html html = template.render(item);
		return this.send(item.getReceiverAddress(), subject, html.toString());
	}

	public <S extends Mailable> boolean sendMany(
			S receiver, 
			List<T> items, 
			String subject, 
			Template2<S, List<T>, Html> template) {
		
		Html html = template.render(receiver, items);
		return this.send(receiver.getReceiverAddress(), subject, html.toString());
	}
}
