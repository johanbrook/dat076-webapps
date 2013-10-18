/**
 *	ClientController.java
 *
 *	@author Andreas Rolén
 *	@copyright (c) 2013 Andreas Rolén
 *	@license MIT
 */

package controllers;

import java.util.List;

import com.avaje.ebean.Ebean;

import controllers.Application.Responder;

import models.BankAccount;
import models.Client;
import models.Invoice;
import play.Logger;
import play.data.Form;
import play.mvc.Result;
import play.mvc.Security;
import util.FileHandler;
import util.FileUploadException;
import views.html.clients.*;
import play.libs.Json;
import service.GMailService;
import service.Mailer;
import com.google.inject.Inject;

/**
 * Controller class for handling the Clients.
 * 
 * @author Andreas Rolén
 *
 */
@Security.Authenticated(Secured.class)
public class Clients extends Application {

	public static Form<Client> newForm = Form.form(Client.class);
	
	@Inject
	private Mailer<Invoice> mailer;

	/**
	 * GET /clients
	 */
	@Security.Authenticated(Secured.class)
	public static Result index() {
		return ok(index.render(Client.find.all(), newForm));
	}

	/**
	 * POST /clients
	 */
	@Security.Authenticated(Secured.class)
	public static Result create() {
		Form<Client> filledForm = newForm.bindFromRequest();

		if (filledForm.hasErrors()) {
			return badRequest(index.render(Client.find.all(), filledForm));

		} else {
			final Client client = filledForm.get();
			client.save();
			
			return respondTo(new Responder() {

				@Override
				public Result json() {
					setLocationHeader(client);
					return created(Json.toJson(client));
				}

				@Override
				public Result html() {
					flash("success", "Client was created!");
					return goHome();
				}

				@Override
				public Result script() {
					return created(views.js.clients.create.render(client));
				}
			});

		}

	}

	/**
	 * GET /clients/:id
	 */
	@Security.Authenticated(Secured.class)
	public static Result show(Long id) {
		return ok(show.render(Client.find.byId(id)));
	}

	/**
	 * GET /clients/:id/edit 
	 */
	@Security.Authenticated(Secured.class)
	public static Result edit(Long id) {
		Client client = Client.find.byId(id);
		Form<Client> form = newForm.fill(client);

		return ok(edit.render(client, form));
	}

	/**
	 * POST /clients/:id 
	 */
	@Security.Authenticated(Secured.class)
	public static Result update(Long id) {
		Client client = Client.find.byId(id);
		Form<Client> form = newForm.bindFromRequest();

		if (form.hasErrors()) {
			flash("fail", "The form has errors.");
			return badRequest(index.render(Client.find.all(), newForm));
		} else {
			client.name = form.get().name;
			client.address = form.get().address;
			client.country = form.get().country;
			client.postalCode = form.get().postalCode;
			client.orgNumber = form.get().orgNumber;
			client.contactPerson = form.get().contactPerson;
			client.email = form.get().email;

			client.update(id);

			flash("success", "The Client: " + client.name
					+ " was successfully updated");

			return goHome();
		}
	}

	/**
	 * DELETE /clients/:id
	 */
	@Security.Authenticated(Secured.class)
	public static Result destroy(Long id) {
		// Gets the client for the given ID and all invoices set to that client.
		final Client tmpClient = Client.find.byId(id);
		final List<Invoice> list = Invoice.find.where().eq("client_id", id).findList();

		// If the client got any invoices those must be deleted due to the dependency between a client and invoice. Must clean up manually.
		if (list != null) {
			Ebean.delete(list);
		}
		if (tmpClient != null) {
			tmpClient.delete();

			return respondTo(new Responder() {
				@Override
				public Result json() {
					return noContent();
				}

				@Override
				public Result html() {
					flash("success", "The client was deleted (along with "+list.size()+" invoices");
					return goHome();
				}

				@Override
				public Result script() {
					return ok(views.js.clients.destroy.render(tmpClient));
				}
			});
		} else {
			return badRequest(views.html.clients.index.render(
					Client.find.all(), newForm));
		}

	}

	/**
	 * POST /clients/:id/send
	 */
	public Result sendInvoices(Long id) {
		
		final Client client = Client.find.byId(id);
		// Gets a list of invoices owned by the current user and is set to the client with the given ID.
		final List<Invoice> invoiceList = Invoice.invoicesOfUser(Session.getCurrentUser().id)
				.where().eq("client_id", client.id).findList();
		
		if (client != null) {
			if (!invoiceList.isEmpty()) {
				final boolean didSend = mailer.sendMany(client, invoiceList, "You have new invoices", views.html.mails.invoiceList.ref());
				
				return respondTo(new Responder() {
					
					// Renders a js to show that the mail has been sent or not.
					@Override
					public Result script() {
						if(didSend) {
							return ok(views.js.invoices.send_invoices.render(invoiceList, didSend));
						}
						else {
							return internalServerError(views.js.invoices.send_invoices.render(invoiceList, didSend));
						}
					}
					
					@Override
					public Result json() {
						return noContent();
					}
					
					@Override
					public Result html() {
						if(didSend) {
							flash("success", "A mail has been sent to: " + client.name);
							return goHome();
						}
						else {
							flash("fail", "An error occurred when trying to send the e-mail");
							return internalServerError(index.render(Client.find.all(), newForm));
						}
						
					}
				});
				
			} else {
				return respondTo(new Responder() {
					
					@Override
					public Result script() {
						return badRequest("No invoices for client");
					}
					
					@Override
					public Result json() {
						return badRequest("No invoices for client");
					}
					
					@Override
					public Result html() {
						flash("fail", "The client doesn't own any invoices");
						return badRequest(index.render(Client.find.all(), newForm));
					}
				});
			}
		}
		
		return badRequest(index.render(Client.find.all(), newForm));
	}

	private static Result goHome() {
		return redirect(controllers.routes.Clients.index());
	}
	
	/**
	 * (Action called from POST to /clients/upload)
	 * 
	 * Upload and parse a file to a Client
	 * 
	 * @return
	 */
	public static Result upload() {
		
		final Client client;
		
		try {
			
			client = FileHandler.uploadModel(request(), Client.class);
		
		// Catch any errors with file upload
		} catch (final FileUploadException e) {
			
			return uploadError(e.getMessage());
		}
		
		if(client.find.where().eq("name", client.name).findUnique() != null) {
			
			return uploadError("Client with the name '" + client.name + "' already exist!");
		}
		
		client.id = null;
		
		client.save();
			
		
		return respondTo(new Responder() {

			@Override
			public Result json() {
				setLocationHeader(client);
				return created(Json.toJson(client));
			}

			@Override
			public Result html() {
				flash("success", "Client '" + client.name + "' added!");
				return goHome();
			}

			@Override
			public Result script() {
				return badRequest();
			}
		});
		
		
	}
	
	private static Result uploadError(final String message) {
		
		return respondTo(new Responder() {
			
			@Override
			public Result json() {
				return badRequest();
			}

			@Override
			public Result html() {
				Logger.info("Upload error: " + message);
				flash("error", message);
				return goHome();
			}

			@Override
			public Result script() {
				return badRequest();
			}
		});
		
	}
}
