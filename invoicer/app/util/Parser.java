/**
 * 
 */
package util;

import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author Robin
 *
 */
public class Parser {
	
	/**
	 * Parses a JSON string to the specified model instance
	 * 
	 * @param content The JSON string
	 * @param clazz The object to parse the string to
	 * @return The parsed instance
	 * 
	 * @throws FileUploadException Thrown if parsing failed
	 */
	public static <T> T parseJSON(String json, Class<T> clazz) throws FileUploadException {
		
		try {
			
			JsonNode jsonNode = Json.parse(json);
			
			return Json.fromJson(jsonNode, clazz);
			
		}  catch (RuntimeException e) {
			
			e.printStackTrace();
			throw new FileUploadException("Invalid JSON file");
			
		}
		
	}

}
