@(invoice : Invoice, editForm: Form[Invoice])

@if(editForm.hasErrors()) {
	Util.showError("Your form contains errors!");
} else {
	Util.showSuccess("Your invoice was updated!");
}