function autoGenerateInvoiceTitle(data) {
	return data.date.replace(/-/g, "")+"-"+data.client.toLowerCase();
}

function updateNewInvoiceTitle() {
	var data = {
		date: $("#invoice-date").val(),
		client: $("#client-dropdown option:selected").text()
	};

	$("#title").val(autoGenerateInvoiceTitle(data));
}

// DOM Ready

$(function() {

	//$("#add-client-btn").formExpand(".form-container");
	//$("#add-bankaccount-btn").formExpand(".form-container");

	// Unbind the auto-generating listener when user starts
	// writing a custom title.
	$("#title").on("input", function() {
		$(".create-form").find("input, select").off("change");
	});

	updateNewInvoiceTitle();

	$(".create-form").find("input:not(#title), select").on("change", updateNewInvoiceTitle);

	// Check if there are notice messages on the page
	// in order to show them with our own system:
	if($("#flash-message").length !== 0) {
		var self = $("#flash-message"),
				method = "show" + Util.messageMap[self.attr("class")]
		
		Util[method](self.text());
		self.hide();
	}
});

