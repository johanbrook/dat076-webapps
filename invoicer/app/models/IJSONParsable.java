/**
 * 
 */
package models;

import java.io.Serializable;
import java.text.ParseException;

import com.fasterxml.jackson.databind.JsonNode;

import play.mvc.Result;

/**
 * @author Robin
 *
 */
public interface IJSONParsable extends Serializable {
	
	public void parseJSON(JsonNode jsonNode) throws ParseException;
	
}
