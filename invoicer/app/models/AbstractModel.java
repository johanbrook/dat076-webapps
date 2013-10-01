/**
*	AbstractModel.java
*
*	A middle layer between Play's Model super class and the concrete
*	model classes in the application.
*
*	@author Johan Brook
*	@copyright (c) 2013 Johan Brook
*	@license MIT
*/

package models;

import javax.persistence.Id;

import play.db.ebean.Model;

public abstract class AbstractModel extends Model {

	@Id
	public Long id;
	
	
	
}
