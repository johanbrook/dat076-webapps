@(invoices : List[Invoice], didSend : Boolean)

@if(didSend) {
	Util.showSuccess("@invoices.size() invoices were sent!");
} else {
	Util.showError("An error occurred when trying to send the invoices");
}