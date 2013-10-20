/**
 * jQuery plugin for attaching an alternate text when clicking
 * the button specified. Used for 'Send e-mail' buttons.
 */
$.fn.dynamicButtons = function(options) {
	var defaults = {
		activeTextAttribute: 'active-text',
		activeClass: 'sending'
	};
	var settings = $.extend({}, defaults, options);

	return this.each(function() {
		var $this = $(this),
				original = $this.text(),
				alt = $this.data(settings.activeTextAttribute);

		$this.on("click", function() {
			$this.text(alt).addClass(settings.activeClass).attr("disabled", true);
		});
		// Change back to original text on success
		$this.on("ajax:success", function() {
			$this.text(original).removeClass(settings.activeClass).attr("disabled", false);
		});
	});
};

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

	// Attach dynamic text plugin
	$(".dynamic-btn-text").dynamicButtons();

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
