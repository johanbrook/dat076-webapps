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
 * Handles file uploads
 * 
 * (for now only JSON files allowed, could be expanded by allowing different
 * content types below)
 *
 */
public class FileUploader<T> {
	
	public final static String FILE_PART_NAME = "file";
	
	public static <T> T uploadJSON(Request request, Class<T> clazz) {
		
		// Parse request body to java object
		MultipartFormData body = request.body().asMultipartFormData();
		FilePart filePart = body.getFile(FILE_PART_NAME);
		
		if (filePart != null) {
			
			// Accept only json file
			if(filePart.getContentType().equals("application/json")){
		
			
				File file = filePart.getFile();
				
				try {
					
					String content = Files.toString(file, Charsets.UTF_8);
					JsonNode jsonNode = Json.parse(content);
					
					return Json.fromJson(jsonNode, clazz);
					
				} catch (JsonParseException e) {
					e.printStackTrace();
					
				} catch (IOException e) {
					e.printStackTrace();
					
				// The above exceptions are not caught when thrown, why?
				} catch (RuntimeException e) {
					throw new FileUploadException("Invalid JSON file");
				}
			} else {
				throw new FileUploadException("Only JSON files allowed");
			}
			
		} else {
			throw new FileUploadException("No file recieved");
		}
		
		return null;
		
	}
	

}
