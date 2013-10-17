/**
 * 
 */
package util;

import java.io.File;
import java.io.IOException;

import play.libs.Json;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Http.Request;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Charsets;
import com.google.common.io.Files;

/**
 * @author Robin
 *
 */
public class FileUploader<T> {
	
	public final static String FILE_PART_NAME = "file";
	
	@SuppressWarnings("unchecked")
	public static <T> T uploadJSON(Request request, T t) {
		
		// Parse request body to java object
		MultipartFormData body = request.body().asMultipartFormData();
		FilePart filePart = body.getFile(FILE_PART_NAME);
		
		// Accept only json file
		if (filePart != null && filePart.getContentType().equals("application/json")){
			
			File file = filePart.getFile();
			
			try {
				
				String content = Files.toString(file, Charsets.UTF_8);
				JsonNode jsonNode = Json.parse(content);
				
				return (T) Json.fromJson(jsonNode, t.getClass());
				
			} catch (JsonParseException e) {
				e.printStackTrace();
				
			} catch (IOException e) {
				e.printStackTrace();
				
			// The above exceptions are not caught when thrown, why?
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
		
		return null;
		
	}
	

}
