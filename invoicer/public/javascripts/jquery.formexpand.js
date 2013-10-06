	/**
	 * Quick jQuery plugin for dealing with showing/hiding a form
	 * with a sliding motion.
	 *
	 * Emits events when expanded/hiding and shown/hidden.
	 *
	 * 	$(".trigger-element").formExpand(".form-element", [options]);
	 *
	 * @author  Johan Brook
	 */
;(function($) {
	$.fn.formExpand = function(form, options) {
		var defaults = {
			close: ".close",
			speed: 200
		};

		var settings = $.extend({}, defaults, options),
				$self;

		var Events = {
			expand: function(evt) {
				evt.preventDefault();
				$(form).slideDown(settings.speed, $.proxy(function() {
					$(this).add(form).trigger("formexpand:shown")
				}, this));

				$(this).add(form).trigger("formexpand:expand");
			},

			close: function(evt) {
				evt.preventDefault();
				$(form).slideUp(settings.speed, $.proxy(function() {
					$(this).add(form).trigger("formexpand:hidden");
				}, this));

				$(this).add(form).trigger("formexpand:close");
			},

			toggleButton: function(evt) {
				$(this).toggle();
			}
		};

		return this.each(function() {
			// Bind events
			$(this).on("click", $.proxy(Events.expand, this));
			$(form).find(settings.close).on("click", $.proxy(Events.close, this));

			$(this).on("formexpand:hidden formexpand:expand", $.proxy(Events.toggleButton, this));
		});
	};

})(jQuery);