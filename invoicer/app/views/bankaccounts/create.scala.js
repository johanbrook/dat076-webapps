@(bankAccount : BankAccount, editForm: Form[BankAccount], validation: Boolean)

@if(editForm.hasErrors()|| !validation) {
	Util.showError("Your form contains errors!");
} else {
	Util.showSuccess("Your account was added!");
	
	var item = "@views.html.bankaccounts.row.render(bankAccount)";
	$(".accounts-table").prepend(item).find("tr:first").addClass("added");
}


