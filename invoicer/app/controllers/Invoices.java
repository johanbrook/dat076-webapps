/**
 *	InvoiceController.java
 *
 *	@author Johan Brook
 *	@copyright (c) 2013 Johan Brook
 *	@license MIT
 */

package controllers;

import play.*;

import views.html.invoices.*;
import models.Client;
import models.BankAccount;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Charsets;
import com.google.common.io.Files;

import models.Invoice;
import play.api.templates.Html;
import play.data.Form;
import play.libs.*;
import play.mvc.*;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;

import akka.actor.*;

public class Invoices extends Application {

	public static Form<Invoice> form = Form.form(Invoice.class);

	private static List<Invoice> invoicesOfCurrentUser() {
		return Invoice.getInvoicesOfUser(Session.getCurrentUser().id);
	}

	private static List<Invoice> paidInvoicesOfCurrentUser() {
		return Invoice.invoicesOfUser(Session.getCurrentUser().id).where()
				.isNotNull("datePaid").findList();
	}

	private static List<Invoice> overdueInvoicesOfCurrentUser() {
		return Invoice.getOverdueInvoicesOfUser(Session.getCurrentUser().id);
	}

	public static Result toJSON(){
		return ok(Json.toJson(Invoice.find.all()));
	}
	
	
	@Security.Authenticated(Secured.class)
	public static Result index() {

		return respondTo(new Responder() {

			@Override
			public Result json() {
				return ok(Json.toJson(Invoice.find.all()));
			}

			@Override
			public Result html() {
				return ok(index.render(invoicesOfCurrentUser(),
						paidInvoicesOfCurrentUser(),
						overdueInvoicesOfCurrentUser()));
			}

			@Override
			public Result script() {
				return noContent();
			}
		});
    }
	
	@Security.Authenticated(Secured.class)
	public static Result show(Long id) {
		return respondTo(Invoice.find.byId(id), show.ref(), null);
	}

	@Security.Authenticated(Secured.class)
	public static Result newInvoice() {
		return ok(new_invoice.render(new Invoice(), form));
	}
	

	public static Result create() {
		final Form<Invoice> filledForm = form.bindFromRequest();

		if (filledForm.hasErrors()) {

			return respondTo(new Responder() {

				@Override
				public Result json() {
					return badRequest();
				}

				@Override
				public Result html() {
					flash("error", "There were errors in your form.");
					return badRequest(index.render(invoicesOfCurrentUser(),
							paidInvoicesOfCurrentUser(),
							overdueInvoicesOfCurrentUser()));
				}

				@Override
				public Result script() {
					return badRequest();
				}
			});
		} else {
			final Invoice in = filledForm.get();

			in.owner = Session.getCurrentUser();
			in.client = Client.find.byId(Long.parseLong(Form.form()
					.bindFromRequest().get("client.id")));

			in.bankAccount = BankAccount.find.byId(Long.parseLong(Form.form()
					.bindFromRequest().get("bankAccount.id")));

			in.setPaid(Form.form().bindFromRequest().get("ispaid") != null);

			in.save();

			return respondTo(new Responder() {

				@Override
				public Result json() {
					setLocationHeader(in);
					return created(Json.toJson(in));
				}

				@Override
				public Result html() {
					flash("success", "Invoice was created!");
					return goHome();
				}

				@Override
				public Result script() {
					return created(views.js.invoices.create.render(in));
				}
			});
		}

	}
	
	@Security.Authenticated(Secured.class)
	public static Result edit(Long id) {
		Invoice invoice = Invoice.find.byId(id);
		Form<Invoice> editForm = form.fill(invoice);

		return ok(edit.render(invoice, editForm));
	}

	@Security.Authenticated(Secured.class)
	public static Result update(Long id) {
		final Invoice invoice = Invoice.find.byId(id);
		final Form<Invoice> filledForm = form.bindFromRequest();

		if (filledForm.hasErrors()) {
			return respondTo(new Responder() {
				@Override
				public Result json() {
					return badRequest();
				}

				@Override
				public Result html() {
					flash("fail",
							"Something went wrong when trying to update the invoice");
					return badRequest(views.html.invoices.edit.render(invoice,
							filledForm));
				}

				@Override
				public Result script() {
					return badRequest(views.js.invoices.update.render(invoice,
							filledForm));
				}
			});
		}

		/*
		 * TODO: this doesn't work for now.
		 * 
		 * See:
		 * http://stackoverflow.com/questions/19177077/how-to-properly-update
		 * -a-model-with-nested-models-in-play-framework-2-2-0
		 */

		// invoice.client.id = filledForm.get().client.id;
		// invoice.bankAccount.id = filledForm.get().bankAccount.id;

		if (filledForm.get().title != null)
			invoice.title = filledForm.get().title;

		if (filledForm.get().dueDate != null)
			invoice.dueDate = filledForm.get().dueDate;

		invoice.setPaid(Form.form().bindFromRequest().get("ispaid") != null);

		invoice.update(id);

		return respondTo(new Responder() {
			@Override
			public Result json() {
				setLocationHeader(invoice);
				return ok(Json.toJson(invoice));
			}

			@Override
			public Result html() {
				flash("success", "Invoice " + invoice.title + " was updated!");
				return redirect(controllers.routes.Invoices.show(invoice.id));
			}

			@Override
			public Result script() {
				return ok(views.js.invoices.update.render(invoice, filledForm));
			}
		});
	}
	
