/**
 *	InvoiceController.java
 *
 *	@author Johan Brook
 *	@copyright (c) 2013 Johan Brook
 *	@license MIT
 */

package controllers;

import views.html.invoices.*;
import models.Client;
import models.BankAccount;
import java.util.*;

import com.google.inject.Inject;

import models.Invoice;
import play.data.Form;
import play.libs.*;
import play.mvc.*;
import service.GMailService;
import service.Mailer;

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

	
	public static Result invoicesByClient(final String client){
		
		return respondTo(new Responder() {

			@Override
			public Result json() {
				return ok(Json.toJson(Invoice.find.where().ieq("client.name", client).findList()));
			}

			@Override
			public Result html() {
				return badRequest();
			}

			@Override
			public Result script() {
				return noContent();
			}
		});
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
}
