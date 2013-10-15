@(invoice : Invoice, didSend : Boolean)

@if(didSend) {
	Util.showSuccess("The email was sent to @invoice.client.name!");
} else {
	Util.showError("Couldn't send the e-mail");
}