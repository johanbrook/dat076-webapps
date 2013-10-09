package controllers;
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

		bankAccount.accountType = filledForm.get().accountType;
		bankAccount.bank = filledForm.get().bank;
		bankAccount.iban = filledForm.get().iban;
		bankAccount.bic = filledForm.get().bic;
		setAccountNumber(bankAccount, filledForm);
		
		bankAccount.update(id);

		return goHome();
	}
	
	public static Result destroy(Long id){
		BankAccount bankAccount = BankAccount.find.byId(id);
		
		if(bankAccount != null) {
			bankAccount.delete();
			flash("success", "The bankAccount was deleted.");
			return goHome();
		}
		else {
			return notFound(show.render(bankAccount));
		}
	}

	private static Result goHome() {
		return redirect(controllers.routes.BankAccounts.index());
	}
	
	private static void setAccountNumber(BankAccount bankAccount, Form<BankAccount> filledForm ){
		AccountType aType = bankAccount.accountType;
		String aNumber = filledForm.get().accountNumber;
		
		switch(aType){
		 case PG: if(aNumber.matches("[0-9]{1,6}(-[0-9]{1})")){
			 bankAccount.accountNumber = aNumber;
		 }
        break;
		 case BG: if(aNumber.matches("[0-9]{4}-[0-9]{4}")){
			 bankAccount.accountNumber = aNumber;
		 }
		 break;
		 case BUSINESSACCOUNT: if(aNumber.matches("[0-9]{4}(-[0-9]{1})[0-9]{0,9}(-[0-9]){0,1}")){
			 bankAccount.accountNumber = aNumber;
		 }
		 break;
		 default: 
        break;
		}
	}

}
