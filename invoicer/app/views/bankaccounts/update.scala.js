@(bankAccount : BankAccount, editForm: Form[BankAccount], validation: Boolean)

@if(editForm.hasErrors()|| !validation) {
	Util.showError("Your form contains errors!");
} else {
	Util.showSuccess("Your account was updated!");
	$("main h1:first").text("Editing @bankAccount.accountNumber");
}