	@Security.Authenticated(Secured.class)
	public static Result destroy(Long id) {
		final Invoice invoice = Invoice.find.byId(id);

		if (invoice != null) {
			invoice.delete();

			return respondTo(new Responder() {
				@Override
				public Result json() {
					return noContent();
				}

				@Override
				public Result html() {
					flash("success", "The invoice was deleted");
					return goHome();
				}

				@Override
				public Result script() {
					return ok(views.js.invoices.destroy.render(invoice));
				}
			});
		} else {
			return notFound(show.render(invoice));
		}
	}
	
	@Security.Authenticated(Secured.class)
	public static Result toggleStarred(Long id) {
		final Invoice invoice = Invoice.find.byId(id);

		if (invoice != null) {
			invoice.toggleStarred();
			invoice.update(invoice.id);

			return respondTo(new Responder() {
				@Override
				public Result json() {
					return noContent();
				}

				@Override
				public Result html() {
					return redirect(controllers.routes.Invoices
							.show(invoice.id));
				}

				@Override
				public Result script() {
					return ok(views.js.invoices.starred.render(invoice));
				}
			});
		}

		return notFound();
	}

	@Security.Authenticated(Secured.class)
	public static Result starred() {
		final List<Invoice> starred = Invoice
				.invoicesOfUser(Session.getCurrentUser().id).where()
				.eq("starred", true).findList();

		return respondTo(new Responder() {
			@Override
			public Result json() {
				return ok(Json.toJson(starred));
			}

			@Override
			public Result html() {
				return ok(index.render(starred, null, null));
			}

			@Override
			public Result script() {
				return noContent();
			}
		});
	}

	public static Result setPaid(Long id) {
		Invoice invoice = Invoice.find.byId(id);
		// Fetch reference to Akka actor and send event
		final ActorRef actor = Events.actorInstance;

		if(invoice != null) {
			if(!invoice.isPaid()) {
				invoice.setPaid();
				invoice.save();
				
				actor.tell(Json.toJson(invoice), null);
				return ok();
							
			}
			else {
				return noContent();
			}
		}

		return notFound();
	}
	
	public static Result sendInvoice(Long id) {
		final Form<Invoice> filledForm = form.bindFromRequest();

		Invoice invoice = Invoice.find.byId(id);

		if (invoice != null) {
			if (!invoice.client.email.isEmpty()) {
				MailController.sendOneInvoice(invoice);
				flash("success", "The invoice: " + invoice.title + " was sent to the client: " + invoice.client.name);
				return goHome();
			} else {
				flash("fail", "The client for this invoice don't got any email");
				return badRequest(index.render(invoicesOfCurrentUser(),
						paidInvoicesOfCurrentUser(),
						overdueInvoicesOfCurrentUser()));
			}
		} else {
			return noContent();
		}
	}
	
	public static Result sendReminder(Long id) {
		final Form<Invoice> filledForm = form.bindFromRequest();

		Invoice invoice = Invoice.find.byId(id);
		
		if (invoice != null) {
			if (!invoice.client.email.isEmpty()) {
				MailController.sendReminder(invoice);
				flash("success", "A reminder for the invoice : " + invoice.title + " was sent to the client: " + invoice.client.name);
				return goHome();
			} else {
				flash("fail", "The client for this invoice don't got any email");
				return badRequest(index.render(invoicesOfCurrentUser(),
						paidInvoicesOfCurrentUser(),
						overdueInvoicesOfCurrentUser()));
			}
		} else {
			return noContent();
		}
	}

	private static Result goHome() {
		return redirect(controllers.routes.Invoices.index());
	}

	
	public static Result upload() {
		
		// Parse request body to java object
		MultipartFormData body = request().body().asMultipartFormData();
		
		FilePart invoice = body.getFile("invoice");
		
		if (invoice != null) {
			String fileName = invoice.getFilename();
			String contentType = invoice.getContentType();
			File file = invoice.getFile();
			
			try {
				
				String content = Files.toString(file, Charsets.UTF_8);
				JsonNode jsonNode = Json.parse(content);
				
				final Invoice in = new Invoice(jsonNode);
				in.save();
				
				return respondTo(new Responder() {

					@Override
					public Result json() {
						setLocationHeader(in);
						return created(Json.toJson(in));
					}

					@Override
					public Result html() {
						flash("success", "Invoice was created!");
						return goHome();
					}

					@Override
					public Result script() {
						return created(views.js.invoices.create.render(in));
					}
				});
				
			} catch (JsonParseException e) {
				flash("error", "Couldn't parse file to Json"); 
				
			} catch (IOException e) {
				flash("error", "Couldn't read file");
				
			// The above exceptions are not caught, why?
			} catch (RuntimeException e) {
				Logger.info("Runtime failed");
				e.printStackTrace();
				flash("error", "Upload failed");
				
			} catch (ParseException e) {
				Logger.info("Parsing file failed");
				e.printStackTrace();
				flash("error", "Error reading file");
			}
			
		} else {
			flash("error", "Missing file");
		}
		
		return respondTo(new Responder() {

			@Override
			public Result json() {
				return badRequest();
			}

			@Override
			public Result html() {
				return redirect(controllers.routes.Invoices.newInvoice());
			}

			@Override
			public Result script() {
				return badRequest();
			}
		});
		
	}
}
