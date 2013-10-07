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
		/*Invoice invoice = Invoice.find.byId(id);
		Form<Invoice> filledForm = form.bindFromRequest();
		
		if(filledForm.hasErrors()) {
			return badRequest(views.html.invoices.edit.render(invoice, filledForm));
		}
		
		invoice.title = filledForm.get().title;
		/*
		 * TODO: this doesn't work for now.
		 * 
		 *  See: http://stackoverflow.com/questions/19177077/how-to-properly-update-a-model-with-nested-models-in-play-framework-2-2-0
		 */
		
//		invoice.client.id = filledForm.get().client.id;
		
		/*invoice.title = filledForm.get().title;
		invoice.dueDate = filledForm.get().dueDate;
		
		invoice.setPaid( Form.form().bindFromRequest().get("ispaid") != null );
		
		invoice.update(id);

		flash("success", "Invoice " + invoice.title + " was updated!");
		*/
		return null; //goHome();
	}

}
