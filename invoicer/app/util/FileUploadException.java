/**
 * 
 */
package util;

/**
 * @author Robin
 *
 */
public class FileUploadException extends RuntimeException {
	
	private String message = null;
	 
    public FileUploadException() {
        super();
    }
 
    public FileUploadException(String message) {
        super(message);
        this.message = message;
    }
 
    public FileUploadException(Throwable cause) {
        super(cause);
    }
 
    @Override
    public String toString() {
        return message;
    }
 
    @Override
    public String getMessage() {
        return message;
    }

}
