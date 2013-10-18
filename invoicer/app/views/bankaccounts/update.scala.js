@(bankAccount : BankAccount, editForm: Form[BankAccount])

@if(editForm.hasErrors()) {
	Util.showError("Your form contains errors!");
} else {
	Util.showSuccess("Your account was updated!");
	$("main h1:first").text("Editing @bankAccount.accountNumber");
}