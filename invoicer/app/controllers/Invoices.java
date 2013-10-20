/**
 *	InvoiceController.java
 *
 *	@author Johan Brook
 *	@copyright (c) 2013 Johan Brook
 *	@license MIT
 */

package controllers;

import play.*;

import util.DateOverlapException;
import util.FileHandler;
import util.FileUploadException;

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


import com.google.inject.Inject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Charsets;
import com.google.common.io.Files;

import controllers.routes.ref;

import models.Invoice;
import play.data.Form;
import play.libs.*;
import play.mvc.*;

import service.GMailService;
import service.Mailer;

import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Security.Authenticated;


import akka.actor.*;

public class Invoices extends Application {
	
	@Inject
	private Mailer<Invoice> mailer;

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
	
	/**
	 * GET /invoices/:id 
	 */
	@Security.Authenticated(Secured.class)
	public static Result show(Long id) {
		return respondTo(Invoice.find.byId(id), show.ref(), null);
	}
	
	/**
	 * GET /invoices/new 
	 */
	@Security.Authenticated(Secured.class)
	public static Result newInvoice() {
		return ok(new_invoice.render(new Invoice(), form));
	}
	
	/**
	 * POST /invoices/
	 */
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
					flash("fail", "There were errors in your form.");
					return badRequest(new_invoice.render(filledForm.get(), filledForm));
				}

				@Override
				public Result script() {
					return badRequest();
				}
			});
		} else {
			final Invoice in = filledForm.get();

			in.client = Client.find.byId(Long.parseLong(Form.form()
					.bindFromRequest().get("client.id")));

			in.bankAccount = BankAccount.find.byId(Long.parseLong(Form.form()
					.bindFromRequest().get("bankAccount.id")));

			in.setPaid(Form.form().bindFromRequest().get("ispaid") != null);

			try {
				in.save();
			}
			catch(DateOverlapException ex) {
				flash("fail", "Due date can't be before invoice date!");
				return badRequest(new_invoice.render(in, filledForm));
			}

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
	
	/**
	 * GET /invoices/:id/edit 
	 */
	@Security.Authenticated(Secured.class)
	public static Result edit(Long id) {
		Invoice invoice = Invoice.find.byId(id);
		Form<Invoice> editForm = form.fill(invoice);

		return ok(edit.render(invoice, editForm));
	}

	/**
	 * PUT /invoices/:id 
	 */
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
			Tedious updating of references ..
		 */
		Long clientId = filledForm.get().client.id;
		Long accountId = filledForm.get().bankAccount.id;
		
		invoice.setBankAccountFromId(accountId);
		invoice.setClientFromId(clientId);

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
	
	/**
	 * DELETE /invoices/:id 
	 */
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
	
	/**
	 * PUT /invoices/:id/star 
	 */
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

	/**
	 * GET /invoices/starred
	 */
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

	/**
	 * GET /invoices/paid
	 */
	@Security.Authenticated(Secured.class)
	public static Result paid() {
		final List<Invoice> paid = paidInvoicesOfCurrentUser();

		return respondTo(new Responder() {
			@Override
			public Result json() {
				return ok(Json.toJson(paid));
			}

			@Override
			public Result html() {
				return ok(index.render(paid, null, null));
			}

			@Override
			public Result script() {
				return noContent();
			}
		});
	}

	/**
	 * PUT /invoices/:id/paid 
	 */
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
	
	/**
	 * POST /invoices/:id/send 
	 */
	public Result sendInvoice(Long id) {
		
		final Invoice invoice = Invoice.find.byId(id);

		if (invoice != null) {
			if (!invoice.client.email.isEmpty()) {
				final boolean didSend = mailer.send(invoice, "You have a new invoice", views.html.mails.invoice.ref());
				
				return respondTo(new Responder() {
					
					@Override
					public Result script() {
						return (didSend) ? ok(views.js.invoices.send_invoice.render(invoice, didSend)) 
								: internalServerError(views.js.invoices.send_invoice.render(invoice, didSend));
					}
					
					@Override
					public Result json() {
						return (didSend) ? noContent() : internalServerError();
					}
					
					@Override
					public Result html() {
						if(didSend)
							flash("success", "The invoice: " + invoice.title + " was sent to the client: " + invoice.client.name);
						else
							flash("fail", "The invoice couldn't be sent");
						return goHome();
					}
				});
				
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
	
	/**
	 * POST /invoices/:id/reminder 
	 */
	public Result sendReminder(Long id) {

		final Invoice invoice = Invoice.find.byId(id);
		
		if (invoice != null) {
			if (!invoice.client.email.isEmpty()) {
				final boolean didSend = mailer.send(invoice, "You have an unpayed invoice", views.html.mails.reminder.ref());
				
				return respondTo(new Responder() {
					
					@Override
					public Result script() {
						return (didSend) ? ok(views.js.invoices.send_invoice.render(invoice, didSend)) 
								: internalServerError(views.js.invoices.send_invoice.render(invoice, didSend));
					}
					
					@Override
					public Result json() {
						return (didSend) ? noContent() : internalServerError();
					}
					
					@Override
					public Result html() {
						if(didSend)
							flash("success", "A reminder for the invoice : " + invoice.title + " was sent to the client: " + invoice.client.name);
						else
							flash("fail", "The invoice couldn't be sent");
						return goHome();
					}
				});
				
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

	/**
	 * (Action called from POST to invoices/upload)
	 * 
	 * Upload and parse a file to an invoice
	 * 
	 * @return
	 */
	@Security.Authenticated(Secured.class)
	public static Result upload() {
		
		final Invoice in;
		
		try {
			
			in = FileHandler.uploadModel(request(), Invoice.class);
		
		// Catch any errors with file upload
		} catch (final FileUploadException e) {
		
			return respondTo(new Responder() {
	
				@Override
				public Result json() {
					return badRequest();
				}
	
				@Override
				public Result html() {
					Logger.info("Upload error: " + e.getMessage());
					flash("fail", e.getMessage());
					return redirect(controllers.routes.Invoices.newInvoice());
				}
	
				@Override
				public Result script() {
					return badRequest();
				}
			});
		}
		
		/*
		 *  Upload successful, continue with invoice specific implementation details
		 */
				
		// Replace bank account if identical found in DB (persistance error otherwise)
		BankAccount dbBankAccount = BankAccount.find.where()
				.eq("accountNumber", in.bankAccount.accountNumber).findUnique();
		
		if(dbBankAccount != null) {
			in.bankAccount = dbBankAccount;
		}
		
		// Replace client if identical found in DB (persistance error otherwise)
		Client dbClient = Client.find.where()
				.eq("orgNumber", in.client.orgNumber).findUnique();
		
		if(dbClient != null) {
			in.client = dbClient;
		}
		
		in.save();
		
		return respondTo(new Responder() {

			@Override
			public Result json() {
				setLocationHeader(in);
				return created(Json.toJson(in));
			}

			@Override
			public Result html() {
				flash("success", "Invoice '" + in.title + "' created!");
				return goHome();
			}

			@Override
			public Result script() {
				return created(views.js.invoices.create.render(in));
			}
		});
		
	}
}
