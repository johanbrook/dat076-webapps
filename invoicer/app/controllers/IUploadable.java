/**
 * 
 */
package controllers;

import java.io.Serializable;

import play.mvc.Result;

/**
 * @author Robin
 *
 */
public interface IUploadable extends Serializable{
	
	public Result upload();
	
}
