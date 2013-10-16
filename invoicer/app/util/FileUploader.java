/**
 * 
 */
package util;

import java.io.File;
import java.io.IOException;

import models.Invoice;
import play.Logger;
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
	
	public static <T> T uploadJSON(Request request, Class<T> t, String filePartName) {
		
		// Parse request body to java object
		MultipartFormData body = request.body().asMultipartFormData();
		FilePart filePart = body.getFile(filePartName);
		
		// Accept only json file
		if (filePart != null && filePart.getContentType().equals("application/json")){
			
			File file = filePart.getFile();
			
			try {
				
				String content = Files.toString(file, Charsets.UTF_8);
				JsonNode jsonNode = Json.parse(content);
				
				return Json.fromJson(jsonNode, t);
				
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
