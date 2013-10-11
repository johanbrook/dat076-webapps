@(invoice : Invoice, editForm: Form[Invoice])

@if(editForm.hasErrors()) {
	Util.showError("Your form contains errors!");
} else {
	Util.showSuccess("Your invoice was updated!");
	$("main h1:first").text("Editing @invoice.title");
}