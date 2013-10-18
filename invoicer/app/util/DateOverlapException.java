/**
*	DateException.java
*
*	Throw this exception when a certain date (secondDate) is before another
*	date (firstDate).
*
*	@author Johan Brook
*	@copyright (c) 2013 Johan Brook
*	@license MIT
*/

package util;

import java.util.Date;

public class DateOverlapException extends RuntimeException{

	private Date firstDate;
	private Date secondDate;
	
	public DateOverlapException(Date firstDate, Date secondDate) {
		this.firstDate = firstDate;
		this.secondDate = secondDate;
	}
	
	public Date getFirstDate() {
		return this.firstDate;
	}
	
	public Date getSecondDate() {
		return this.secondDate;
	}
	
	@Override
	public String getMessage() {
		return this.secondDate.toString() + " can't be before " + this.firstDate.toString();
	}
}
