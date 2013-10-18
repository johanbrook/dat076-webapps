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
 * Handles file uploading
 * 
 * (for now only JSON files allowed, could be expanded by allowing different
 * content types below)
 *
 */
public class FileHandler<T> {
	
	// Used to identify part of file in request
	public final static String FILE_PART_NAME = "file";
	
	/**
	 * Uploads and converts the file in request to the specified model
	 * @param request The request containing the file
	 * @param clazz
	 * @return The converted object
	 * @throws FileUploadException
	 */
	public static <T> T uploadModel(Request request, Class<T> clazz) throws FileUploadException {
		
		// Parse request body to java object
		MultipartFormData body = request.body().asMultipartFormData();
		FilePart filePart = body.getFile(FILE_PART_NAME);
		
		T model;
		
		if (filePart == null) {
			
			throw new FileUploadException("No file recieved");
		}
		
		else {
			
			File file = filePart.getFile();
			
			String content;
			
			try {
				content = Files.toString(file, Charsets.UTF_8);
			} catch (IOException e) {
				
				e.printStackTrace();
				throw new FileUploadException("Unable to convert file content to UTF_8");
			}
			
			if(filePart.getContentType().equals("application/json")) {
				
				try {
					
					JsonNode jsonNode = Json.parse(content);
					
					model = Json.fromJson(jsonNode, clazz);
					
				}  catch (RuntimeException e) {
					
					e.printStackTrace();
					throw new FileUploadException("Invalid JSON file");
					
				}
				
			// Could be expanded with more types
			} else {
				throw new FileUploadException("Only JSON files allowed");
			}
		}
		
		return model;
		
	}

}
