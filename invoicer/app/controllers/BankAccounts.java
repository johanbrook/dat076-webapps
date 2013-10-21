package controllers;

import java.util.List;

import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import models.BankAccount;
import play.mvc.Security;

import util.FileHandler;
import util.FileUploadException;
import views.html.bankaccounts.*;


public class BankAccounts extends Application {

	public static Form<BankAccount> form = Form.form(BankAccount.class);

	public BankAccounts() {
	}

	/**
	 * GET /accounts 
	 */
	@Security.Authenticated(Secured.class)
	public static Result index() {

		return ok(index.render(bankAccountsOfCurrentUser(), form));

	}

	/**
	 * GET /accounts/new
	 */
	@Security.Authenticated(Secured.class)
	public static Result newAccount() {

		return ok(new_account.render(form));
	}

	/**
	 * GET /accounts/import
	 */
	@Security.Authenticated(Secured.class)
	public static Result newFromImport() {

		return ok(new_account_upload.render());
	}

	/**
	 * GET /accounts/:id 
	 */
	@Security.Authenticated(Secured.class)
	public static Result show(Long id) {

		return ok(show.render(BankAccount.find.byId(id)));

	}

	/**
	 * POST /accounts 
	 */
	@Security.Authenticated(Secured.class)
	public static Result create() {
		final Form<BankAccount> filledForm = form.bindFromRequest();
		final BankAccount ba = filledForm.get();

		if (filledForm.hasErrors() || !validate(ba.accountType, ba.accountNumber)) {

			flash("error", "There were errors in your form.");
			return badRequest(index.render(BankAccount.find.all(), filledForm));

		} else {

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

	/**
	 * GET /accounts/:id/edit 
	 */
	@Security.Authenticated(Secured.class)
	public static Result edit(Long id) {
		BankAccount bankAccount = BankAccount.find.byId(id);
		Form<BankAccount> editForm = form.fill(bankAccount);

		return ok(edit.render(bankAccount, editForm));
	}

	/**
	 * POST /accounts/:id 
	 */
	@Security.Authenticated(Secured.class)
	public static Result update(Long id) {
		final BankAccount bankAccount = BankAccount.find.byId(id);
		final Form<BankAccount> filledForm = form.bindFromRequest();

		// Validates if account type/account number matches in controller because 
		// annotation values in model can not change during runtime
		if (filledForm.hasErrors()|| !validate(filledForm.get().accountType, filledForm.get().accountNumber)) {

			return respondTo(new Responder() {
				@Override
				public Result json() {
					System.out.println("JSON");
					return badRequest();
				}

				@Override
				public Result html() {
					return badRequest(views.html.bankaccounts.edit.render(bankAccount,
							filledForm));
				}

				@Override
				public Result script() {
					Logger.info("FEL!!");
					return ok(views.js.bankaccounts.update.render(bankAccount, filledForm, false));
				}
			});
		}

		bankAccount.bank = filledForm.get().bank;
		bankAccount.iban = filledForm.get().iban;
		bankAccount.bic = filledForm.get().bic;
		bankAccount.accountNumber = filledForm.get().accountNumber;
		bankAccount.accountType = filledForm.get().accountType;

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
				Logger.info("RÄTT!!");
				return ok(views.js.bankaccounts.update.render(bankAccount, filledForm, true));
			}
		});
	}

	/**
	 * DELETE /accounts/:id 
	 */
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
	private static boolean validate(BankAccount.AccountType accountType, String accountNumber) {
		switch (accountType) {
		case PG:
			if (accountNumber.matches("[0-9]{1,6}-[0-9]{1}")) {

				return true;
			}
			break;
		case BG:
			if (accountNumber.matches("[0-9]{4}-[0-9]{4}")) {
				return true;
			}
			break;
		case BUSINESSACCOUNT:
			if (accountNumber.matches("[0-9]{4}(-[0-9]{0,1})[0-9]{0,9}(-[0-9]){0,1}")) {
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
	@Security.Authenticated(Secured.class)
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

		else if(!validate(ba.accountType, ba.accountNumber)) {
			return uploadError("The account number does not match the account type!");
		}

		ba.id = null;
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
