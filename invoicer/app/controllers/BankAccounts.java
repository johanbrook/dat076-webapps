package controllers;
import models.Invoice;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.helper.form;
import models.BankAccount;

@Security.Authenticated(Secured.class)
public class BankAccounts extends Controller{

	public BankAccounts() {
	}
	
	public static Result index() {
		//return ok("hej");
    	
		return ok(views.html.bankaccounts.index.render(BankAccount.find.all()));
		// Returns a Result
    }

}
