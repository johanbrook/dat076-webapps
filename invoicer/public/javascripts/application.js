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
	var exports = util || {},

			Elements = {
				invoiceCounter: $("#invoices-total")
			};

	function getElementContentAsInteger(cb) {
		var value = parseInt(Elements.invoiceCounter.text());
		Elements.invoiceCounter.text(cb(value));
	}

	var methods = {
		template: function t(tmpl, data){
			for(var p in data)
				tmpl = tmpl.replace(new RegExp('{{'+p+'}}','g'), data[p]);
			return tmpl;
		},

		decrementInvoices: function() {
			getElementContentAsInteger(function(value) {
				return value - 1;
			});
		},
		incrementInvoices: function() {
			getElementContentAsInteger(function(value) {
				return value + 1;
			});
		}
	};

	// Create 'show-' methods dynamically:

	$.each(["Notice", "Success", "Error"], function(i, type) {
		// Map method types to notification classes:
		var map = {error: "negative", success: "positive", notice: "notice"};
		methods["show"+type] = function(text) {
			new Notification(text, map[type.toLowerCase()] ).reveal();
		}
	});

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

