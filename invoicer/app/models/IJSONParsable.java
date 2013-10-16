/**
 * 
 */
package models;

import java.io.Serializable;

import com.fasterxml.jackson.databind.JsonNode;

import play.mvc.Result;

/**
 * @author Robin
 *
 */
public interface IJSONParsable <T extends IJSONParsable> extends Serializable {
	
	public boolean parseJSON(JsonNode jsonNode);
	
}
