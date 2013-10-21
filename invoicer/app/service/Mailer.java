/**
*	Mailer.java
*
*	@author Johan Brook
*	@copyright (c) 2013 Johan Brook
*	@license MIT
*/

package service;

import java.util.List;

import play.api.templates.Html;
import play.api.templates.Template1;
import play.api.templates.Template2;
import com.google.inject.ImplementedBy;

@ImplementedBy(GMailService.class)
public interface Mailer<T extends Mailable> {
	
	/**
	 * Send an item by e-mail. The receiver will be <code>receivingAddress()</code>
	 * in <code>item</code>.
	 * 
	 * @param  item                 The item (a Mailable)
	 * @param  subject              The subject
	 * @param  Template1						The body template
	 * @return                      True on successful send, otherwise false
	 */
	public boolean send(T item, String subject, Template1<T, Html> template);

/**
 * Send a list of items to a receiver.
 * 
 * @param  receiver            A Mailable receiver
 * @param  items               The list of items
 * @param  subject             The subject
 * @param  template  					 The body template
 * @return                     True on successful send, otherwise false
 */
	public <S extends Mailable> boolean sendMany(
			S receiver, 
			List<T> items, 
			String subject, 
			Template2<S, List<T>, Html> template);
	
}