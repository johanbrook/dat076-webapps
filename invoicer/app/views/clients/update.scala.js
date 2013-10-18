@(client : Client, form : Form[Client])

@if(form.hasErrors()){
	Util.showError("There was an error in your form!");
} else {
	Util.showSuccess("Client updated!");
}