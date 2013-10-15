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

	var Constants = {
		messageMap: {
			positive: "Success",
			notice: "Notice",
			negative: "Error"
		}
	};

	// Create 'show-' methods dynamically:

	$.each(["positive", "notice", "negative"], function(i, type) {
		methods["show" + Constants.messageMap[type.toLowerCase()]] = function(text) {
			new Notification(text, type).reveal();
		}
	});

	return exports = $.extend({}, methods, Constants);
})(window.Util);


// DOM Ready

$(function() {

	$("#add-client-btn").formExpand(".form-container");
	$("#add-bankaccount-btn").formExpand(".form-container");

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

