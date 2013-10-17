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
	public boolean send(T item, String subject, Template1<T, Html> template);

	public <S extends Mailable> boolean sendMany(
			S receiver, 
			List<T> items, 
			String subject, 
			Template2<S, List<T>, Html> template);
	
}