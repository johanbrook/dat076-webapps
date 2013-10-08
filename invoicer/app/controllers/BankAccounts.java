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
final Form<BankAccount> filledForm = form.bindFromRequest();
		
		if(filledForm.hasErrors()) {
			
			return respondTo(new Responder() {
				
				@Override
				public Result json() {
					return badRequest();
				}
				
				@Override
				public Result html() {
					flash("error", "There were errors in your form.");
					return badRequest(index.render(BankAccount.find.all(), filledForm));
				}
			});
		}
		else {
			final BankAccount ba = filledForm.get();
			
			//in.owner = Session.getCurrentUser();
			//in.client = Client.find.byId( Long.parseLong( Form.form().bindFromRequest().get("client.id") ) );
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
			});
		}

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
