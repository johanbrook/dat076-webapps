package controllers;

import java.util.List;

import controllers.Application.Responder;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import models.BankAccount;
import models.Client;
import models.Invoice;
import models.BankAccount.AccountType;
import play.mvc.Security;

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
		BankAccount bankAccount = BankAccount.find.byId(id);
		Form<BankAccount> filledForm = form.bindFromRequest();

		if (filledForm.hasErrors()) {
			return badRequest(edit.render(bankAccount, filledForm));
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

		return goHome();
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

}
