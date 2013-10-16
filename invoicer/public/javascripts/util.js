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
		setCounterElement: function(element) {
			Elements.invoiceCounter = element;
		},

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
			return new Notification(text, type).reveal();
		}
	});

	return exports = $.extend({}, methods, Constants);
})(window.Util);