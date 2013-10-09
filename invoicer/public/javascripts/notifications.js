(function(root, $) {
	/**
	 * Constructor for a notification.
	 * 
	 * @param String text    The text to display
	 * @param String type    The type (class name to be applied)
	 * @param Object options Misc options
	 */
	function Notification(text, type, options) {
		var defaults = {
			element: "#notification",
			delay: 3000,
			slideIn: 200,
			slideOut: 400
		};

		if(typeof(type) === "object") {
			options = type;
			defaults.type = "notice";
		}
		else if(type !== undefined) {
			defaults.type = type;
		}
		else if(type === undefined) {
			defaults.type = "notice";
		}
		this.text = text;
		this.settings = $.extend({}, defaults, options);
		this.element = this.settings.element = retrieveOrCreateElement(this.settings.element);

		this.element.addClass(this.settings.type);
	}

	function retrieveOrCreateElement(selector) {
		if( $(selector).length === 0 ) {
			return $("<div />", {id: selector}).prependTo("body");
		}
		// Remember to clear any existing class attributes
		return $(selector).attr("class", "");
	}

	Notification.prototype = {

		reveal: function() {
			this.show();
			setTimeout($.proxy(this.hide, this), this.settings.delay);
		},

		show: function() {
			this.element.trigger("notification:show");
			this.element.text(this.text).hide().slideDown(this.settings.slideIn, function() {
				$(this).trigger("notification:shown");
			});

			this.element.on("click", $.proxy(this.hide, this));
		},

		hide: function() {
			this.element.trigger("notification:hide");
			var self = this;
			this.element.slideUp(this.settings.slideOut, function() {
				$(this).text("").trigger("notification:hidden");
			});

			// Unbind click listener
			this.element.off("click");
		}
	};

	// Export to global
	root.Notification = Notification;

})(window, jQuery);