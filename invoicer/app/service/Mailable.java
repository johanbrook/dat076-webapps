/**
*	Mailable.java
*
*	Implementations of this class can be considered being 
*	"receivers" of e-mails.
*
*	@author Johan Brook
*	@copyright (c) 2013 Johan Brook
*	@license MIT
*/

package service;

public interface Mailable {
	
	/**
	 * The receiving address.
	 */
	public String getReceiverAddress();
}
