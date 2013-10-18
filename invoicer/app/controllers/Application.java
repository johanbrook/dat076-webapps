package controllers;

import models.AbstractModel;

import play.api.templates.Html;
import play.api.templates.JavaScript;
import play.api.templates.Template1;
import play.libs.Json;
import play.mvc.*;

public abstract class Application extends Controller {
	
	public static Result index() {
		return redirect(controllers.routes.Invoices.index());
	}
	
	/**
	 * Interface for returning proper content responses in controller actions.
	 * 
	 * @author Johan Brook
	 * @see respondTo
	 */
	public interface Responder {
		/**
		 * Respond as <code>text/html</code>
		 */
		public Result html();
		/**
		 * Respond as <code>application/json</code>
		 */
		public Result json();
		/**
		 * Respond as <code>application/javascript</code>
		 */
		public Result script();
	}
	
	/**
	 * Helper method for responding with the correct content representation of a
	 * <code>resource</code> for the server's <code>Accepts</code> header.
	 * 
	 * <p>
	 * This method is handy when only responding with a single <code>resource</code>. 
	 * Actions/views working with several resources may use <code>respondTo(Responder)</code>
	 * instead. Note that this method only returns results of type <code>OK</code>.
	 * </p>
	 * 
	 * @param resource The model resource
	 * @param html A reference to a view (ex. <code>index.ref()</code>)
	 * @return A <code>Result</code>
	 */
	protected static <T> Result respondTo(final T resource, final Template1<T, Html> html, final Template1<T, JavaScript> js) {
		return respondTo(new Responder() {
			
			@Override
			public Result json() {
				System.out.println("Rendering JSON ...");
				// Render using Play's built-in JSON renderer.
				return ok(Json.toJson(resource));
			}
			
			@Override
			public Result html() {
				System.out.println("Rendering HTML ...");
				// Let injected HTML view render the response.
				return ok(html.render(resource));
			}
			
			@Override
			public Result script() {
				System.out.println("Rendering JS ...");
				return ok(js.render(resource));
			}
		});
	}
  
	/**
	 * Returns the appropriate content response depending on the <code>Accepts</code>
	 * header in the request.
	 * 
	 * @param respond A <code>Responder</code> implementing the different content response formats
	 * (JSON, HTML)
	 * @return A <code>Result</code>
	 */
	protected static Result respondTo(Responder respond) {
		String types = request().acceptedTypes().toString();
		
		if(types.indexOf("application/json") != -1){
			return respond.json();
		}
		else if(types.indexOf("text/script") != -1 || types.indexOf("application/javascript") != -1) {
			return respond.script();
		}
		else {
			return respond.html();
		}
	}
	
	protected static <T extends AbstractModel> void setLocationHeader(T resource) {
		response().setHeader("Location", controllers.routes.Invoices.show(resource.id).absoluteURL(request()));
	}

}
