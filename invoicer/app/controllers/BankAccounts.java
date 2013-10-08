package controllers;
import controllers.Application.Responder;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import models.BankAccount;
import models.Client;
import models.Invoice;
import play.mvc.Security;

import views.html.bankaccounts.*;

@Security.Authenticated(Secured.class)
public class BankAccounts extends Application {

	public static Form<BankAccount> form = Form.form(BankAccount.class);

	public BankAccounts() {
	}

	public static Result index() {


		return ok(index.render(BankAccount.find.all(), form));

	}

	public static Result show(Long id) {


		return ok(show.render(BankAccount.find.byId(id)));

	}

	public static Result create(){
		return null;

	}

	public static Result edit(Long id) {
		BankAccount bankAccount = BankAccount.find.byId(id);
		Form<BankAccount> editForm = form.fill(bankAccount);

		return ok(edit.render(bankAccount, editForm));
	}

	public static Result update(Long id) {
		BankAccount bankAccount = BankAccount.find.byId(id);
		Form<BankAccount> filledForm = form.bindFromRequest();

		if(filledForm.hasErrors()) {
			return badRequest(edit.render(bankAccount, filledForm));
		}

		bankAccount.accountNumber = filledForm.get().accountNumber;
		bankAccount.bank = filledForm.get().bank;
		bankAccount.accountType = filledForm.get().accountType;
		bankAccount.iban = filledForm.get().iban;
		bankAccount.bic = filledForm.get().bic;

		bankAccount.update(id);

		return goHome();
	}

	private static Result goHome() {
		return redirect(controllers.routes.BankAccounts.index());
	}

}
