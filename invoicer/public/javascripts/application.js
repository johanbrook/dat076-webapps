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

Util = (function(util) {
	var exports = util || {};
	var methods = {
		template: function t(tmpl, data){
			for(var p in data)
				tmpl = tmpl.replace(new RegExp('{{'+p+'}}','g'), data[p]);
			return tmpl;
		}
	};

	return exports = methods;
})(window.Util);


// DOM Ready

$(function() {

	$("#add-invoice-btn").formExpand(".form-container");
	$("#add-client-btn").formExpand(".form-container");

	// Unbind the auto-generating listener when user starts
	// writing a custom title.
	$("#title").on("input", function() {
		$(".create-form").find("input, select").off("change");
	});

	updateNewInvoiceTitle();

	$(".create-form").find("input:not(#title), select").on("change", updateNewInvoiceTitle);
});

