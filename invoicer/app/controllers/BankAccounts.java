package controllers;

import java.util.List;

import controllers.Application.Responder;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import models.BankAccount;
import models.Client;
import models.Invoice;
import models.BankAccount.AccountType;
import play.mvc.Security;

import util.FileHandler;
import util.FileUploadException;
import views.html.bankaccounts.*;


public class BankAccounts extends Application {

	public static Form<BankAccount> form = Form.form(BankAccount.class);

	public BankAccounts() {
	}
	
	@Security.Authenticated(Secured.class)
	public static Result index() {

		return ok(index.render(bankAccountsOfCurrentUser(), form));

	}

	@Security.Authenticated(Secured.class)
	public static Result show(Long id) {

		return ok(show.render(BankAccount.find.byId(id)));

	}

	@Security.Authenticated(Secured.class)
	public static Result create() {
		final Form<BankAccount> filledForm = form.bindFromRequest();
		if (filledForm.hasErrors() || !validate(filledForm)) {

			flash("error", "There were errors in your form.");
			return badRequest(index.render(BankAccount.find.all(), filledForm));
			
		} else {
			final BankAccount ba = filledForm.get();
			
			ba.owner = Session.getCurrentUser();
			ba.save();

			return respondTo(new Responder() {

				@Override
				public Result json() {
					setLocationHeader(ba);
					return created(Json.toJson(ba));
				}

				@Override
				public Result html() {
					flash("success", "Bank account was created!");
					return goHome();
				}
				
				@Override
				public Result script() {
					return created(views.js.bankaccounts.create.render(ba));
				}
			});
		}

	}

	@Security.Authenticated(Secured.class)
	public static Result edit(Long id) {
		BankAccount bankAccount = BankAccount.find.byId(id);
		Form<BankAccount> editForm = form.fill(bankAccount);

		return ok(edit.render(bankAccount, editForm));
	}

	@Security.Authenticated(Secured.class)
	public static Result update(Long id) {
		final BankAccount bankAccount = BankAccount.find.byId(id);
		final Form<BankAccount> filledForm = form.bindFromRequest();

		if (filledForm.hasErrors()) {
			return respondTo(new Responder() {
				@Override
				public Result json() {
					return badRequest();
				}

				@Override
				public Result html() {
					flash("fail",
							"Something went wrong when trying to update the bank account");
					return badRequest(views.html.bankaccounts.edit.render(bankAccount,
							filledForm));
				}

				@Override
				public Result script() {
					return badRequest();
				}
			});
		}
		
		bankAccount.bank = filledForm.get().bank;
		bankAccount.iban = filledForm.get().iban;
		bankAccount.bic = filledForm.get().bic;
		
		// Validates in controller because annotation values in model can not change
		// during runtime
		if (validate(filledForm)) {
			bankAccount.accountNumber = filledForm.get().accountNumber;
			bankAccount.accountType = filledForm.get().accountType;
		}
		

		bankAccount.update(id);

		return respondTo(new Responder() {
			@Override
			public Result json() {
				setLocationHeader(bankAccount);
				return ok(Json.toJson(bankAccount));
			}

			@Override
			public Result html() {
				flash("success", "Bank account " + bankAccount.accountNumber + " was updated!");
				return redirect(controllers.routes.BankAccounts.show(bankAccount.id));
			}

			@Override
			public Result script() {
				return ok();
			}
		});
	}

	@Security.Authenticated(Secured.class)
	public static Result destroy(Long id) {
		final BankAccount bankAccount = BankAccount.find.byId(id);

		if (bankAccount != null) {
			bankAccount.delete();
			
			return respondTo(new Responder() {
				@Override
				public Result json() {
					return noContent();
				}

				@Override
				public Result html() {
					flash("success", "The bank account was deleted");
					return goHome();
				}

				@Override
				public Result script() {
					return ok(views.js.bankaccounts.destroy.render(bankAccount));
				}
			});
		} else {
			return notFound(show.render(bankAccount));
		}
	}
	
	private static List<BankAccount> bankAccountsOfCurrentUser() {
		return BankAccount.getBankAccountsOfUser(Session.getCurrentUser().id);
	}

	private static Result goHome() {
		return redirect(controllers.routes.BankAccounts.index());
	}

	// Method to validate account type with the account number
	private static boolean validate(Form<BankAccount> filledForm) {
		String aNumber = filledForm.get().accountNumber;
		switch (filledForm.get().accountType) {
		case PG:
			if (aNumber.matches("[0-9]{1,6}-[0-9]{1}")) {
				
				return true;
			}
			break;
		case BG:
			if (aNumber.matches("[0-9]{4}-[0-9]{4}")) {
				return true;
			}
			break;
		case BUSINESSACCOUNT:
			if (aNumber.matches("[0-9]{4}(-[0-9]{0,1})[0-9]{0,9}(-[0-9]){0,1}")) {
				return true;
			}
			break;
		default:
			return false;
		}
		return false;
	}
	
	/**
	 * (Action called from POST to bankaccounts/upload)
	 * 
	 * Upload and parse a file to an invoice
	 * 
	 * @return
	 */
	public static Result upload() {
		
		final BankAccount ba;
		
		try {
			
			ba = FileHandler.uploadModel(request(), BankAccount.class);
		
		// Catch any errors with file upload
		} catch (final FileUploadException e) {
			
			return uploadError(e.getMessage());
		}
		
		BankAccount dbBankAccount = BankAccount.find.where()
				.eq("accountNumber", ba.accountNumber).findUnique();
		
		if(dbBankAccount != null) {
			
			return uploadError("Bank account with that account number already exist!");
		}
		
		ba.id = null;
		
		ba.save();
			
		
		return respondTo(new Responder() {

			@Override
			public Result json() {
				setLocationHeader(ba);
				return created(Json.toJson(ba));
			}

			@Override
			public Result html() {
				flash("success", "Bank account '" + ba.accountNumber + "' added!");
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
