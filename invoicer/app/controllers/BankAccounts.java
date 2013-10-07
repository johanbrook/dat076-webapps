package controllers;
import models.Invoice;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.helper.form;
import models.BankAccount;

public class BankAccounts extends Controller{

	public static Form<BankAccount> form = Form.form(BankAccount.class);
	
	public BankAccounts() {
	}
	
	public static Result index() {
		//return ok("hej");
    	
		return ok(views.html.bankAccounts.index.render(BankAccount.find.all(), form));
		// Returns a Result
    }
	
	public static Result show(Long id) {
		//return ok("hej");
    	
		return ok(views.html.bankAccounts.show.render(BankAccount.find.byId(id)));
		// Returns a Result
    }
	
	public static Result edit(Long id) {
		BankAccount bankAccount = BankAccount.find.byId(id);
		Form<BankAccount> editForm = form.fill(bankAccount);
		
		return ok(views.html.bankAccounts.edit.render(bankAccount, editForm));
	}
	
	public static Result update(Long id) {
		BankAccount bankAccount = BankAccount.find.byId(id);
		Form<BankAccount> filledForm = form.bindFromRequest();
		
		if(filledForm.hasErrors()) {
			return badRequest(views.html.bankAccounts.edit.render(bankAccount, filledForm));
		}
		
		bankAccount.accountNumber = filledForm.get().accountNumber;
		bankAccount.bank = filledForm.get().bank;
	
		return goHome();
	}
	
	private static Result goHome() {
		return redirect(controllers.routes.BankAccounts.index());
	}

}
